package io.github.pratik98.minimal_saved_reddit;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pratik Agrawal on 11/17/2015.
 */
public class RedditPost {
    //common
    private String kind;
    private String subreddit;
    private String author;
    private String created;

    //t3 = entire thread
    private String title;
    private String url;
    private String selftext;
    private Boolean is_self;
    private String permalink;
    private String post_hint;

    //t1 = comment
    private String link_title;
    private String link_url;
    private String body;
    private String link_author;
    private ArrayList<String> imgURLList;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public Boolean getIs_self() {
        return is_self;
    }

    public void setIs_self(Boolean is_self) {
        this.is_self = is_self;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPost_hint() {
        return post_hint;
    }

    public void setPost_hint(String post_hint) {
        this.post_hint = post_hint;
    }

    public String getLink_title() {
        return link_title;
    }

    public void setLink_title(String link_title) {
        this.link_title = link_title;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLink_author() {
        return link_author;
    }

    public void setLink_author(String link_author) {
        this.link_author = link_author;
    }

    public ArrayList<String> getImgURLList() {
        return imgURLList;
    }

    public void setImgURLList(ArrayList<String> imgURLList) {
        this.imgURLList = imgURLList;
    }
    public static RedditPost fromJson(JSONObject jsonObject)
    {
        RedditPost postobject = new RedditPost();

        try {
            postobject.kind = jsonObject.getString("kind");
            postobject.author = jsonObject.getJSONObject("data").getString("author");
            postobject.subreddit = jsonObject.getJSONObject("data").getString("subreddit");
            postobject.created = jsonObject.getJSONObject("data").getString("created");

            if(postobject.kind.equalsIgnoreCase("t3")) {
                postobject.title = jsonObject.getJSONObject("data").getString("title");
                postobject.selftext = jsonObject.getJSONObject("data").getString("selftext");
                postobject.url = jsonObject.getJSONObject("data").getString("url");
                postobject.is_self = jsonObject.getJSONObject("data").getBoolean("is_self");
                postobject.permalink = jsonObject.getJSONObject("data").getString("permalink");
               // if(postobject.is_self)
                //postobject.post_hint = jsonObject.getJSONObject("data").getString("post_hint");
            }
            else{
                postobject.body = jsonObject.getJSONObject("data").getString("body");
                postobject.link_title = jsonObject.getJSONObject("data").getString("link_title");
                postobject.link_author = jsonObject.getJSONObject("data").getString("link_author");
            }
             Log.d("subreddit", postobject.subreddit);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return postobject;
    }

    public static ArrayList<RedditPost> fromJsonArray(JSONArray jsonArray)
    {
        JSONObject postJson = null;

        ArrayList<RedditPost> posts = new ArrayList<RedditPost>(jsonArray.length());
        for(int i=0;i <jsonArray.length(); i++)
        {
            try {
                postJson = jsonArray.getJSONObject(i);
                RedditPost post = RedditPost.fromJson(postJson);
                    if (post != null) {
                        posts.add(post);
                    }

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

        }

        return posts;
    }
}
