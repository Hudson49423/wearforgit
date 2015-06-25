package com.octopusbeach.wearforgit.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import java.util.Calendar;

/**
 * Created by hudson on 6/25/15.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the notification service.
        context.startService(new Intent(context, NotificationService.class));
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        // Repeat every 5 minutes.
        Calendar c = Calendar.getInstance();
        int interval = PreferenceManager.getDefaultSharedPreferences(context).getInt("interval", 5);
        c.add(Calendar.MINUTE, interval);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), c.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pendingIntent);
    }
}
