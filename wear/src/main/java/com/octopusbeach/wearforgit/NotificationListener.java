package com.octopusbeach.wearforgit;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by hudson on 6/27/15.
 */
public class NotificationListener extends WearableListenerService {

    private int notificationId = 1;
    private static final String TAG = NotificationListener.class.getSimpleName();

    public static final String NOTIFICATION_PATH = "/notification";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "content";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_KEY = "loggedIn";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "Data changed");
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                String uriPath = dataMapItem.getUri().getPath();
                DataMap map = dataMapItem.getDataMap();
                if (NOTIFICATION_PATH.equals(uriPath)) {
                    String title = map.getString(NOTIFICATION_TITLE);
                    String content = map.getString(NOTIFICATION_CONTENT);
                    sendNotification(title, content);
                } else if (LOGIN_PATH.equals(uriPath)) {
                    boolean loggedIn = map.getBoolean(LOGIN_KEY, false);
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    edit.putBoolean(LOGIN_KEY, loggedIn);
                    edit.apply();
                }
            }
        }
    }

    private void sendNotification(String title, String content) {
        // Create the second page which will bring us to an activity.
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingViewIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
        Notification secondPage = new NotificationCompat.Builder(this)
                .extend(new NotificationCompat.WearableExtender()
                        .setDisplayIntent(pendingViewIntent)
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN))
                .build();


        Notification note = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .extend(new NotificationCompat.WearableExtender()
                        .addPage(secondPage))
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(notificationId++, note);
    }

}
