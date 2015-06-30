package com.octopusbeach.wearforgit.services;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by hudson on 6/28/15.
 */
public class WearableListener extends WearableListenerService {

    private static final String REFRESH_PATH = "/refresh";
    private static final String TAG = WearableListener.class.getSimpleName();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received");
        if (messageEvent.getPath().equals(REFRESH_PATH)) {
            Intent startIntent = new Intent(this, NotificationService.class);
            startService(startIntent);
        }
    }
}
