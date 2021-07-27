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

    public static Context contextCalendar;

    EditText titleText,contentText; /*제목, 내용 입력*/
    Button timeselectBtn; /*스케줄 추가,시간 선책,일정 보기 버튼*/
    ImageButton listOpenBtn,scheduleAddBtn; /*일정 목록 열기, 일정 저장 이미지 버튼*/
    RadioGroup radioGroup1; /*알람 ON/OFF 설정, 수정용 라디오버튼 그룹*/
    MaterialCalendarView calendarView; /*Material 캘린더뷰*/
    EventDecorator ev; /*일정 이벤트 데코레이터*/
    DBHelper dbHelper; /*일정관리 전용 db 클래스*/
    dotspanDBHelper dotspanDBHelper; /*테이블명 저장용 db 클래스*/
    SQLiteDatabase db,db2; /*SQLiteDatabase*/
    TextView alarmText,monthText,dateText; /*선택한 시간 정보를 텍스트로 입력받을 textView*/
    public String setUpAlarmString; /*알람 요청코드_문자열*/
    public int requestCode1; /*알람 요청코드_정수*/
    String title,content,time; /*일정_제목, 내용, 알람 시간*/
    String sYear,sMonth,sDay; /*특정 날짜의 db 테이블 생성*/
    String schedule="schedule";
    CalendarDay datee;
    String sHour,sMinute,alarm; /*알람_시/분/시간+분*/
    String originHour,originMinute; /*알람 수정용(원래 시/분)*/

    //일정 목록
    RecyclerView.LayoutManager manager;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;

    final static String dbName="calendar3.db"; /*db이름*/
    final static String dbName2="calendar_monthDay"; /*테이블명 저장용 db 이름*/
    final static int dbVersion=1; /*db 버전*/

    private AlarmManager alarmManager;
    private GregorianCalendar mCalendar; //윤년에 대해 파악 가능한 달력 클래스

    //알람 노티피케이션
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    AlertDialog scheduleListDialog; //일정 목록 다이얼로그

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

        /** 일정이 저장된 날짜에 도트 추가
         1. month, day의 2가지 열을 가진 calendar_monthDay db 생성 및 데이터 중복 검사 -> ok
         2. calendar_monthDay db에 존재하는 날짜에 도트 추가 -> ok
         3. 해당 날짜의 모든 데이터가 삭제될 경우 calendar_monthDay db에서도 동일한 월,일의 데이터 삭제 -> ok
         4. 일정 목록이 비면 해당 날짜의 도트 삭제 -> ok **/
        dotspanDBHelper=new dotspanDBHelper(CalendarActivity.this,dbName2,null,dbVersion);
        db2=dotspanDBHelper.getWritableDatabase();
        dotspanDBHelper.onCreate(db2);
        db2=dotspanDBHelper.getReadableDatabase();
        Cursor cursor=db2.rawQuery("SELECT * FROM"+" "+"tablenameList",null);
        try {
            if (cursor!=null){
             if (cursor.moveToFirst()){
                 do {
                     String m=cursor.getString(cursor.getColumnIndex("Month"));
                     String d=cursor.getString(cursor.getColumnIndex("Day"));
                     Log.i("month",m);
                     Log.i("Day",d);
                     int iMonth=Integer.parseInt(m);
                     int iDay=Integer.parseInt(d);
                     ev=new EventDecorator(Color.BLUE,Collections.singleton(CalendarDay.from(Year,iMonth-1,iDay)));
                     calendarView.addDecorator(ev);
                 }while (cursor.moveToNext());
             }
           }
        }catch (Exception e){
            Log.i("calendar events", "dot print error");
        }

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

                int iMonth=Integer.parseInt(sMonth);
                int iDay=Integer.parseInt(sDay);

                int id=radioGroup1.getCheckedRadioButtonId();

                if (title.isEmpty() || content.isEmpty()){
                    Toast.makeText(CalendarActivity.this,"제목 또는 내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!time.isEmpty()){ //알람 시간을 설정했을 경우
                        if (id==R.id.alarmOnCheck){ //체크박스 on 선택 시 제목,내용,알람시간 모두 db에 저장
                            dbHelper.insertDBcontent(db,title,content,time);
                            setAlarm(); /*setAlarm 함수 호출*/
                            dotspanDBHelper.insertDBcontent(db2,sMonth,sDay);
                            ev=new EventDecorator(Color.BLUE,Collections.singleton(CalendarDay.from(Year,iMonth-1,iDay)));
                            calendarView.addDecorator(ev);
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
                            dotspanDBHelper.insertDBcontent(db2,sMonth,sDay);
                            ev=new EventDecorator(Color.BLUE,Collections.singleton(CalendarDay.from(Year,iMonth-1,iDay)));
                            calendarView.addDecorator(ev);
                            Toast.makeText(CalendarActivity.this,"일정 및 알람 저장",Toast.LENGTH_SHORT).show();
                        }
                    }
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
                recyclerAdapter=new RecyclerAdapter(contextCalendar,sMonth,sDay,alarm); //db 내 테이블 삭제를 위해 테이블명도 전달!!
                if(c.moveToFirst()){ //핸드폰 내부 저장소로부터 해당 테이블 명으로 테이블 내의 데이터(제목, 내용, 알람시간) 끌어오기
                    do {
                        String title2=c.getString(c.getColumnIndex("Title"));
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
        receiverIntent.putExtra("alarmTitle",title); //수정_일정 제목을 리시버에 전달

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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void modifyCalendarDialog(String originTitle, String originContent, String originAlarm){

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
        //수정 시 원래의 제목과 내용을 에디트텍스트에 출력
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
                //수정된 제목과 내용을 스트링 형태로 변환
                title=edt1.getText().toString();
                content=edt2.getText().toString();
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
                    if (originAlarm!=null){
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
    }

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

