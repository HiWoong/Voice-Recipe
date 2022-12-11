package com.penelope.acousticrecipe;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AcousticRecipeApplication extends Application {

    public static final String CHANNEL_NAME_TIMER_FOREGROUND = "조리 타이머";
    public static final String CHANNEL_ID_TIMER_FOREGROUND = "com.penelope.acousticrecipe.timer_foreground";

    public static final String CHANNEL_NAME_TIMER_END = "조리 타이머.";
    public static final String CHANNEL_ID_TIMER_END = "com.penelope.acousticrecipe.timer_end";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {

        NotificationChannel timerForegroundChannel = new NotificationChannel(
                CHANNEL_ID_TIMER_FOREGROUND,
                CHANNEL_NAME_TIMER_FOREGROUND,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationChannel timerEndChannel = new NotificationChannel(
                CHANNEL_ID_TIMER_END,
                CHANNEL_NAME_TIMER_END,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(timerForegroundChannel);
        manager.createNotificationChannel(timerEndChannel);
    }

}







