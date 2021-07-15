package com.example.exercise_android_trainer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarActivity extends AppCompatActivity {

    public static Context contextCalendar;

    /*아무것도 저장 안했을 때 목록 누르면 오류*/
    /*초기 날짜에 저장하면 테이블 이름이 nullnull로 되는 오류*/
    EditText titleText,contentText; /*제목, 내용 입력*/
    Button timeselectBtn; /*스케줄 추가,시간 선책,일정 보기 버튼*/
    ImageButton listOpenBtn,scheduleAddBtn; /*일정 목록 열기, 일정 저장 이미지 버튼*/
    CheckBox alarmOnCheck; /*알람 ON 체크박스*/
    MaterialCalendarView calendarView; /*Material 캘린더뷰*/
    DBHelper dbHelper; /*데이터베이스 테이블 생성/업뎃 클래스*/
    SQLiteDatabase db; /*SQLiteDatabase*/
    TextView alarmText,monthText,dateText; /*선택한 시간 정보를
    텍스트로 입력받을 textView*/
    public String title,setUpAlarmString;
    public int requestCode1;
    String content,time,sYear,sMonth,sDay;
    String schedule="schedule";
    CalendarDay datee;
    String sHour,sMinute,alarm; /*알람용 문자열 변수*/
    String title2;

    //추가 - 일정목록 다이얼로그 리스트뷰 -> 리사이클러뷰+카드뷰로 수정, 스와이프하여 삭제 수정 버튼 추가//
    RecyclerView.LayoutManager manager;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    //swipeController controller;
    ItemTouchHelper itemTouchHelper;

    final static String dbName="calendar3.db"; /*db이름*/
    final static int dbVersion=1; /*db 버전*/

    /*알람 설정 관련 변수*/
    private AlarmManager alarmManager;
    private GregorianCalendar mCalendar; //윤년에 대해 파악 가능한 달력 클래스

    NotificationManager notificationManager; //알림(Notification)을 관리하는 관리자
    NotificationCompat.Builder notificationBuilder; //Notification 객체 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE); //NotificationManager를 운영체제로부터 호출
        notificationBuilder=null;
        alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE); //AlarmManager를 운영체제로부터 호출
        mCalendar=new GregorianCalendar(); //캘린더 객체 생성
        setContentView(R.layout.activity_calendar);

        contextCalendar=this;
        titleText=(EditText)findViewById(R.id.calendarTitle);
        contentText=(EditText)findViewById(R.id.calendarContent);
        timeselectBtn=(Button)findViewById(R.id.timeselectBtn);
        listOpenBtn=(ImageButton)findViewById(R.id.listOpen);
        scheduleAddBtn=(ImageButton) findViewById(R.id.scheduleSaveBtn);
        alarmOnCheck=(CheckBox)findViewById(R.id.alarmOnCheck);
        calendarView=(MaterialCalendarView) findViewById(R.id.mCalendarView);
        alarmText=(TextView)findViewById(R.id.alarmText);
        monthText=(TextView)findViewById(R.id.monthText1);
        dateText=(TextView)findViewById(R.id.dateText1);

        calendarView.setSelectedDate(CalendarDay.today()); /*달력의 오늘 날짜부터 시작하도록*/
        /*오늘 날짜를 월/일로 텍스트뷰에 출력*/
        int Year=CalendarDay.today().getYear();
        int Month=CalendarDay.today().getMonth()+1;
        int Day=CalendarDay.today().getDay();
        sYear=Integer.toString(Year);
        sMonth=Integer.toString(Month);
        sDay=Integer.toString(Day);
        monthText.setText(sMonth);
        dateText.setText(sDay);

        //calendarView.addDecorator(new EventDecorator(Color.BLUE,Collections.singleton(CalendarDay.today())));

        /*달력의 주말 텍스트 색상 변환*/
        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator()
        );

        /*달력에서 날짜 선택 시 해당 월/일을 각각의 텍스트뷰에 출력*/
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int month=date.getMonth()+1;
                int day=date.getDay();
                datee=date;
                sMonth=Integer.toString(month);
                sDay=Integer.toString(day);
                monthText.setText(sMonth);
                dateText.setText(sDay);
            }
        });

        scheduleAddBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                dbHelper=new DBHelper(CalendarActivity.this,dbName,null,dbVersion,sMonth,sDay);
                db=dbHelper.getWritableDatabase();
                dbHelper.onCreate(db); /*데이터베이스 내에 테이블 생성*/

                title=titleText.getText().toString().trim();
                content=contentText.getText().toString().trim();
                time=alarmText.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty()){
                    Toast.makeText(CalendarActivity.this,"제목 또는 내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (alarmOnCheck.isChecked() && !time.isEmpty()) { /*On에 체크가 되어있고, 시간이 설정되어 있다면*/
                        ContentValues values=new ContentValues();
                        values.put("Title",title);
                        values.put("Content",content);
                        values.put("Alarm",time);
                        db.insert(schedule+sMonth+sDay,null,values); /*db에 저장*/
                        setAlarm(); /*setAlarm 함수 호출*/
                        Toast.makeText(CalendarActivity.this,"일정 및 알람 저장",Toast.LENGTH_SHORT).show();
                    }else if(!alarmOnCheck.isChecked()){ //Off에 체크가 되어있다면 제목하고 내용만 저장
                        ContentValues values=new ContentValues();
                        values.put("Title",title);
                        values.put("Content",content);
                        db.insert(schedule+sMonth+sDay,null,values); /*db에 저장*/
                        Toast.makeText(CalendarActivity.this,"일정 및 알람 저장",Toast.LENGTH_SHORT).show();
                    }else { //On에 체크가 되어 있는데 시간이 설정되어 있지 않다면
                        Toast.makeText(CalendarActivity.this,"알람 시간을 입력해주세요.",Toast.LENGTH_SHORT).show(); //에러메세지 출력
                    }
                    //calendarView.addDecorator(new EventDecorator(Color.BLUE,Collections.singleton(datee))); /*DotSpan 찍기*/
                }
            }
        });

        timeselectBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar c=Calendar.getInstance();
               int mhour=c.get(Calendar.HOUR);
               int mMinute=c.get(Calendar.MINUTE);

               TimePickerDialog timePickerDialog=new TimePickerDialog(CalendarActivity.this, R.style.themeOnverlay_timePicker, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       sHour=Integer.toString(hourOfDay);
                       sMinute=Integer.toString(minute);
                       alarmText.setText(hourOfDay+":"+minute);
                       alarm=sHour+sMinute;
                   }
               },mhour,mMinute,false);
               timePickerDialog.show();
           }
       });

        listOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    /*팝업창 띄우기*/
    public void showDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(CalendarActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.alert_dialog,null);
        builder.setPositiveButton("닫기",null);
        builder.setView(view);
        String tableName=schedule+sMonth+sDay;

        final AlertDialog alertDialog=builder.create();

        dbHelper=new DBHelper(CalendarActivity.this,dbName,null,dbVersion,sMonth,sDay);
        db=dbHelper.getReadableDatabase(); /*읽기 모드*/
        dbHelper.onCreate(db); //db 생성

        Cursor c=db.rawQuery("SELECT * FROM"+" "+tableName,null);

        try {
            if (c!=null){
                recyclerView=(RecyclerView)view.findViewById(R.id.listview_alterdialog_list); //추가
                manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(manager);
                recyclerAdapter=new RecyclerAdapter(contextCalendar,sMonth,sDay,alarm); //수정, db 내 테이블 삭제를 위해 테이블명도 전달 알람!!
                if(c.moveToFirst()){ //핸드폰 내부 저장소로부터 해당 테이블 명으로 테이블 내의 데이터(제목, 내용, 알람시간) 끌어오기
                    do {
                        title2=c.getString(c.getColumnIndex("Title"));
                        String content2=c.getString(c.getColumnIndex("Content"));
                        String alarm2 = c.getString(c.getColumnIndex("Alarm"));
                        recyclerAdapter.addItem(new ListViewData4(title2,content2,alarm2)); //어댑터에 생성한 ListViewData4 객체 저장하고
                        recyclerView.setAdapter(recyclerAdapter); //리사이클러뷰에 어댑터 연결
                    }while (c.moveToNext()); //삭제&수정 스와이프 기능
                    itemTouchHelper=new ItemTouchHelper(new swipeController(recyclerAdapter)); //어댑터 객체를 전달해서 ItemTouchHelper객체 생성한 다음
                    itemTouchHelper.attachToRecyclerView(recyclerView); //ItemTouchHelper 객체를 리사이클러뷰에 부착
                }
            }
        }catch (Exception e){
            Log.w("TAG","no such table",e); //테이블이 존재하지 않으면 로그 메세지 출력
        }
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm(){ /*알람 생성*/
        Intent receiverIntent=new Intent(CalendarActivity.this, AlarmReceiver.class); //리시버로 전달될 인텐트 설정

        setUpAlarmString=sMonth+sDay+sHour+sMinute+"00"; /*추가, requestCode를 날짜+시간으로 수정*/
        requestCode1=Integer.parseInt(setUpAlarmString); //요청코드를 정수로 변환
        receiverIntent.putExtra("requestCode",requestCode1); //요청 코드를 리시버에 전달

        PendingIntent pendingIntent=PendingIntent.getBroadcast(CalendarActivity.this,requestCode1,receiverIntent,PendingIntent.FLAG_UPDATE_CURRENT); /*getBroadcast(fromContext,customRequestcode,toIntent,flag)*/
        Log.w("InCalendarActivity_RC", String.valueOf(requestCode1));

        String from=sYear+"-"+sMonth+"-"+sDay+" "+sHour+":"+sMinute+":"+"00"; /*알람으로 설정한 날짜*/
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        Date datetime=new Date();
        try {
            datetime=dateFormat.parse(from);
        }catch (ParseException e){
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(datetime);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        //alarmManager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pendingIntent); //set은 API 레벨 23부터 도즈모드에서 실행 X
    }

    private void setUpRecyclerView(){
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                itemTouchHelper.onDraw(c, parent, state);
            }
        });
    }

}

