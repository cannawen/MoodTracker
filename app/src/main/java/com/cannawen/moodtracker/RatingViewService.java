package com.cannawen.moodtracker;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

/**
 * Created by canna on 2018-02-11.
 */

public class RatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;

    public RatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.mood_rating_view, null);

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
        mWindowManager.addView(mChatHeadView, params);

        //Set the close button.
        registerButton(R.id.mood_rating_positive);
        registerButton(R.id.mood_rating_neutral);
        registerButton(R.id.mood_rating_negative);

        mChatHeadView.findViewById(R.id.mood_rating_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
    }

    private void registerButton(@IdRes int resId) {
        mChatHeadView.findViewById(resId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appFolderExists()) {
                    handleSuccessfulLog();
                } else {
                    handleFailedLogAttempt();
                }
            }
        });
    }

    private void handleSuccessfulLog() {
        stopSelf();
    }

    private void handleFailedLogAttempt() {

    }

    private boolean appFolderExists() {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "MoodTracker");
        return folder.exists() || folder.mkdirs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }
}
