package io.github.pratik98.minimal_saved_reddit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pratik Agrawal on 11/18/2015.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {



    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView postTitle;
        public TextView postBody;

        public ViewHolder(View itemview)
        {
            super(itemview);
            postTitle = (TextView)itemview.findViewById(R.id.lblPostTitle);
            postBody = (TextView)itemview.findViewById(R.id.lblPostText);
        }

    }

    private ArrayList<RedditPost> redditPosts;

    public PostsAdapter(ArrayList<RedditPost> rPosts)
    {
        redditPosts = rPosts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View postView = layoutInflater.inflate(R.layout.item_post, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RedditPost redditPost = redditPosts.get(position);

        TextView title = holder.postTitle;
        TextView body = holder.postBody;
        if(redditPost.getKind().equalsIgnoreCase("t1")) {
            title.setText(redditPost.getLink_title());
            body.setText(redditPost.getBody());
        }else {
            title.setText(redditPost.getTitle());
            body.setText(redditPost.getSelftext());
        }
    }

    @Override
    public int getItemCount() {
        return redditPosts.size();
    }

}
