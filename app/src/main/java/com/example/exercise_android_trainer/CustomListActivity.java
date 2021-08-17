package com.example.exercise_android_trainer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        Button createPostButton = findViewById(R.id.createPostBtn);
        createPostButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goCreatePostActivity = new Intent(getApplicationContext(),CreatePostActivity.class);
                startActivity(goCreatePostActivity);
            }
        }) ;
        populatePostsList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, ShowPostActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void populatePostsList() {
        ArrayList<Post> arrayOfPosts = Post.getPosts();

        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("result");

        if (s != null) { //리턴 값이 null이 아니면 jsonArray로 값 목록을 받음
            try{
                if (s.length() <= 2) { //검색된 값이 없음
                    Toast toast = Toast.makeText(getApplicationContext(), "작성된 글이 없습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    JSONArray jArr = new JSONArray(s);;
//                            ArrayList posts = new ArrayList();
//                            ArrayList<Post> posts = new ArrayList<Post>();
                    JSONObject json = new JSONObject();

                    for (int i=0; i<jArr.length();i++) {
                        json = jArr.getJSONObject(i);

                        String writer = json.getString("writer");
                        String title = json.getString("title");
                        String content = json.getString("content");

                        arrayOfPosts.add(new Post(writer, title, content));
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        else {
            Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }

        CustomPostsAdapter adapter = new CustomPostsAdapter(this, arrayOfPosts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvPosts);
        listView.setAdapter(adapter);

    }
}