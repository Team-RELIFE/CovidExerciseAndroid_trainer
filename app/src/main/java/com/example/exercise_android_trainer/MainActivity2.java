package com.example.exercise_android_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.exercise_android_trainer.Calendar.CalendarActivity;
import com.example.exercise_android_trainer.board.GetPostsActivity;
import com.example.exercise_android_trainer.board.Post;
import com.example.exercise_android_trainer.reservation.GetItemActivity;
import com.google.android.material.navigation.NavigationView;
import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class MainActivity2 extends AppCompatActivity {

    ImageButton menuIcon; /*메뉴버튼*/
    Toolbar toolbar; /*툴바*/
    DrawerLayout drawerLayout; /*드로어레이아웃*/
    NavigationView navigationView; /*내비게이션뷰*/
    OAuthLogin mOAuthLogin;
    Context nContext;
    TextView mainText1;
    ImageView userInfo_frame;

//    RelativeLayout msgView = findViewById(R.id.messageLayout);
//    Animation animation = Animation.ofFloat(msgView, "translate", -100f);
//    msgView.animate().translationY(-500).withLayer();
//    msgView.animate().translationY(500).withLayer();

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
        userInfo_frame = findViewById(R.id.userInfo_frame);

        TextView userIdTv = (TextView)findViewById(R.id.main2_userID);
        TextView userNicknameTv = (TextView)findViewById(R.id.main2_userNickname);

        User currentUser = new User().getCurrentUser();

        if (currentUser.id != null) {
            String userId = currentUser.id;
            String userNickname = currentUser.nickname;

            userIdTv.setText(userId);
            userNicknameTv.setText(userNickname);

            ConnectServer();
        }
        else {
            userIdTv.setText("Guest");
            userNicknameTv.setText("Guest");
        }
        
        //클릭하면 예약 화면으로 이동
        userInfo_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity2.this, GetItemActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout goBoardLayout;
        goBoardLayout = findViewById(R.id.board_btn);
        goBoardLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, GetPostsActivity.class);
            startActivity(intent);
        });

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
                    Intent intent=new Intent(MainActivity2.this, CalendarActivity.class);
                    startActivity(intent);
                }
//                if (id==R.id.menu_post){
//                    Intent intent=new Intent(MainActivity2.this, GetPostsActivity.class);
//                    startActivity(intent);
//                }
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

    private void showMessageView(int count) {
        TextView countTv = findViewById(R.id.countTv);
        countTv.setText(String.valueOf(count)+"개");
        RelativeLayout msgLayout;
        msgLayout = findViewById(R.id.messageLayout);
        msgLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, GetItemActivity.class);
            startActivity(intent);
        });
    }


    // 예약 신청 내역 알림을 위한 db연동
    public void ConnectServer(){

        //                         http://서버 ip:포트번호(tomcat 8080포트 사용)/DB연동하는 jsp파일
        final String SIGNIN_URL = getString(R.string.db_server)+"showReservation.jsp";
        final String urlSuffix = "?type=1&id=" + User.id;
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
                            Toast.makeText(getApplicationContext(), "내역이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            JSONArray jArr = new JSONArray(s);
                            JSONObject json = new JSONObject();

                            int status;
                            int reserve_count = 0;
                            for (int i = 0; i < jArr.length(); i++) {
                                json = jArr.getJSONObject(i);

                                // 확인하지 않은 예약 신청의 개수
                                status = json.getInt("status");
                                if (status == 0) {
                                    reserve_count += 1;
                                }
                            }
                            showMessageView(reserve_count);
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
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
                    String strParams = "type=1&id=" + User.id;

                    OutputStream os = conn.getOutputStream();
                    os.write(strParams.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 통신 체크 : 연결 실패시 null 반환하고 종료
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("reserveActivity", "통신 오류");
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