package com.octopusbeach.wearforgit.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by hudson on 6/25/15.
 */
public class NotificationService extends IntentService {


    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO get notifications.
        publishResults();
    }

    private void publishResults() {
    }
}
