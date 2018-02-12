package com.cannawen.moodtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by canna on 2018-02-11.
 */

public class RatingViewService extends Service {
    private WindowManager mWindowManager;
    private View moodRatingView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeViews();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (moodRatingView != null) {
            mWindowManager.removeView(moodRatingView);
        }
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (3 * 1000 * 60 * 60),
                PendingIntent.getService(this, 0, new Intent(this, RatingViewService.class), 0)
        );
    }

    private boolean logMoodToDisk(String mood) {
        return SaveUtility.saveMood(mood);
    }

    private void handleSuccessfulLog() {
        stopSelf();
    }

    private void handleFailedLogAttempt() {

    }

    private void initializeViews() {
        moodRatingView = LayoutInflater.from(this).inflate(R.layout.mood_rating_view, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(moodRatingView, params);

        registerButton(R.id.mood_rating_positive, "positive");
        registerButton(R.id.mood_rating_neutral, "neutral");
        registerButton(R.id.mood_rating_negative, "negative");

        moodRatingView.findViewById(R.id.mood_rating_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
    }

    private void registerButton(@IdRes int resId, final String mood) {
        moodRatingView.findViewById(resId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logMoodToDisk(mood)) {
                    handleSuccessfulLog();
                } else {
                    handleFailedLogAttempt();
                }
            }
        });
    }
}
