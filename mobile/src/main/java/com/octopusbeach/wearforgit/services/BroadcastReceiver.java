package com.octopusbeach.wearforgit.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by hudson on 6/25/15.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver{

    private static final String TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the notification service.
        Log.d(TAG, "Starting notification service");

        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            cancelAlarm(context);
            setAlarm(context);
        }

        context.startService(new Intent(context, NotificationService.class));
    }

    public void setAlarm(Context context) {
        Log.d(TAG, "Setting alarm");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        // Repeat every 5 minutes.
        Calendar c = Calendar.getInstance();
        int interval = PreferenceManager.getDefaultSharedPreferences(context).getInt("interval", 5);
        c.add(Calendar.MINUTE, 1);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TimeUnit.MINUTES.toMillis(interval), pendingIntent);
    }

    public void cancelAlarm(Context context) {
        Log.d(TAG, "Canceling alarm");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pendingIntent);
    }
}
