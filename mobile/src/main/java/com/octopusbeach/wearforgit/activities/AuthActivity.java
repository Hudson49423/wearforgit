package com.octopusbeach.wearforgit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.octopusbeach.wearforgit.Helpers.AuthHelper;
import com.octopusbeach.wearforgit.R;
import com.octopusbeach.wearforgit.services.BroadcastReceiver;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import butterknife.ButterKnife;
import butterknife.InjectView;

//https://stackoverflow.com/questions/22062145/oauth-2-0-authorization-for-linkedin-in-android
public class AuthActivity extends ActionBarActivity {

    @InjectView(R.id.webView)
    WebView webView;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.inject(this);
        webView.clearCache(true);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.getSettings().setJavaScriptEnabled(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        WebView webview = new WebView(this);
        WebSettings ws = webview.getSettings();
        ws.setSaveFormData(false);
        progress = new ProgressDialog(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progress.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(AuthHelper.REDIRECT)) {
                    Uri uri = Uri.parse(url);
                    String stateToken = uri.getQueryParameter(AuthHelper.STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(AuthHelper.STATE)) {
                        Log.i("auth", "State token does not match");
                        return true;
                    }

                    String authToken = uri.getQueryParameter("code");
                    if (authToken == null) {
                        Log.i("auth", "token was null");
                        return true;
                    }
                    new PostRequestTask().execute(AuthHelper.getAccessTokenUrl(authToken));
                    webView.destroy();
                    return true;
                } else {
                    webView.loadUrl(url);
                    return true;
                }
            }
        });
        progress.show();
        webView.loadUrl(AuthHelper.getAuthorizationUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    private class PostRequestTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(AuthActivity.this);
            progress.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {
                String s = urls[0];
                try {
                    URL url = new URL(s);
                    URLConnection connection = url.openConnection();
                    String response = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                    String accessToken = response.split("=")[1].split("&")[0];

                    URL nameUrl = new URL(AuthHelper.USER_URL + accessToken);
                    URLConnection nameConnection = nameUrl.openConnection();
                    BufferedReader nameReader = new BufferedReader(new InputStreamReader(nameConnection.getInputStream()));
                    StringBuilder nameJsonString = new StringBuilder();
                    String line = nameReader.readLine();
                    while (line != null) {
                        nameJsonString.append(line);
                        line = nameReader.readLine();
                    }
                    JSONObject nameObj = new JSONObject(nameJsonString.toString());
                    String name = nameObj.getString("login");

                    // Save access token.
                    SharedPreferences preferences = AuthActivity.this.getSharedPreferences("token", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(AuthHelper.TOKEN_KEY, accessToken);
                    editor.apply();

                    // Save the name.
                    SharedPreferences.Editor defaultEditor = PreferenceManager.getDefaultSharedPreferences(AuthActivity.this).edit();
                    defaultEditor.putString(AuthHelper.USER_NAME_KEY, name);
                    defaultEditor.apply();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            progress.dismiss();
            if (status) {
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
                AuthActivity.this.finish();
                Log.d("Auth", "Finished Successful");
                new BroadcastReceiver().setAlarm(AuthActivity.this);
            }
        }
    }
}
