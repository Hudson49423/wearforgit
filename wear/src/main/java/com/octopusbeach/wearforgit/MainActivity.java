package com.octopusbeach.wearforgit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

    private static final String LOGIN_KEY = "loggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(LOGIN_KEY, false))
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_main_not_logged_in);
    }
}
