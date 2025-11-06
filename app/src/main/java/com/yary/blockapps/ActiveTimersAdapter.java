package com.yary.blockapps;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActiveTimersAdapter extends RecyclerView.Adapter<ActiveTimersAdapter.TimerViewHolder> {

    private final List<ActiveTimer> timersList;
    private final OnTimerRemoveListener removeListener;
    private Timer updateTimer;
    private final Handler mainHandler;

    public interface OnTimerRemoveListener {
        void onTimerRemove(ActiveTimer timer);
    }

    public ActiveTimersAdapter(List<ActiveTimer> timersList, OnTimerRemoveListener removeListener) {
        this.timersList = timersList != null ? timersList : new ArrayList<>();
        this.removeListener = removeListener;
        this.mainHandler = new Handler(Looper.getMainLooper());
        startUpdateTimer();
    }

    // Запускаем таймер для обновления времени каждую секунду
    private void startUpdateTimer() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Обновляем UI в главном потоке
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getItemCount() > 0) {
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }, 0, 1000); // Обновляем каждую секунду
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_timer, parent, false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        try {
            if (position < 0 || position >= timersList.size()) {
                return;
            }
            
            ActiveTimer timer = timersList.get(position);
            if (timer == null) return;
            
            // Обновляем время
            holder.appNameTextView.setText(timer.getAppName() != null ? timer.getAppName() : "Unknown App");
            holder.timeTextView.setText(timer.getFormattedTime() != null ? timer.getFormattedTime() : "00:00");
            holder.initialTimeTextView.setText("Установлено: " + 
                (timer.getInitialTimeFormatted() != null ? timer.getInitialTimeFormatted() : "00:00"));
            
            // Проверяем истек ли таймер
            if (timer.isExpired()) {
                holder.timeTextView.setText("Время истекло");
                holder.timeTextView.setTextColor(0xFFFF0000); // Красный цвет
            } else {
                holder.timeTextView.setTextColor(0xFF000000); // Черный цвет
            }
            
            holder.removeButton.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onTimerRemove(timer);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("ActiveTimersAdapter", "Error in onBindViewHolder: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return timersList.size();
    }

    // Останавливаем таймер при уничтожении адаптера
    public void cleanup() {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }

    public static class TimerViewHolder extends RecyclerView.ViewHolder {
        TextView appNameTextView;
        TextView timeTextView;
        TextView initialTimeTextView;
        ImageButton removeButton;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.tv_app_name);
            timeTextView = itemView.findViewById(R.id.tv_time);
            initialTimeTextView = itemView.findViewById(R.id.tv_initial_time);
            removeButton = itemView.findViewById(R.id.btn_remove);
        }
    }
}