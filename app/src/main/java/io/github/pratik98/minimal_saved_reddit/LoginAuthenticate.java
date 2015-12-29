package io.github.pratik98.minimal_saved_reddit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by 20080048 on 11/27/2015.
 */
public class LoginAuthenticate extends AppCompatActivity {

    private static String CLIENT_ID = "";
    private static String CLIENT_SECRET ="";
    private static String REDIRECT_URI="http://pratik98.github.io";
    private static String GRANT_TYPE="https://oauth.reddit.com/grants/installed_client";
    private static String GRANT_TYPE2="authorization_code";
    private static String TOKEN_URL ="access_token";
    private static String OAUTH_URL ="https://www.reddit.com/api/v1/authorize";
    private static String OAUTH_SCOPE="identity,history";
    private static String DURATION = "permanent";

    WebView web;
    Button auth;
    SharedPreferences pref;
    TextView Access;
    Dialog auth_dialog;
    String DEVICE_ID = UUID.randomUUID().toString();
    String authCode;
    Boolean isRedirected;
    boolean authComplete = false;
    JSONObject savedposts;
    Intent resultIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		// if already logged in once, redirect to saved posts  
		File appdir = new File(Environment.getExternalStorageDirectory(),"reddit");
                    if(appdir.exists())
                    {
                         Intent i = new Intent(this,MainActivity.class);
						startActivity(i);
                    }
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        Access =(TextView)findViewById(R.id.Access);
        auth = (Button)findViewById(R.id.auth);
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                auth_dialog = new Dialog(LoginAuthenticate.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                clearPreferences();
                web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                String url = OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&state=TEST&redirect_uri=" + REDIRECT_URI + "&scope=" + OAUTH_SCOPE;
                web.loadUrl(url);
                Toast.makeText(getApplicationContext(), "" + url, Toast.LENGTH_LONG).show();

                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        isRedirected = true;
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        isRedirected = false;

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (!isRedirected) {
                            if (url.contains("?code=") || url.contains("&code=")) {

                                Uri uri = Uri.parse(url);
                                authCode = uri.getQueryParameter("code");
                                Log.i("Step:1 done,CODE:", " " + authCode);
                                authComplete = true;
                                resultIntent.putExtra("code", authCode);
                                LoginAuthenticate.this.setResult(Activity.RESULT_OK, resultIntent);
                                setResult(Activity.RESULT_CANCELED, resultIntent);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("Code", authCode);
                                edit.commit();
                                auth_dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Authorization Code is: " + pref.getString("Code", ""), Toast.LENGTH_SHORT).show();

                                try {
                                    new RedditRestClient(getApplicationContext()).getToken(TOKEN_URL, GRANT_TYPE2, DEVICE_ID);
                                   // Toast.makeText(getApplicationContext(), "Access Token: " + pref.getString("token", ""), Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else if (url.contains("error=access_denied")) {
                                Log.i("", "ACCESS_DENIED_HERE");
                                resultIntent.putExtra("code", authCode);
                                authComplete = true;
                                setResult(Activity.RESULT_CANCELED, resultIntent);
                                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                                auth_dialog.dismiss();
                            }
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("Authorize");
                auth_dialog.setCancelable(true);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fetching Saved Posts...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            };

        });
    }

    private void clearPreferences() {
        try {
            pref = getSharedPreferences("AppPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor= pref.edit();
            editor.clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


}
