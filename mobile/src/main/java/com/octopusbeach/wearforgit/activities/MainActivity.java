package com.octopusbeach.wearforgit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.octopusbeach.wearforgit.Helpers.AuthHelper;
import com.octopusbeach.wearforgit.R;
import com.octopusbeach.wearforgit.services.BroadcastReceiver;

import java.io.File;
import java.io.FileInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.login_button)
    Button loginButton;
    @InjectView(R.id.avatar)
    ImageView avatarImage;
    @InjectView(R.id.user_text)
    TextView userText;

    private boolean loggedIn;
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_KEY = "loggedIn";
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("loginSuccessful", false))
            alertWatchOfLogin(true);
        SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
        // Check to see if we are logged in.
        if (prefs.getString(AuthHelper.TOKEN_KEY, null) != null) { // We are logged in.
            loginButton.setText(R.string.logout);
            loggedIn = true;
            loadAvatar();
            userText.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(AuthHelper.USER_NAME_KEY, ""));
        } else {
            loggedIn = false;
        }
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
            // Stop the current service and alarm manager.
            new BroadcastReceiver().cancelAlarm(this);
            alertWatchOfLogin(false);
            // Delete the name
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString(AuthHelper.USER_NAME_KEY, "");
            edit.apply();
            userText.setText("");
            //Now try to delete the avatar image.
            try {
                File img = this.getFileStreamPath(AuthHelper.AVATAR_FILE_NAME);
                img.delete();
                avatarImage.setImageDrawable(null);
            } catch (Exception e) {
                Log.e(TAG, "Could not delete the avatar");
            }
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

    private void loadAvatar() {
        try {
            File path = this.getFileStreamPath(AuthHelper.AVATAR_FILE_NAME);
            FileInputStream fis = new FileInputStream(path);
            avatarImage.setImageBitmap(BitmapFactory.decodeStream(fis));
        } catch (Exception e) {
            Log.e(TAG, "Could not load the avatar image");
            e.printStackTrace();
        }
    }

}