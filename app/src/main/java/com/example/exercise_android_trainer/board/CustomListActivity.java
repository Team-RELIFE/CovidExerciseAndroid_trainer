package com.example.exercise_android_trainer.board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.exercise_android_trainer.R;
import com.example.exercise_android_trainer.reservation.GetItemActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CustomListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        ListView listView = findViewById(R.id.lvPosts);
        ImageButton createPostButton = findViewById(R.id.createPostBtn);
        createPostButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goCreatePostActivity = new Intent(getApplicationContext(),CreatePostActivity.class);
                startActivity(goCreatePostActivity);
            }
        }) ;
        populatePostsList();

        // 당겨서 새로고침
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getApplicationContext(), GetPostsActivity.class);
                startActivity(intent);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //항목을 클릭하면 해당 글 상세보기 페이지로 데이터를 넘긴 후 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                Post item = (Post) listView.getItemAtPosition(a_position);
                Intent postViewActivity = new Intent(getApplicationContext(),PostViewActivity.class);
                postViewActivity.putExtra("id", item.getId());
                postViewActivity.putExtra("title", item.getTitle());
                postViewActivity.putExtra("writer", item.getWriter());
                postViewActivity.putExtra("content", item.getContent());
                postViewActivity.putExtra("date", item.getDate());
                startActivity(postViewActivity);
            }
        });
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
                    JSONObject json = new JSONObject();

                    for (int i=0; i<jArr.length();i++) {
                        json = jArr.getJSONObject(i);

                        String postId = json.getString("postId");
                        String writer = json.getString("writer");
                        String title = json.getString("title");
                        String content = json.getString("content");
                        String date = json.getString("date");

                        arrayOfPosts.add(new Post(postId, writer, title, content, date));
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        else {
            Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }

        Collections.reverse(arrayOfPosts); //늦게 생성된 글이 위로 오도록 순서 반전
        CustomPostsAdapter adapter = new CustomPostsAdapter(this, arrayOfPosts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvPosts);
        listView.setAdapter(adapter);

    }
}