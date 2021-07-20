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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarActivity extends AppCompatActivity {

    // TODO: 알람 수정 기능 추가, DBHelper 수정
    // TODO: 2021-07-20 <일정 테스트>
    // TODO: 1. 일정이 제대로 저장 되는가, 시간을 설정하지 않아도 제대로 저장 되는가 -> ok
    // TODO: 2. 일정이 제대로 삭제되는가, 일정 수정 시 다이얼로그가 적절하게 호출되는가 -> ok
    // TODO: 3. 일정 수정 시 제목과 내용, 시간이 제대로 수정되어 db에 반영되는가  -> ok
    // TODO: 4. 알람 ON 으로 수정 시
    // TODO:    4.1 기존 알람이 취소되고, 새로 설정한 알람이 제대로 실행되는가 -> ok
    // TODO:    4.2 기존에 설정한 알람이 없다면 새로 설정한 알람이 제대로 실행되는가 -> ok
    // TODO: 5. 알람 OFF 로 수정 시
    // TODO:    5.1 기존에 설정한 알람이 제대로 취소되는가 -> ok
    // TODO: 6. 앱이 꺼져도 알람이 제대로 실행되는가 -> ok

    // TODO: 2021-07-21 : 알람 실행 시 notification 타이틀이 해당 알람의 제목으로 출력이 안되는 에러 수정
    // TODO: 2021-07-22 : 일정 저장 시 캘린더에 dotSpan 추가

    public static Context contextCalendar;

    EditText titleText,contentText; /*제목, 내용 입력*/
    Button timeselectBtn; /*스케줄 추가,시간 선책,일정 보기 버튼*/
    ImageButton listOpenBtn,scheduleAddBtn; /*일정 목록 열기, 일정 저장 이미지 버튼*/
    RadioGroup radioGroup1; /*알람 ON/OFF 설정, 수정 라디오그룹*/
    MaterialCalendarView calendarView; /*Material 캘린더뷰*/
    DBHelper dbHelper; /*데이터베이스 테이블 생성/업뎃 클래스*/
    SQLiteDatabase db; /*SQLiteDatabase*/
    TextView alarmText,monthText,dateText; /*선택한 시간 정보를 텍스트로 입력받을 textView*/
    public String title,setUpAlarmString;
    public int requestCode1;
    String content,time,sYear,sMonth,sDay;
    String schedule="schedule";
    CalendarDay datee;
    String sHour,sMinute,alarm,originHour,originMinute; /*알람용 문자열 변수*/
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

    AlertDialog scheduleListDialog;

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
        radioGroup1=(RadioGroup)findViewById(R.id.alarmGroup); //0718 수정 -> 체크박스 -> 라디오버튼 변경
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

                int id=radioGroup1.getCheckedRadioButtonId();

                if (title.isEmpty() || content.isEmpty()){
                    Toast.makeText(CalendarActivity.this,"제목 또는 내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!time.isEmpty()){ //알람 시간을 설정했을 경우
                        if (id==R.id.alarmOnCheck){ //체크박스 on 선택 시 제목,내용,알람시간 모두 db에 저장
                            dbHelper.insertDBcontent(db,title,content,time);
                            setAlarm(); /*setAlarm 함수 호출*/
                            Toast.makeText(CalendarActivity.this,"일정 및 알람 저장",Toast.LENGTH_SHORT).show();
                        }
                        else{ //체크박스 off 선택 시
                            Toast.makeText(CalendarActivity.this,"On에 체크해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    }else{ //알람 시간을 설정하지 않았을 경우
                        if (id==R.id.alarmOnCheck){
                            Toast.makeText(CalendarActivity.this,"알람 시간을 설정해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ContentValues values=new ContentValues();
                            values.put("Title",title);
                            values.put("Content",content);
                            db.insert(schedule+sMonth+sDay,null,values); /*db에 저장*/
                            Toast.makeText(CalendarActivity.this,"일정 및 알람 저장",Toast.LENGTH_SHORT).show();
                        }
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

        scheduleListDialog=builder.create();

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
        scheduleListDialog.setCancelable(false);
        scheduleListDialog.show();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void modifyCalendarDialog(String originTitle, String originContent, String originAlarm){
        //edittext 제목, 내용에는 db에 저장되어 있던 내용이 출력되어야 하고
        //textview 시간도 원래 db에 저장되어 있던 내용이 출력되어야 함
        AlertDialog.Builder builder=new AlertDialog.Builder(CalendarActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.modifycalendar,null);
        builder.setPositiveButton("취소",null);
        builder.setView(view);

        dbHelper=new DBHelper(CalendarActivity.this,dbName,null,dbVersion,sMonth,sDay);
        db=dbHelper.getWritableDatabase();
        String tableName=schedule+sMonth+sDay;

        EditText edt1=(EditText)view.findViewById(R.id.modiEditTitle);
        EditText edt2=(EditText)view.findViewById(R.id.modiEditContent);
        edt1.setText(originTitle);
        edt2.setText(originContent);

        TimePicker timePicker=(TimePicker)view.findViewById(R.id.timePicker);

        if (originAlarm!=null){ //기존의 알람 시간이 설정되어 있다면 기존 시간으로 타임피커 설정
            stringSplit(originAlarm);
            timePicker.setHour(Integer.parseInt(originHour));
            timePicker.setMinute(Integer.parseInt(originMinute));
        }else{ //설정되어 있지 않다면 현재 시간으로 타임피커 설정, if(originalAlarm==null) ->ok
            Calendar calendar=Calendar.getInstance();
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        builder.setNeutralButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title=edt1.getText().toString();
                String content=edt2.getText().toString();
                String time;
                RadioGroup radioGroup2=(RadioGroup)view.findViewById(R.id.modiAlarmGroup);
                int id=radioGroup2.getCheckedRadioButtonId();

                if (id==R.id.modiAlarmOnCheck){ //수정 시 알람 ON
                    if (originAlarm!=null){ //기존에 설정했던 알람이 있는 경우
                        stringSplit(originAlarm);
                        cancelAlarm(originHour,originMinute); //기존 알람 취소
                    }
                    int hour=timePicker.getHour(); //타임피커로부터 시간,분 얻어옴
                    int minute=timePicker.getMinute();
                    sHour=Integer.toString(hour);
                    sMinute=Integer.toString(minute);
                    time=sHour+":"+sMinute;
                    if (originAlarm==null){ //기존의 것 삭제하고 새로 생성
                        dbHelper.deleteDBcontent(db,originTitle);
                        dbHelper.insertDBcontent(db,title,content,time);
                    }else{ //기존 일정 업데이트
                        dbHelper.updateDBcontent(db,title,originTitle,content,originContent,time,originAlarm);
                    }
                    setAlarm(); //새 알람 설정
                }else{ //수정 시 알람 OFF, 기존에 설정했던 알람은 취소
                    if (originAlarm!=null){ //->따로 조건문을 만들까??
                        stringSplit(originAlarm);
                        cancelAlarm(originHour,originMinute); //기존 알람 취소
                    }
                    db.execSQL("UPDATE "+tableName+" SET "+"Title="+"'"+title+"'"+" WHERE Title="+"'"+originTitle+"'"+";");
                    db.execSQL("UPDATE "+tableName+" SET "+"Content="+"'"+content+"'"+" WHERE Content="+"'"+originContent+"'"+";");
                }
                scheduleListDialog.dismiss();
                Toast.makeText(CalendarActivity.this,"수정되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    } //제목, 내용 업데이트 완료, 알람 수정 미완료

    public void cancelAlarm(String originHour,String originMinute){
        String rqCodeInModi=sMonth+sDay+originHour+originMinute+"00"; //기존에 설정했던 알람을 취소하기 위해 요청 코드 재설정
        int requestCode=Integer.parseInt(rqCodeInModi);
        Intent intent=new Intent(contextCalendar,AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(contextCalendar,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT); //0
        alarmManager.cancel(pendingIntent);
    }

    public void stringSplit(String s){
        String[] splitString=s.split(":");
        originHour=splitString[0];
        originMinute=splitString[1];
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

