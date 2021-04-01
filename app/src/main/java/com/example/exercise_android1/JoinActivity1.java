package com.example.exercise_android1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

public class JoinActivity1 extends AppCompatActivity {

    ImageButton joinNextBtn;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join1);

        joinNextBtn=(ImageButton)findViewById(R.id.joinNextBtn); /*회원가입 다음단계로 이동*/
        ImageView[] imageViews=new ImageView[5];
        /*아이콘 색상 변경*/
        imageViews[0]=(ImageView)findViewById(R.id.JoinImage1);
        imageViews[1]=(ImageView)findViewById(R.id.JoinImage2);
        imageViews[2]=(ImageView)findViewById(R.id.JoinImage3);
        imageViews[3]=(ImageView)findViewById(R.id.JoinImage4);
        imageViews[4]=(ImageView)findViewById(R.id.JoinImage5);

        for(int i=0;i<5;i++)
            imageViews[i].setColorFilter(Color.parseColor("#54BFEF"));

        /*버튼 수가 적어서 switch문 안쓰고 setOnClickListener 사용*/
        joinNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(JoinActivity1.this,JoinActivity2.class);
                startActivity(intent);

            }
        });
    }

    /*뒤로가기 버튼 눌렀을 때 이전 페이지로 돌아간 다음 finish()호출*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}