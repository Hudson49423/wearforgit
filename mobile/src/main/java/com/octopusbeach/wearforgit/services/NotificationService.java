package com.octopusbeach.wearforgit.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.octopusbeach.wearforgit.Helpers.AuthHelper;
import com.octopusbeach.wearforgit.R;
import com.octopusbeach.wearforgit.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hudson on 6/25/15.
 */
public class NotificationService extends IntentService {
    private static final String TAG = NotificationService.class.getSimpleName();
    private static final String URL = "https://api.github.com/notifications?access_token=";

    private static final String GROUP = "notifications";


    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Pulling notifications");
        String token = getSharedPreferences("token", MODE_PRIVATE).getString(AuthHelper.TOKEN_KEY, null);
        if (token == null) {// We do not have an access token.
            Log.e(TAG, "Token was null");
            new BroadcastReceiver().cancelAlarm(getApplicationContext()); // Stop the alarm.
            return;
        }

        try {
            URL url = new URL(URL + token);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                jsonBuilder.append(line);
                line = reader.readLine();
            }
            JSONArray array = new JSONArray(jsonBuilder.toString());
            publishResults(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishResults(JSONArray array) {
        Log.d(TAG, "Publishing results");
        int notification = 1;
        if (array != null) {
            int length = array.length();
            if (length == 1) {
                try {
                    JSONObject not = array.getJSONObject(0);
                    JSONObject subject = not.getJSONObject("subject");

                    Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentText(subject.getString("title"))
                                    .setContentTitle("New " + subject.getString("type"))
                                    .setContentIntent(pi)
                                    .setDefaults(Notification.DEFAULT_ALL);
                    NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                    manager.notify(notification + 1, builder.build());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (length > 1) { // Multiple notifications
                NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                //Create the individual notifications.
                Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject not = array.getJSONObject(i);
                        JSONObject subject = not.getJSONObject("subject");
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentText(subject.getString("title"))
                                        .setContentTitle("New " + subject.getString("type"))
                                        .setContentIntent(pi)
                                        .setGroup(GROUP)
                                        .setGroupSummary(false);
                        manager.notify(notification, builder.build());
                        notification++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Create the summary notification.
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("" + length + " new")
                        .setContentTitle("New GitHub notifications")
                        .setGroup(GROUP)
                        .setGroupSummary(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pi);
                manager.notify(notification, builder.build());
            }
        }
    }
}
