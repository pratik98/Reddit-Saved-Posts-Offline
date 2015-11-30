package io.github.pratik98.minimal_saved_reddit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;


/**
 * Created by Pratik Agrawal on 11/20/2015.
 */
public class RedditRestClient {
    SharedPreferences pref;
    String token;
    Context context;
    JSONObject responseObject;
    JSONArray respArray;
    String respString;

    private static String CLIENT_ID = "";
    private static String CLIENT_SECRET = "";
    private static final String BASE_URL = "https://www.reddit.com/api/v1/";
    private static String REDIRECT_URI = "http://pratik98.github.io";

    RedditRestClient(Context cnt) {
        context = cnt;
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);

    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void getToken(String relativeUrl, String grant_type, String device_id) throws JSONException {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        //  pref = context.getSharedPreferences("AppPref",Context.MODE_PRIVATE);
        String code = pref.getString("Code", "");
        Log.i("code", code);
        RequestParams requestParams = new RequestParams();
        requestParams.put("code", code);
        requestParams.put("grant_type", grant_type);
        requestParams.put("redirect_uri", REDIRECT_URI);

        post(relativeUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
              //  Log.i("response", response.toString());
                try {
                    token = response.getString("access_token").toString();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("token", token);
                    edit.commit();
                    Log.i("Step2 done,Access_token", pref.getString("token", ""));
                    getUsername();

                } catch (JSONException j) {
                    j.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.i("statusCode", "" + statusCode);
            }
        });

    }




    public void getUsername() {
        Log.i("token", pref.getString("token", ""));
        //  client.addHeader("Authorization", "bearer " + pref.getString("token", ""));
        // client.addHeader("User-Agent", "Redditsavedoffline/0.1 by pratik");

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", "myRedditapp/0.1 by redditusername");
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));

        client.get(context, "https://oauth.reddit.com/api/v1/me", headers, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
             //   Log.i("response", response.toString());
                try {
                    String username = response.getString("name").toString();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("username", username);
                    edit.commit();
                    Log.i("Step3 done,username", pref.getString("username", ""));
                    String PostURL = "https://oauth.reddit.com/user/" + pref.getString("username", "").trim() + "/saved?limit=50";
                    writePoststofile(PostURL);
                } catch (JSONException j) {
                    j.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("response", errorResponse.toString());
                Log.i("statusCode", "" + statusCode);
            }
        });
    }

    public void writePoststofile(String url)
    {

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", "myRedditapp/0.1 by redditusername");
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));

        client.get(context, url, headers, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                //   Log.i("response", resp.toString());
                Toast.makeText(context, "" + resp.toString(),Toast.LENGTH_SHORT).show();
                try
                {
                    Toast.makeText(context,""+Environment.getExternalStorageDirectory(),Toast.LENGTH_LONG).show();
                    File appdir = new File(Environment.getExternalStorageDirectory(),"reddit");
                    if(!appdir.exists())
                    {
                        appdir.mkdirs();
                    }
                    File gpxfile = new File(appdir, "posts.txt");
                    FileWriter writer = new FileWriter(gpxfile,false);
                    writer.append(resp.toString());
                    writer.flush();
                    writer.close();

                    SharedPreferences.Editor editor= pref.edit();
                    editor.putString("writeSuccess", "true");
                    editor.commit();
                    Log.i("Step4 done,file written", pref.getString("writeSuccess", ""));

                }
                catch(IOException e)
                {
                    Log.i("Log","failed to write file on device");
                    e.printStackTrace();
                }
               responseObject=resp;
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("response", errorResponse.toString());
                Log.i("statusCode", "" + statusCode);
                responseObject=errorResponse;

            }

            @Override
            public void onFinish() {
                Intent i = new Intent(context,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }

    public String getResponseString(String url)
    {

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("User-Agent", "myRedditapp/0.1 by redditusername");
        headers[1] = new BasicHeader("Authorization", "bearer " + pref.getString("token", ""));

        client.get(context, url, headers, null, new JsonHttpResponseHandler() {
           @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("response", responseString.toString());
                respString=responseString;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("response", responseString.toString());
                Log.i("statusCode", "" + statusCode);
                respString=responseString;
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("response", errorResponse.toString());
                Log.i("statusCode", "" + statusCode);
                Object o =headers.clone();

                Log.i("headers",o.toString());
            }


        });
        return respString;
    }
    public void revokeToken() {
        client.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        //  pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String access_token = pref.getString("token", "");

        RequestParams requestParams = new RequestParams();
        requestParams.put("token", access_token);
        requestParams.put("token_type_hint", "access_token");

        post("revoke_token", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("response", response.toString());
                SharedPreferences.Editor edit = pref.edit();
                edit.remove(token);
                edit.commit();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("statusCode", "" + statusCode);
            }
        });

    }

}
