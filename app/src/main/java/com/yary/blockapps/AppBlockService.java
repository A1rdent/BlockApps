package com.yary.blockapps;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppBlockService extends Service {
    private static final String TAG = "AppBlockService";
    private Timer monitoringTimer;
    private Handler mainHandler;
    private List<ActiveTimer> activeTimers;

    @Override
    public void onCreate() {
        super.onCreate();
        mainHandler = new Handler(Looper.getMainLooper());
        startMonitoring();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopMonitoring();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateTimers(List<ActiveTimer> timers) {
        this.activeTimers = timers;
    }

    private void startMonitoring() {
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
        }

        monitoringTimer = new Timer();
        monitoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndBlockApps();
            }
        }, 0, 2000); // Проверяем каждые 2 секунды
    }

    private void stopMonitoring() {
        if (monitoringTimer != null) {
            monitoringTimer.cancel();
            monitoringTimer = null;
        }
    }

    private void checkAndBlockApps() {
        try {
            if (activeTimers == null || activeTimers.isEmpty()) {
                return;
            }

            for (ActiveTimer timer : activeTimers) {
                if (shouldBlockApp(timer)) {
                    blockApplication(timer);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in monitoring task", e);
        }
    }

    private boolean shouldBlockApp(ActiveTimer timer) {
        return timer != null && 
               timer.isRunning() && 
               timer.calculateRemainingTime() <= 0;
    }

    private void blockApplication(ActiveTimer timer) {
        mainHandler.post(() -> {
            try {
                if (isAppRunning(timer.getPackageName())) {
                    Log.d(TAG, "Blocking app: " + timer.getAppName());
                    
                    Intent blockIntent = new Intent(this, BlockActivity.class);
                    blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    blockIntent.putExtra("blocked_package", timer.getPackageName());
                    blockIntent.putExtra("blocked_app_name", timer.getAppName());
                    startActivity(blockIntent);
                    
                    // Останавливаем таймер после блокировки
                    timer.setRunning(false);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error blocking app: " + timer.getPackageName(), e);
            }
        });
    }

    private boolean isAppRunning(String packageName) {
        try {
            PackageManager pm = getPackageManager();
            return pm.getLaunchIntentForPackage(packageName) != null;
        } catch (Exception e) {
            return false;
        }
    }
}