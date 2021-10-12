package com.example.exercise_android_trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button joinBtn, loginBtn; /*회원가입 버튼, 로그인 버튼*/
    OAuthLoginButton naverBtn;

    TextView guestLoginTV; // 게스트 로그인 텍스트뷰
    EditText emailEt, pwEt;
    String email="", pw="";

    private static OAuthLogin mOAuthLoginInstance; /*네이버 아이디로 로그인의 모든 연산을 수행하는 클래스*/
    private static Context mContext;
    private static String OAUTH_CLIENT_ID = "x75hOTqK0cxCz7lAugqC"; /*앱 등록 후 발급받은 클라이언트 ID*/
    private static String OAUTH_CLIENT_SECRET = "Tcr31DKBaM"; /*앱 등록 후 발급받은 클라이언트 비밀번호*/
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인하기"; /*로그인 시 화면에 표시할 앱 이름*/
    private long time=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=this; /*this=MainActivity*/
        initData(); /*'네이버 아이디로 로그인' 라이브러리를 애플리케이션에 적용하기위해 로그인 인스턴스를 초기화*/

        joinBtn=(Button)findViewById(R.id.JoinBtn);
        joinBtn.setOnClickListener(this::onClick);

        loginBtn=(Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this::onClick);
    }

    /*뒤로가기 버튼 두 번 누르면 앱 종료*/
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"한번더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        else if (System.currentTimeMillis()-time<2000){
            finishAffinity(); /*어느 액티비티에서든 모든 부모 액티비티 종료*/
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.JoinBtn: /*회원가입 버튼 눌렀을 때 JoinActivity1으로 이동*/
                Intent intent=new Intent(MainActivity.this,JoinActivity2.class);
                startActivity(intent);
                break;
            case R.id.loginBtn:
                emailEt = findViewById(R.id.emailEdit);
                pwEt = findViewById(R.id.passwordEdit);
                email = emailEt.getText().toString().trim();
                pw = pwEt.getText().toString().trim();

                if (email.length()==0 || pw.length()==0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "아이디와 패스워드를 모두 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                else {
                    login(this.email, this.pw);
                    break;
                }
            case R.id.guestLoginTV: // 게스트 로그인
                intent=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent);
                break;
        }
    }


    private void login(String email, String pw) {
        //                         http://서버 ip:포트번호(tomcat 8080포트 사용)/DB연동하는 jsp파일
        final String SIGNIN_URL = getString(R.string.db_server)+"login_t.jsp";
        final String urlSuffix = "?email=" + email;
        //Log.d("urlSuffix", urlSuffix);

        class SignupUser extends AsyncTask<String, Void, String> {

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
                            Toast toast = Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            JSONArray jArr = new JSONArray(s);;
                            JSONObject json = new JSONObject();

                            json = jArr.getJSONObject(0);

                            if (pw.equals(json.getString("trainerPassword"))) {
                                String phoneNum = json.getString("trainerPhone");
                                String name = json.getString("trainerName");
                                String nickname = json.getString("trainerNickname");

                                Toast.makeText(mContext, name+"님, 환영합니다.", Toast.LENGTH_SHORT).show();

                                User user = new User(email, phoneNum, name, nickname);

                                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                                startActivity(intent); /*회원가입 성공 후 이동할 액티비티_MainActivity2*/;
                            }
                            else {
                                Toast toast = Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
                    String strParams = "email=" + email;

                    OutputStream os = conn.getOutputStream();
                    os.write(strParams.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 통신 체크 : 연결 실패시 null 반환하고 종료
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("Login Process", "통신 오류");
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

        SignupUser su = new SignupUser();
        su.execute(urlSuffix);

    }


    /*로그인 인스턴스 초기화 메서드*/
    private void initData(){
        mOAuthLoginInstance=OAuthLogin.getInstance();
        mOAuthLoginInstance.init(mContext,OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET,OAUTH_CLIENT_NAME);

        naverBtn=(OAuthLoginButton)findViewById(R.id.naverLogin);
        naverBtn.setOAuthLoginHandler(mOAuthLoginHandler); /*핸들러*/
    }

    private OAuthLoginHandler mOAuthLoginHandler=new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken=mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken=mOAuthLoginInstance.getRefreshToken(mContext);
                long expireAt=mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType=mOAuthLoginInstance.getTokenType(mContext);
                Toast.makeText(mContext,"로그인 성공",Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent); /*회원가입 성공 후 이동할 액티비티_MainActivity2*/
            }else {
                String errorCode=mOAuthLoginInstance.getLastErrorCode(mContext).getCode(); /*마지막으로 실패한 로그인의 에러코드*/
                String errorDesc=mOAuthLoginInstance.getLastErrorDesc(mContext); /*마지막으로 실패한 로그인의 에러메세지 반환*/
                //Toast.makeText(mContext,"ErrorCode:"+errorCode+", ErrorDesc:"+errorDesc,Toast.LENGTH_SHORT).show();
            }
        }
    };

}