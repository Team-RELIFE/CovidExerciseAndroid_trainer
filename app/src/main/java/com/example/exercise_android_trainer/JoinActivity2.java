package com.example.exercise_android_trainer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JoinActivity2 extends AppCompatActivity {

    ImageButton joinOkBtn,galleryBtn,NicknameModifyBtn; /*회원가입 완료,갤러리 이동,닉네임 수정 버튼*/
    Button addBtn,workaddBtn,prizeaddBtn; /*시간 등록,근무지 추가 버튼*/
    ListView listView,listView2,listView3;
    ListAdapter listAdapter; /*listview용 어댑터*/
    ListAdapter2 listAdapter2;
    ListAdapter3 listAdapter3;
    EditText houredt1,houredt2,workAreaEdit,prizeEdit;
    EditText nicknameEt, emailEt, passwordEt, passwordChkEt, phoneNumEt, nameEt;
    String edt1,edt2,sp,edt3,edt4; /*edittext,spinner를 string형태로 변환*/
    String nickname, email, password, passwordChk, phoneNum, name;
    Spinner spinner; /*요일 선택 스피너*/
    ArrayAdapter arrayAdapter; /*spinner용 어댑터*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        //기본 정보 입력 사항
        nicknameEt = findViewById(R.id.nickname);
        emailEt = findViewById(R.id.joinIdEdit);
        passwordEt = findViewById(R.id.joinPassEdit1);
        passwordChkEt = findViewById(R.id.joinPassEdit2);
        phoneNumEt = findViewById(R.id.joinPhoneEdit);
        nameEt = findViewById(R.id.joinNameEdit);

        
        joinOkBtn=(ImageButton)findViewById(R.id.JoinOkBtn);
        galleryBtn=(ImageButton)findViewById(R.id.galleryBtn);
        NicknameModifyBtn=(ImageButton)findViewById(R.id.nickname_modify);
        addBtn=(Button)findViewById(R.id.timeaddBtn);
        workaddBtn=(Button)findViewById(R.id.workAddBtn);
        prizeaddBtn=(Button)findViewById(R.id.prizeAddBtn);

        spinner=(Spinner)findViewById(R.id.daySpinner);
        arrayAdapter=ArrayAdapter.createFromResource(this,R.array.day_list, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter); /*스피너와 어댑터 연결*/

        listView=(ListView)findViewById(R.id.listView);
        listAdapter=new ListAdapter();
        listView.setAdapter(listAdapter);
        workAreaEdit=(EditText)findViewById(R.id.workAreaEdit);

        listView2=(ListView)findViewById(R.id.listView2);
        listAdapter2=new ListAdapter2();
        listView2.setAdapter(listAdapter2);
        prizeEdit=(EditText)findViewById(R.id.prizeEdit);

        listView3=(ListView)findViewById(R.id.listView3);
        listAdapter3=new ListAdapter3();
        listView3.setAdapter(listAdapter3);
        /*시간 입력 edittext*/
        houredt1=(EditText)findViewById(R.id.hourEdit1);
        houredt2=(EditText)findViewById(R.id.hourEdit2);

        /*아이콘 배열*/
        ImageView[] imageViews=new ImageView[5];

        /*아이콘 색상 변경*/
        imageViews[0]=(ImageView)findViewById(R.id.JoinImage1);
        imageViews[1]=(ImageView)findViewById(R.id.JoinImage2);
        imageViews[2]=(ImageView)findViewById(R.id.JoinImage3);
        imageViews[3]=(ImageView)findViewById(R.id.JoinImage4);
        imageViews[4]=(ImageView)findViewById(R.id.JoinImage5);

        for(int i=0;i<5;i++)
            imageViews[i].setColorFilter(Color.parseColor("#000000"));

        NicknameModifyBtn.setColorFilter(Color.parseColor("#000000"));

        /*항목 선택 시 삭제*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter.removeItem(position);
                listAdapter.notifyDataSetChanged();
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter2.removeItem(position);
                listAdapter2.notifyDataSetChanged();
            }
        });

        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter3.removeItem(position);
                listAdapter3.notifyDataSetChanged();
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.JoinOkBtn: /*가입 완료 -> 메인 화면으로 이동*/
                        signUp();
                        break;
                    case R.id.galleryBtn: /*클릭 시 갤러리로 이동*/
                        Intent intent2=new Intent();
                        intent2.setType("image/*");
                        intent2.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent2, 1);
                        break;
                    case R.id.timeaddBtn: /*edittext와 spinner의 텍스트 값을 string으로 변환해서 리스트로 출력*/
                        edt1=houredt1.getText().toString().trim();
                        edt2=houredt2.getText().toString().trim();
                        sp=spinner.getSelectedItem().toString();
                        listAdapter3.addItem(new ListViewData3(edt1,edt2,sp));
                        listAdapter3.notifyDataSetChanged();
                        break;
                    case R.id.workAddBtn:
                        edt3=workAreaEdit.getText().toString().trim();
                        listAdapter.addItem(new ListviewData(edt3));
                        listAdapter.notifyDataSetChanged();
                        break;
                    case R.id.prizeAddBtn:
                        edt4=prizeEdit.getText().toString().trim();
                        listAdapter2.addItem(new ListviewData2(edt4));
                        listAdapter2.notifyDataSetChanged();
                        break;
                }
            }
        };
        joinOkBtn.setOnClickListener(onClickListener);
        galleryBtn.setOnClickListener(onClickListener);
        addBtn.setOnClickListener(onClickListener);
        workaddBtn.setOnClickListener(onClickListener);
        prizeaddBtn.setOnClickListener(onClickListener);
    }

    /*갤러리에서 선택한 이미지를 버튼에 띄움*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            if(resultCode==RESULT_OK){
                try {
                    InputStream in=getContentResolver().openInputStream(data.getData());
                    Bitmap img= BitmapFactory.decodeStream(in);
                    in.close();
                    galleryBtn.setImageBitmap(img);
                }catch (Exception e){
                }
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    //회원가입
    private void signUp(){
        nickname = nicknameEt.getText().toString();
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        passwordChk = passwordChkEt.getText().toString().trim();
        phoneNum = phoneNumEt.getText().toString().trim();
        name = nameEt.getText().toString().trim();

        // 모든 정보 입력 여부 체크
        //TODO : 글자수 체크, 기본키(아이디) 존재 여부 체크
        if(email.length()==0 || password.length()==0 || name.length()==0 || phoneNum.length()==0 || nickname.length()==0) {
            Toast toast = Toast.makeText(getApplicationContext(), "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 비밀번호 체크
        else if (!password.contentEquals(passwordChk)) {
            Toast toast = Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        else {
            Intent intent1=new Intent(JoinActivity2.this,MainActivity2.class);
            connectDB();
            startActivity(intent1);
        }
    }


    private void connectDB(){

        //                         http://서버 ip:포트번호(tomcat 8080포트 사용)/DB연동하는 jsp파일
        final String SIGNIN_URL = getString(R.string.db_server)+"signup.jsp";
        final String urlSuffix = "?email=" + email + "&nickname=" + nickname + "&password=" + password + "&phoneNum=" + phoneNum + "&name=" + name;

        class SignupUser extends AsyncTask<String, Void, String> {

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
                            Toast toast = Toast.makeText(getApplicationContext(), "입력한 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(), "가입이 완료되었습니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }catch(Exception e) {
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
                    String strParams = "email=" + email + "&nickname=" + nickname + "&password=" + password + "&phoneNum=" + phoneNum + "&name=" + name;

                    OutputStream os = conn.getOutputStream();
                    os.write(strParams.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 통신 체크 : 연결 실패시 null 반환하고 종료
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d("JoinActivity", "통신 오류");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}