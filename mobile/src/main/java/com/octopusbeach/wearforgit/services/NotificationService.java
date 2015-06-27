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
import com.octopusbeach.wearforgit.model.GitNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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

        ArrayList<GitNotification> notifications = new ArrayList<>();

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

            // We now have an array of notification json objects.
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject not = array.getJSONObject(i);
                    JSONObject subject = not.getJSONObject("subject");
                    URL commentURL = new URL(subject.getString("latest_comment_url") + "?access_token=" + token);
                    URLConnection commentConnection = commentURL.openConnection();
                    BufferedReader commentReader = new BufferedReader(new InputStreamReader(commentConnection.getInputStream()));
                    StringBuilder commentBuilder = new StringBuilder();
                    line = commentReader.readLine();
                    while (line != null) {
                        commentBuilder.append(line);
                        line = commentReader.readLine();
                    }
                    JSONObject comment = new JSONObject(commentBuilder.toString());

                    JSONObject user = new JSONObject(comment.getString("user"));

                    GitNotification note = new GitNotification(subject.getString("title"),
                            subject.getString("type"), user.getString("login") + ": " + comment.getString("body"));
                    notifications.add(note);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            publishResults(notifications);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishResults(ArrayList<GitNotification> notifications) {
        Log.d(TAG, "Publishing results");
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        int notification = 1;
        if (notifications != null) {
            //Create the individual notifications.
            for (GitNotification not : notifications) {
                manager.notify(notification, getBuilderForGitNotification(not).build());
                notification++;
            }

            if (notifications.size() > 1) { // Multiple notifications.
                // Create the summary notification.
                Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("" + notifications.size() + " new")
                        .setContentTitle("New GitHub notifications")
                        .setGroup(GROUP)
                        .setGroupSummary(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pi);
                manager.notify(notification, builder.build());
            }
        }
    }

    private NotificationCompat.Builder getBuilderForGitNotification(GitNotification not) {
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(not.getComment());

        Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
        return
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(not.getType())
                        .setContentTitle(not.getTitle())
                        .setContentIntent(pi)
                        .setGroup(GROUP)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(bigStyle);
    }
}
