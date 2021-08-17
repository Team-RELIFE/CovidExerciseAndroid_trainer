package com.example.exercise_android_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    UserSlideAdapter userSlideAdapter;
    Handler slideHandler;
    int currentNum=0;

    /*트레이너 랭킹*/
    ArrayList<RankingData> rankingList=new ArrayList<>();
    RecyclerView tr_Recyclerview;
    RankingAdapter rankingAdapter;

    //@BindView(R.id.pt_list_Btn) Button ptListBtn;    //TODO : PT list 버튼 -> 리스트 만들어 연동하기 (우선 PT 화면으로 바로 이동)
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
        slideHandler = new Handler(Looper.getMainLooper()); //handler deprecated
        tr_Recyclerview=(RecyclerView)findViewById(R.id.trainer_rangkingView);
        //mainText1=(TextView)findViewById(R.id.main2_Text1);

        TextView userIdTv = (TextView)findViewById(R.id.main2_userID);
        TextView userNicknameTv = (TextView)findViewById(R.id.main2_userNickname);

        User currentUser = new User().getCurrentUser();

        if (currentUser.id != null) {
            String userId = currentUser.id;
            String userNickname = currentUser.nickname;

            userIdTv.setText(userId);
            userNicknameTv.setText(userNickname);
        }
        else {
            userIdTv.setText("Guest");
            userNicknameTv.setText("Guest");
        }

        /*액션바 대신 툴바 사용*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); /*앱 타이틀 안보이게*/
        //getActionBar사용시 오류//

        //5 new users
        arrayList.add(new newUserData(getDrawable(R.drawable.user1),"헬린이1"));
        arrayList.add(new newUserData(getDrawable(R.drawable.user2),"lala la"));
        arrayList.add(new newUserData(getDrawable(R.drawable.user3),"체리"));
        arrayList.add(new newUserData(getDrawable(R.drawable.user4),"헬스왕"));
        //arrayList.add(new newUserData(getDrawable(R.drawable.user5),"ICE"));
        userSlideAdapter=new UserSlideAdapter(this, arrayList);
        user_Viewpager.setAdapter(userSlideAdapter);
        user_Viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        user_Viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { //auto slide: 3 seconds
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(slideRunnable);
                slideHandler.postDelayed(slideRunnable, 3000);
            }
        });

        tr_Recyclerview.setLayoutManager(new LinearLayoutManager(this));
        rankingList.add(new RankingData(getDrawable(R.drawable.girl),getDrawable(R.drawable.number1),"nanana"));
        rankingList.add(new RankingData(getDrawable(R.drawable.girl2),getDrawable(R.drawable.number2),"메로나"));
        rankingList.add(new RankingData(getDrawable(R.drawable.son),getDrawable(R.drawable.number3),"버거킹"));
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

        //drawer header :: 로그인한 상태라면 유저 이름을 표시, 아니라면(게스트 로그인) "Guest"로 표시
        View drawerHeader = navigationView.getHeaderView(0);
        TextView header_userName = (TextView) drawerHeader.findViewById(R.id.nameTV);
//        User currentUser = new User().getCurrentUser();

        if (currentUser.id != null) {
            header_userName.setText(currentUser.name+" 님");
        }
        else {
            header_userName.setText("Guest 님");
        }


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
                if (id==R.id.menu_post){
                    Intent intent=new Intent(MainActivity2.this, GetPostsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

//        ptListBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity2.this, SessionActivity.class);
//                startActivity(intent);
//            }
//        });

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

    //뷰페이저의 마지막 페이지에 도달하면 처음 페이지로 이동
    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentNum==3)
                currentNum=-1;
            user_Viewpager.setCurrentItem(++currentNum, true);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable, 3000);
    }
}