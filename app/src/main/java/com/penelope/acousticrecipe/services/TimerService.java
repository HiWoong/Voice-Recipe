package com.penelope.acousticrecipe.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.penelope.acousticrecipe.AcousticRecipeApplication;
import com.penelope.acousticrecipe.R;
import com.penelope.acousticrecipe.ui.MainActivity;

import java.util.Locale;

public class TimerService extends Service {

    public static boolean isStarted = false;

    public static final int NOTIFICATION_ID_TIMER = 100;
    public static final String ACTION_PLAY_PAUSE = "com.penelope.acousticrecipe.action_play_pause";
    public static final String ACTION_DISMISS = "com.penelope.acousticrecipe.action_dismiss";
    public static final String ACTION_COUNTDOWN = "com.penelope.acousticrecipe.action_countdown";
    public static final String ACTION_ALARM = "com.penelope.acousticrecipe.action_alarm";
    public static final String ACTION_RECOGNIZE = "com.penelope.acousticrecipe.action_recognize";

    private String foodName;
    private String manual;

    private int seconds;
    private boolean isPlaying;
    private boolean isDismissed;
    private boolean isRecognized;

    private BroadcastReceiver playPauseReceiver;
    private BroadcastReceiver stopReceiver;
    private BroadcastReceiver countdownReceiver;
    private BroadcastReceiver alarmReceiver;
    private BroadcastReceiver recognizeReceiver;

    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        playPauseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isPlaying = !isPlaying;
                notificationManager.notify(NOTIFICATION_ID_TIMER, createTimerNotification());
            }
        };
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isDismissed = true;
            }
        };
        countdownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                seconds--;
                if (seconds >= 0) {
                    notificationManager.notify(NOTIFICATION_ID_TIMER, createTimerNotification());
                }
            }
        };

        alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notificationManager.notify(NOTIFICATION_ID_TIMER, createTimerEndNotification());
            }
        };

        recognizeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isRecognized = true;
                notificationManager.cancel(NOTIFICATION_ID_TIMER);
            }
        };

        registerReceiver(playPauseReceiver, new IntentFilter(ACTION_PLAY_PAUSE));
        registerReceiver(stopReceiver, new IntentFilter(ACTION_DISMISS));
        registerReceiver(countdownReceiver, new IntentFilter(ACTION_COUNTDOWN));
        registerReceiver(alarmReceiver, new IntentFilter(ACTION_ALARM));
        registerReceiver(recognizeReceiver, new IntentFilter(ACTION_RECOGNIZE));

        isStarted = true;
    }

    @Override
    public void onDestroy() {

        isStarted = false;

        unregisterReceiver(playPauseReceiver);
        unregisterReceiver(stopReceiver);
        unregisterReceiver(countdownReceiver);
        unregisterReceiver(alarmReceiver);
        unregisterReceiver(recognizeReceiver);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int minutes = intent.getIntExtra("minutes", 10);
        seconds = minutes * 60;
        foodName = intent.getStringExtra("food_name");
        manual = intent.getStringExtra("manual");
        isPlaying = true;

        startForeground(NOTIFICATION_ID_TIMER, createTimerNotification());

        new Thread(() -> {
            long millisSaved = -1;
            long millisAccumulated = 0;

            while (seconds >= 0 && !isDismissed) {
                if (isPlaying && millisSaved > 0) {
                    millisAccumulated += (System.currentTimeMillis() - millisSaved);
                    if (millisAccumulated >= 1000) {
                        millisAccumulated = 0;
                        sendBroadcast(new Intent(ACTION_COUNTDOWN));
                    }
                }
                millisSaved = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isDismissed) {
                stopSelf();
                return;
            }

            while (!isRecognized) {
                sendBroadcast(new Intent(ACTION_ALARM));

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopSelf();

        }).start();

        return START_STICKY;
    }

    private Notification createTimerNotification() {

        Notification notification;

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.view_timer);

        String strTimeLeft = String.format(Locale.getDefault(), "%02d : %02d", seconds / 60, seconds % 60);
        rv.setTextViewText(R.id.textViewTimeLeft, strTimeLeft);
        rv.setTextViewText(R.id.textViewFoodName, foodName);
        rv.setTextViewText(R.id.textViewManual, manual);

        rv.setImageViewResource(R.id.imageViewPlayPause, isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

        rv.setOnClickPendingIntent(R.id.imageViewPlayPause, getPendingIntent(ACTION_PLAY_PAUSE));
        rv.setOnClickPendingIntent(R.id.imageViewStop, getPendingIntent(ACTION_DISMISS));

        notification = new Notification.Builder(this, AcousticRecipeApplication.CHANNEL_ID_TIMER_FOREGROUND)
                .setContentIntent(pendingIntent)
                .setCustomContentView(rv)
                .setCustomBigContentView(rv)
                .setSmallIcon(R.drawable.ic_timer_notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build();

        return notification;
    }

    private Notification createTimerEndNotification() {

        Notification notification;

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.view_timer_end);
        rv.setTextViewText(R.id.textViewFoodName, foodName);
        rv.setTextViewText(R.id.textViewRecognize, "확인");
        rv.setOnClickPendingIntent(R.id.textViewRecognize, getPendingIntent(ACTION_RECOGNIZE));

        notification = new Notification.Builder(this, AcousticRecipeApplication.CHANNEL_ID_TIMER_END)
                .setContentIntent(getPendingIntent(ACTION_RECOGNIZE))
                .setCustomBigContentView(rv)
                .setSmallIcon(R.drawable.ic_timer_notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build();

        return notification;
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(action);
        return PendingIntent.getBroadcast(this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    // 바인딩

    public class TimerServiceBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    private final IBinder binder = new TimerServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getManual() {
        return manual;
    }

}
