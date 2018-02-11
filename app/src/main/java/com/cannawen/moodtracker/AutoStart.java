package com.cannawen.moodtracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by canna on 2018-02-11.
 */

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RatingViewService.class));
    }
}