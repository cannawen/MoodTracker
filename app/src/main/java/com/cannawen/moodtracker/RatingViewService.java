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
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RatingViewService extends Service {
    private WindowManager mWindowManager;
    private View moodRatingView;
    private List<String> moods = Arrays.asList("Pain", "Mood", "Productivity", "Energy");

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
                System.currentTimeMillis() + (1000 * 60 * 60),
                PendingIntent.getService(this, 0, new Intent(this, RatingViewService.class), 0)
        );
    }

    private boolean logMoodToDisk(String mood, String rating) {
        return SaveUtility.saveMood(mood, rating);
    }

    private void handleSuccessfulLog() {
        stopSelf();
    }

    private void handleFailedLogAttempt() {

    }

    private void initializeViews() {
        moodRatingView = LayoutInflater.from(this).inflate(R.layout.mood_rating_view, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(moodRatingView, params);

        String mood = moods.get(new Random().nextInt(moods.size()));

        TextView moodText = moodRatingView.findViewById(R.id.mood_rating_description);
        moodText.setText(mood);

        registerButton(R.id.mood_rating_positive, mood, "+1");
        registerButton(R.id.mood_rating_neutral, mood, "0");
        registerButton(R.id.mood_rating_negative, mood, "-1");

        moodRatingView.findViewById(R.id.mood_rating_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
    }

    private void registerButton(@IdRes int resId, final String mood, final String rating) {
        moodRatingView.findViewById(resId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logMoodToDisk(mood, rating)) {
                    handleSuccessfulLog();
                } else {
                    handleFailedLogAttempt();
                }
            }
        });
    }
}
