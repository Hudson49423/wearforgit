package com.octopusbeach.wearforgit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import at.markushi.ui.CircleButton;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String LOGIN_KEY = "loggedIn";
    private static final String REFRESH_PATH = "/refresh";

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(LOGIN_KEY, false)) { // We are connected.
            setContentView(R.layout.activity_main);
            CircleButton button = (CircleButton) findViewById(R.id.refresh_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });
            setUpClient();
        } else // We are not connected.
            setContentView(R.layout.activity_main_not_logged_in);
    }

    private void sendMessage() {
        if (client == null) return;
        if (client.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult result = Wearable.NodeApi.getConnectedNodes(client).await();
                    List<Node> nodes = result.getNodes();
                    for (Node node : nodes) {
                        Wearable.MessageApi.sendMessage(client, node.getId(), REFRESH_PATH, null);
                    }
                }
            }).start();
        }
    }

    private void setUpClient() {
        if (client == null) return;
        client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client == null) return;
        client.disconnect();
    }
}
