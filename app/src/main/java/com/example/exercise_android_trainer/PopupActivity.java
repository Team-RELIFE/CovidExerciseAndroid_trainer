package com.example.exercise_android_trainer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupActivity extends Activity {

    TextView modelTv;
    TextView priceTv;
    TextView specTv;
    ImageButton shareImageButton;
    ImageButton copyImageButton;
    ImageView itemImage;
    int session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);
//        //UI 객체생성
        modelTv = (TextView)findViewById(R.id.model);
        priceTv = (TextView)findViewById(R.id.price);
        specTv = (TextView) findViewById(R.id.spec);

        //데이터 가져오기
        Intent intent = getIntent();
        String startdate = intent.getStringExtra("startdate");
        String enddate = intent.getStringExtra("enddate");
        session = intent.getIntExtra("session", 0);
//        String img = intent.getStringExtra("img");

        modelTv.setText(startdate);
        priceTv.setText(enddate);
        specTv.setText(String.valueOf(session));

//        shareImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
//                Sharing_intent.setType("text/plain");
//                String Test_Message = modelTv.getText().toString();
//                Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);
//                Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
//                startActivity(Sharing);
//            }
//        });
//
//        copyImageButton.setOnTouchListener(new View.OnTouchListener(){   //터치 이벤트 리스너 등록(누를때)
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {

//                if(event.getAction()==MotionEvent.ACTION_DOWN){ //눌렀을 때 동작
//                    //클립보드 사용 코드
//                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                    ClipData clipData = ClipData.newPlainText("모델명",modelTv.getText().toString());
//                    clipboardManager.setPrimaryClip(clipData);
//                    //복사가 되었다면 토스트메시지 노출
//                    Toast.makeText(getApplicationContext(),"클립보드에 복사되었습니다.",Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });
    }

    //닫기 버튼 클릭
    public void mOnClose(View v){
        //액티비티(팝업) 닫기
        finish();
    }

    //PT룸 접속 버튼 클릭
    public void mGoPT(View v){
        //액티비티(팝업) 닫기
        String url = "https://pt-app.kr:8100/#/" + session;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

//    public void ConnectServer(int id){
//
//        //                         http://서버 ip:포트번호(tomcat 8080포트 사용)/DB연동하는 jsp파일
//        final String SIGNIN_URL = "http://pt-app.kr:8080/" +"getSession.jsp";
//        final String urlSuffix = "?id=" + id;
//        //Log.d("urlSuffix", urlSuffix);
//
//        class SearchReservationRecord extends AsyncTask<String, Void, String> {
//
//            //스레드 관련 및 ui와의 통신을 위한 함수들이 구현되어 있음
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s); // s : DB로부터 리턴된 값
//
//                //s == null -> db 통신 오류, s의 길이는 기본 2이므로 s.length == 2일 때 검색된 값이 없는 것으로 취급
//
//                if (s != null) { //리턴 값이 null이 아니면 jsonArray로 값 목록을 받음
//
//                    try{
//                        if (s.length() <= 2) { //검색된 값이 없음
//                            Toast.makeText(getApplicationContext(), "내역이 없습니다.", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            setPopup(s);
//                            System.out.println("팝업 : " + s);
//                        }
//                    }catch(Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "서버와의 통신에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                BufferedReader bufferedReader = null;
//                String line = null, page = "";
//
//                try {
//                    HttpURLConnection conn = null;
//
//                    URL url = new URL(SIGNIN_URL); //요청 URL을 입력
//                    conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
//                    conn.setRequestProperty("Accept-Charset", "UTF-8");
//                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
//                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
//
//
//                    //strParams에 데이터를 담아 서버로 보냄
//                    String strParams = "id=" + id;
//
//                    OutputStream os = conn.getOutputStream();
//                    os.write(strParams.getBytes("UTF-8"));
//                    os.flush();
//                    os.close();
//
//                    // 통신 체크 : 연결 실패시 null 반환하고 종료
//                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                        Log.d("PopupActivity", "통신 오류");
//                        return null;
//                    }
//
//                    else {
//                        BufferedReader bufreader = new BufferedReader(
//                                new InputStreamReader(
//                                        conn.getInputStream(), "utf-8"));
//                        while ((line = bufreader.readLine()) != null) {
//                            page += line;
//                        }
//
//                        return page;
//                    }
//                }
//
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//        }
//
//        SearchReservationRecord shr = new SearchReservationRecord();
//        shr.execute(urlSuffix);
//    }
}
