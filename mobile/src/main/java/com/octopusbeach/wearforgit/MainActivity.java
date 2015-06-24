package com.octopusbeach.wearforgit;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

//https://stackoverflow.com/questions/22062145/oauth-2-0-authorization-for-linkedin-in-android
public class MainActivity extends ActionBarActivity {

    private WebView webView;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        webView.requestFocus(View.FOCUS_DOWN);
        progress = ProgressDialog.show(this, "", getString(R.string.loading), true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progress != null)
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
                    return true;
                } else {
                    webView.loadUrl(url);
                    return true;
                }
            }
        });
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
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {
                String s = urls[0];
                try {
                    URL url = new URL(s);
                    URLConnection connection = url.openConnection();
                    String response = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                    String accessToken = response.split("=")[1].split("&")[0];
                    // Save access token.
                    SharedPreferences preferences = MainActivity.this.getSharedPreferences("token", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("accessToken", accessToken);
                    editor.apply();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (status)
                Log.d("Auth", "Finished Successful");
        }
    }
}
