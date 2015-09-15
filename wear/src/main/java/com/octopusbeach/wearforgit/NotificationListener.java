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
    public static final String TITLE = "title";
    public static final String COMMENT = "content";
    public static final String TYPE = "type";
    public static final String REPO = "repo";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_KEY = "loggedIn";
    private static final String GROUP = "notifications";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                String uriPath = dataMapItem.getUri().getPath();
                DataMap map = dataMapItem.getDataMap();
                if (NOTIFICATION_PATH.equals(uriPath)) {
                    String title = map.getString(TITLE);
                    String comment = map.getString(COMMENT);
                    String repo = map.getString(REPO);
                    String type = map.getString(TYPE);
                    sendNotification(title, comment, repo, type);
                } else if (LOGIN_PATH.equals(uriPath)) {
                    boolean loggedIn = map.getBoolean(LOGIN_KEY, false);
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    edit.putBoolean(LOGIN_KEY, loggedIn);
                    edit.apply();
                }
            }
        }
    }

    private void sendNotification(String title, String comment, String repo, String type) {
        // Create the notifications second page which gives the user more information about the notification.
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();

        bigStyle.bigText(comment);
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle(repo)
                .setStyle(bigStyle)
                .build();

        Notification note = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(type)
                .setContentText(title)
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroup(GROUP)
                .extend(new NotificationCompat.WearableExtender()
                        .addPage(secondPage))
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(notificationId++, note);
    }

}
