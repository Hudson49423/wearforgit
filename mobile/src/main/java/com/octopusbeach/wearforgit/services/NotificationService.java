package com.octopusbeach.wearforgit.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.octopusbeach.wearforgit.helpers.AuthHelper;
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

    public static final String NOTIFICATION_PATH = "/notification";
    public static final String TITLE = "title";
    public static final String COMMENT = "content";
    public static final String TYPE = "type";
    public static final String USER = "user";
    public static final String REPO = "repo";

    private GoogleApiClient client;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Pulling notifications");
        // Connect to the watch.
        client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();
        client.connect();

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
                    JSONObject repo = not.getJSONObject("repository");
                    GitNotification note = new GitNotification(subject.getString("title"),
                            subject.getString("type"), user.getString("login") + ": " + comment.getString("body"), repo.getString("name"), user.getString("login"));
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
        for (GitNotification not : notifications) {
            if (client.isConnected()) {
                PutDataMapRequest dataMapRequest = PutDataMapRequest.create(NOTIFICATION_PATH);
                dataMapRequest.getDataMap().putString(TITLE, not.getTitle());
                dataMapRequest.getDataMap().putString(COMMENT, not.getComment());
                dataMapRequest.getDataMap().putString(TYPE, not.getType());
                dataMapRequest.getDataMap().putString(USER, not.getUser());
                dataMapRequest.getDataMap().putString(REPO, not.getRepo());
                Wearable.DataApi.putDataItem(client, dataMapRequest.asPutDataRequest());
            } else {
                Log.e(TAG, "Wearable is not connected");
            }
        }
    }
}
