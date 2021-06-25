package com.example.exercise_android_trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

public class MainActivity extends AppCompatActivity {

    Button joinBtn; /*회원가입 버튼*/
    OAuthLoginButton naverBtn;
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
        }
    }

    /*로그인 인스턴스 초기화 메서드*/
    private void initData(){
        mOAuthLoginInstance=OAuthLogin.getInstance();
        mOAuthLoginInstance.init(mContext,OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET,OAUTH_CLIENT_NAME);

        naverBtn=(OAuthLoginButton)findViewById(R.id.naverLogin);
        naverBtn.setOAuthLoginHandler(mOAuthLoginHandler); /*핸들러*/
    }
//깃 푸쉬 나현
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