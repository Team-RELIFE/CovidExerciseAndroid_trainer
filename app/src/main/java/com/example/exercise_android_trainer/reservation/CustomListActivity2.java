
package com.example.exercise_android_trainer.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.exercise_android_trainer.PopupActivity;
import com.example.exercise_android_trainer.R;
import com.example.exercise_android_trainer.User;
import com.example.exercise_android_trainer.board.CreatePostActivity;
import com.example.exercise_android_trainer.board.CustomPostsAdapter;
import com.example.exercise_android_trainer.board.Post;
import com.example.exercise_android_trainer.board.PostViewActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class CustomListActivity2 extends AppCompatActivity {

    //ListView listView;
    TextView title, info_msg_status0, info_msg_status1;
    int num_status0, num_status1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list2);
        ListView listView = findViewById(R.id.lvItem);

        title = findViewById(R.id.classList_tv);
        info_msg_status0 = findViewById(R.id.info_msg);
        info_msg_status1 = findViewById(R.id.info_msg3);

        title.setText(User.nickname + "님의 클래스");

        // 당겨서 새로고침
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getApplicationContext(), GetItemActivity.class);
                startActivity(intent);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        // 스피너
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                populatePostsList(i);
                info_msg_status0.setText(String.valueOf(num_status0) + "건");
                info_msg_status1.setText(String.valueOf(num_status1) + "건");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //항목을 클릭하면 해당 글 상세보기 페이지로 데이터를 넘긴 후 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                Item item = (Item) listView.getItemAtPosition(a_position);
//                Intent postViewActivity = new Intent(getApplicationContext(), PostViewActivity.class);
                System.out.println("온클릭리스너 실행: " + item.getId());
                ConnectServer(item.getId());
//                postViewActivity.putExtra("id", item.getId());
//                postViewActivity.putExtra("title", item.getTitle());
//                postViewActivity.putExtra("writer", item.getWriter());
//                postViewActivity.putExtra("content", item.getContent());
//                postViewActivity.putExtra("date", item.getDate());
//                startActivity(postViewActivity);
            }
        });
    }

    private void populatePostsList(int condition) {
        ArrayList<Item> arrayOfReservations = Item.getItem();
        ArrayList<Item> arrayOfReservations_status0 = Item.getItem();
        ArrayList<Item> arrayOfReservations_status1 = Item.getItem();
        num_status0 = 0; num_status0 = 0;

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

                        int id = json.getInt("id");
                        String trainer = json.getString("trainer");
                        String trainee = json.getString("trainee");
                        int status = json.getInt("status");
                        int post = json.getInt("post");

                        // status에 따라 item을 분류
                        if (status == 0) {
                            arrayOfReservations_status0.add(new Item(id, trainer, trainee, status, post));
                        } else {
                            arrayOfReservations_status1.add(new Item(id, trainer, trainee, status, post));
                        }
                        arrayOfReservations.add(new Item(id, trainer, trainee, status, post));
                    }

                    num_status0 = arrayOfReservations_status0.size();
                    num_status1 = arrayOfReservations_status1.size();

                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        else {
            Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }

        Collections.reverse(arrayOfReservations); //늦게 생성된 데이터가 위로 오도록 순서 반전

        CustomItemAdapter adapter = new CustomItemAdapter(this, arrayOfReservations);
        if (condition == 1) {
            adapter = new CustomItemAdapter(this, arrayOfReservations_status0);
        } else if (condition == 2) {
            adapter = new CustomItemAdapter(this, arrayOfReservations_status1);
        }
        ListView listView = (ListView) findViewById(R.id.lvItem);
        listView.setAdapter(adapter);
    }

    public void ConnectServer(int id){

        //                         http://서버 ip:포트번호(tomcat 8080포트 사용)/DB연동하는 jsp파일
        final String SIGNIN_URL = "http://pt-app.kr:8080/" + "getSession.jsp";
        final String urlSuffix = "?id=" + id;
        //Log.d("urlSuffix", urlSuffix);

        class SearchReservationRecord extends AsyncTask<String, Void, String> {

            //스레드 관련 및 ui와의 통신을 위한 함수들이 구현되어 있음

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s); // s : DB로부터 리턴된 값

                //s == null -> db 통신 오류, s의 길이는 기본 2이므로 s.length == 2일 때 검색된 값이 없는 것으로 취급

                if (s != null) { //리턴 값이 null이 아니면 jsonArray로 값 목록을 받음

                    try{
                        if (s.length() <= 2) { //검색된 값이 없음
                            System.out.println("검색된 값 없음");
                        }
                        else {
                            JSONArray jArr = new JSONArray(s);;
                            JSONObject json = new JSONObject();
                            json = jArr.getJSONObject(0);

                            String startdate = json.getString("startdate");
                            String enddate = json.getString("enddate");
                            int session = json.getInt("session");

                            Intent intent=new Intent(getApplicationContext(), PopupActivity.class);
                            intent.putExtra("startdate", startdate);
                            intent.putExtra("enddate", enddate);
                            intent.putExtra("session", session);

                            startActivity(intent);
//                            sendData(s);

                            System.out.println("팝업 : " + s);
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.println("통신 오류 발생");
                }
            }

            @Override
            protected String doInBackground(String... params) {
                BufferedReader bufferedReader = null;
                String line = null, page = "";

                try {
                    HttpURLConnection conn = null;

                    URL url = new URL(SIGNIN_URL); //요청 URL을 입력
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)


                    //strParams에 데이터를 담아 서버로 보냄
                    String strParams = "id=" + id;

                    OutputStream os = conn.getOutputStream();
                    os.write(strParams.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 통신 체크 : 연결 실패시 null 반환하고 종료
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("PopupActivity", "통신 오류");
                        return null;
                    }

                    else {
                        BufferedReader bufreader = new BufferedReader(
                                new InputStreamReader(
                                        conn.getInputStream(), "utf-8"));
                        while ((line = bufreader.readLine()) != null) {
                            page += line;
                        }

                        return page;
                    }
                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        SearchReservationRecord shr = new SearchReservationRecord();
        shr.execute(urlSuffix);
    }


}