package com.example.exercise_android_trainer;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;
import static org.webrtc.ContextUtils.getApplicationContext;
import com.example.exercise_android_trainer.GetPostsActivity;

public class Post {
    //declare private data instead of public to ensure the privacy of data field of each class
    private String title;
    private String content;
    private String writer;

    public Post(String writer, String title, String content) {
        this.title = title;
        this.writer = writer;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }

    public static ArrayList<Post> getPosts() {
        GetPostsActivity getPostsActivity = new GetPostsActivity();

//        Intent intent = new Intent(this.getIntent());

//        posts.add(new Post("Harry", "San Diego", "dddd"));
//        posts.add(new Post("Harry2", "San Diego2", "dddd2"));
//        posts.add(new Post("Harry3", "San Diego3", "dddd3"));

        return getPostsActivity.posts;
    }
}
