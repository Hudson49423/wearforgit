package com.octopusbeach.wearforgit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.octopusbeach.wearforgit.Helpers.AuthHelper;
import com.octopusbeach.wearforgit.R;
import com.octopusbeach.wearforgit.services.BroadcastReceiver;
import com.octopusbeach.wearforgit.services.NotificationService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
    @InjectView(R.id.login_button)
    Button loginButton;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private boolean loggedIn;

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_KEY = "loggedIn";
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("loginSuccessful", false))
            alertWatchOfLogin(true);

        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
        // Check to see if we are logged in.
        if (prefs.getString(AuthHelper.TOKEN_KEY, null) != null) { // We are logged in.
            loginButton.setText(R.string.logout);
            loggedIn = true;

        } else {
            loggedIn = false;
        }

        //TODO remove this.
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (loggedIn) {
            String name = PreferenceManager.getDefaultSharedPreferences(this).getString(AuthHelper.USER_NAME_KEY, null);
            if (name != null)
                toolbar.setTitle("Logged In As " + name);
        } else
            toolbar.setTitle("Not Currently Logged In");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.login_button)
    void login() {
        if (!loggedIn) // We are not logged in.
            startActivity(new Intent(this, AuthActivity.class));
        else {
            SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(AuthHelper.TOKEN_KEY, null);
            editor.apply();
            loginButton.setText(R.string.login);
            loggedIn = false;
            toolbar.setTitle("Not Currently Logged In");
            // Stop the current service and alarm manager.
            new BroadcastReceiver().cancelAlarm(this);
            alertWatchOfLogin(false);
        }
    }

    private void alertWatchOfLogin(final boolean logggedIn) {
        client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        sendData(logggedIn);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();
        client.connect();
    }

    private void sendData(boolean loggedIn) {
        // Now we want to actually send the data to the watch.
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(LOGIN_PATH);
        dataMapRequest.getDataMap().putBoolean(LOGIN_KEY, loggedIn);
        Wearable.DataApi.putDataItem(client, dataMapRequest.asPutDataRequest());
        client.disconnect();
    }

}