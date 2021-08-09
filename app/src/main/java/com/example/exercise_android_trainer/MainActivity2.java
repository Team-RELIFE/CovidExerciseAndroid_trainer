package com.example.exercise_android_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.nhn.android.naverlogin.OAuthLogin;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity2 extends AppCompatActivity {

    ImageButton menuIcon; /*메뉴버튼*/
    Toolbar toolbar; /*툴바*/
    DrawerLayout drawerLayout; /*드로어레이아웃*/
    NavigationView navigationView; /*내비게이션뷰*/
    OAuthLogin mOAuthLogin;
    Context nContext;
    TextView mainText1;

    /*신규회원 리스트*/
    ArrayList<newUserData> arrayList=new ArrayList<>();
    ViewPager2 user_Viewpager;

    /*트레이너 랭킹*/
    ArrayList<RankingData> rankingList=new ArrayList<>();
    RecyclerView tr_Recyclerview;
    RankingAdapter rankingAdapter;

    @BindView(R.id.pt_list_Btn) Button ptListBtn;    //TODO : PT list 버튼 -> 리스트 만들어 연동하기 (우선 PT 화면으로 바로 이동)
    private long time=0;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        menuIcon=(ImageButton)findViewById(R.id.menuIcon);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView=(NavigationView)findViewById(R.id.navi_view);
        nContext=this;
        user_Viewpager=(ViewPager2)findViewById(R.id.user_viewpager);
        tr_Recyclerview=(RecyclerView)findViewById(R.id.trainer_rangkingView);
        //mainText1=(TextView)findViewById(R.id.main2_Text1);

        /*액션바 대신 툴바 사용*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); /*앱 타이틀 안보이게*/
        //getActionBar사용시 오류//


//        Animation animation= new AlphaAnimation(0.0f,1.0f);
//        animation.setDuration(2000);
//        mainText1.startAnimation(animation);

        arrayList.add(new newUserData(getDrawable(R.drawable.coach),"닉네임1"));
        arrayList.add(new newUserData(getDrawable(R.drawable.diet),"닉네임2"));
        user_Viewpager.setAdapter(new UserSlideAdapter(this, arrayList));
        user_Viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        tr_Recyclerview.setLayoutManager(new LinearLayoutManager(this));
        rankingList.add(new RankingData(getDrawable(R.drawable.coach),"1","트레이너1"));
        rankingAdapter=new RankingAdapter(rankingList, this);

        tr_Recyclerview.setAdapter(rankingAdapter);
        rankingAdapter.notifyDataSetChanged();


        /*메뉴버튼 눌렀을 때 내비게이션 드로어 오픈*/
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        /*소셜 로그인과 회원 로그인 분리해야 할듯? 일단 로그아웃 시에는 문제가 없음*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id=item.getItemId();

                /*로그아웃*/
                if (id==R.id.main_logout){
                    mOAuthLogin=OAuthLogin.getInstance();
                    mOAuthLogin.logout(nContext);
                    Toast.makeText(nContext,"로그아웃",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity2.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (id==R.id.menu_calendar){
                    Intent intent=new Intent(MainActivity2.this,CalendarActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        ptListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity2.this, SessionActivity.class);
                startActivity(intent);
            }
        });

    }

    /*뒤로가기 버튼 두 번 누르면 앱 종료*/
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*currentTimeMills : 현재 시간 불러옴, 현재시간-사용시간 = 실행시간*/
        if (System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"한번더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        else if (System.currentTimeMillis()-time<2000){
            finishAffinity(); /*어느 액티비티에서든 모든 부모 액티비티 종료*/
        }
    }
}