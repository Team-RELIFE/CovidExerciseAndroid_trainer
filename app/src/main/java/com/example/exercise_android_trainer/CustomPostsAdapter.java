package com.example.exercise_android_trainer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class CustomPostsAdapter extends ArrayAdapter {
    public CustomPostsAdapter(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
        }

        // Get the data item for this position
        Post post = (Post) getItem(position);

        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvWriter = (TextView) convertView.findViewById(R.id.tvWriter);
        // Populate the data into the template view using the data object
        tvTitle.setText(post.getTitle());
        tvWriter.setText(post.getWriter());
        // Return the completed view to render on screen
        return convertView;
    }

}
