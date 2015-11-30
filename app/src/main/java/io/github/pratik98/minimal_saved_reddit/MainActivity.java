package io.github.pratik98.minimal_saved_reddit;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerViewPosts = (RecyclerView) findViewById(R.id.rvPosts);
        ArrayList<RedditPost> redditPosts=  fetchRedditPosts();
        PostsAdapter postsAdapter = new PostsAdapter(redditPosts);
        recyclerViewPosts.setAdapter(postsAdapter);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

       final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private ArrayList<RedditPost> fetchRedditPosts() {
        ArrayList<RedditPost> postsObjlist = null;
        File sdcard = new File(Environment.getExternalStorageDirectory(),"reddit");
        File saved_posts = new File(sdcard,"posts.txt");
        Log.i("Step5 done,","Display posts" );
        if(saved_posts.length() > 0) {

            try {
                String posts_text = FileUtils.readFileToString(saved_posts);
                JSONObject posts_json, posts_json2;
                JSONArray posts;
                try {
                    posts_json = new JSONObject(posts_text);
                    posts_json2 = posts_json.getJSONObject("data");
                    posts = posts_json2.getJSONArray("children");
                    Toast.makeText(getApplicationContext(), "" + posts.length(), Toast.LENGTH_LONG).show();
                    postsObjlist = RedditPost.fromJsonArray(posts);

                    return postsObjlist;

                } catch (JSONException e) {
                    e.printStackTrace();
                    return postsObjlist;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return postsObjlist;
            }
        }else
        {
            Toast.makeText(MainActivity.this,"There isn't seem to anything to read!",Toast.LENGTH_SHORT).show();
        }
        return postsObjlist;
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


