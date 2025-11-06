package com.yary.blockapps;

import java.util.Locale;

public class ActiveTimer {
    private final String appName;
    private final String packageName;
    private final long timeInMillis;
    private final long startTime;
    private boolean isRunning;

    public ActiveTimer(String appName, String packageName, long timeInMillis) {
        this.appName = appName;
        this.packageName = packageName;
        this.timeInMillis = timeInMillis;
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFormattedTime() {
        try {
            long remainingTime = calculateRemainingTime();
            if (remainingTime <= 0) {
                return "00:00:00";
            }
            
            long hours = (remainingTime / (1000 * 60 * 60)) % 24;
            long minutes = (remainingTime / (1000 * 60)) % 60;
            long seconds = (remainingTime / 1000) % 60;
            
            if (hours > 0) {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            return "00:00";
        }
    }

    public String getInitialTimeFormatted() {
        try {
            long totalSeconds = timeInMillis / 1000;
            long hours = (totalSeconds / 3600) % 24;
            long minutes = (totalSeconds / 60) % 60;
            long seconds = totalSeconds % 60;
            
            if (hours > 0) {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            return "00:00";
        }
    }

    public long calculateRemainingTime() {
        try {
            long elapsedTime = System.currentTimeMillis() - startTime;
            return Math.max(0, timeInMillis - elapsedTime);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isExpired() {
        return calculateRemainingTime() <= 0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    // Новый метод для получения оставшегося времени в секундах (для отладки)
    public long getRemainingSeconds() {
        return calculateRemainingTime() / 1000;
    }
}