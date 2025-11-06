package com.yary.blockapps;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppMonitoringService extends Service {
    private static final String TAG = "AppMonitoringService";
    private Timer monitoringTimer;
    private Handler mainHandler;
    private static List<ActiveTimer> activeTimers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mainHandler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "Monitoring service created");
        startMonitoring();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Monitoring service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopMonitoring();
        Log.d(TAG, "Monitoring service destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void updateTimers(List<ActiveTimer> timers) {
        if (timers != null) {
            activeTimers = new ArrayList<>(timers);
            Log.d(TAG, "Timers updated, count: " + activeTimers.size());
            
            for (ActiveTimer timer : activeTimers) {
                Log.d(TAG, "Timer: " + timer.getAppName() + " (" + timer.getPackageName() + ") - " + 
                      timer.getFormattedTime() + " remaining");
            }
        }
    }

    private void startMonitoring() {
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
        }

        monitoringTimer = new Timer();
        monitoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkExpiredTimers();
            }
        }, 0, 2000); // Проверяем каждые 2 секунды
    }

    private void stopMonitoring() {
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
            monitoringTimer = null;
        }
    }

    private void checkExpiredTimers() {
        try {
            if (activeTimers == null || activeTimers.isEmpty()) {
                return;
            }

            boolean foundExpired = false;
            
            for (ActiveTimer timer : activeTimers) {
                if (timer != null && timer.isRunning() && timer.calculateRemainingTime() <= 0) {
                    Log.d(TAG, "Timer expired for: " + timer.getAppName());
                    foundExpired = true;
                    
                    // Показываем блокировку для всех просроченных таймеров
                    mainHandler.post(() -> showBlockScreen(timer));
                }
            }
            
            if (foundExpired) {
                Log.d(TAG, "Found expired timers, showing block screen");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in monitoring task", e);
        }
    }

    private void showBlockScreen(ActiveTimer timer) {
        try {
            Log.d(TAG, "Showing block screen for: " + timer.getAppName());
            
            Intent blockIntent = new Intent(this, BlockActivity.class);
            blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                               Intent.FLAG_ACTIVITY_CLEAR_TOP |
                               Intent.FLAG_ACTIVITY_SINGLE_TOP);
            blockIntent.putExtra("blocked_package", timer.getPackageName());
            blockIntent.putExtra("blocked_app_name", timer.getAppName());
            blockIntent.putExtra("remaining_time", timer.calculateRemainingTime());
            
            startActivity(blockIntent);
            
            // Останавливаем таймер после показа экрана блокировки
            timer.setRunning(false);
            
            Log.d(TAG, "Block screen shown for: " + timer.getAppName());
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing block screen: " + e.getMessage(), e);
        }
    }
}