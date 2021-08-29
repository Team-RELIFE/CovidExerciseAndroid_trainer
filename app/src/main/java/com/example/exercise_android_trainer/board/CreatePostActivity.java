package com.example.exercise_android_trainer.board;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise_android_trainer.MainActivity2;
import com.example.exercise_android_trainer.R;
import com.example.exercise_android_trainer.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.Toast.LENGTH_SHORT;

public class CreatePostActivity extends AppCompatActivity {

    EditText content1Et, content2Et;
    Button okBtn, cancelBtn;
    TextView writerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        content1Et = findViewById(R.id.titleTv);
        content2Et = findViewById(R.id.create_contentTv);
        writerName = findViewById(R.id.writerNameTv);

        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this::onClick);
        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this::onClick);

        User currentUser = new User().getCurrentUser();
        writerName.setText(currentUser.id);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okBtn:
                User currentUser = new User().getCurrentUser();
                String writerId;
                if (currentUser.id != null) {
                    writerId = currentUser.id;
                    createPost(writerId);
                    content1Et.setText("");
                    content2Et.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", LENGTH_SHORT).show();
                    Intent intent=new Intent(CreatePostActivity.this, MainActivity2.class);
                    startActivity(intent);
                }
                break;

            case R.id.cancelBtn:
                Intent intent=new Intent(CreatePostActivity.this,MainActivity2.class);
                startActivity(intent);
                break;
        }
    }

    private void createPost(String writerId) {
        String content1 = content1Et.getText().toString();
        String content2 = content2Et.getText().toString();

        if (content1.length() == 0 || content2.length() == 0 || content1 == null || content2 == null){
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", LENGTH_SHORT).show();
        }
        else {
            connectDB(writerId, content1, content2);
        }
    }

    private void connectDB(String writerId, String content1, String content2){

        //                         http://서버 ip:포트번호(tomcat 8080포트 사용)/DB연동하는 jsp파일
        final String SIGNIN_URL = getString(R.string.db_server)+"createPost.jsp";
        final String urlSuffix = "?writerId=" + writerId + "&content1=" + content1 + "&content2=" + content2;

        class UserPost extends AsyncTask<String, Void, String> {

            //스레드 관련 및 ui와의 통신을 위한 함수들이 구현되어 있음

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s); // s : DB로부터 리턴된 값

                if (s != null) { //리턴 값이 null이 아니면 jsonArray로 값 목록을 받음

                    try{
                        if (!s.contains("success")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "입력한 정보를 다시 확인해주세요.", LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(), "작성이 완료되었습니다", LENGTH_SHORT);
                            toast.show();
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다.", LENGTH_SHORT).show();
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
                    String strParams = "writerId=" + writerId + "&content1=" + content1 + "&content2=" + content2;

                    OutputStream os = conn.getOutputStream();
                    os.write(strParams.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 통신 체크 : 연결 실패시 null 반환하고 종료
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("CreatePostActivity", "통신 오류");
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

        UserPost up = new UserPost();
        up.execute(urlSuffix);
    }
}
