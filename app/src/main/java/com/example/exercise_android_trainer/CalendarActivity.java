package com.example.exercise_android_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.sql.Time;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    EditText titleText,contentText; /*제목, 내용 입력*/
    Button scheduleAddBtn,timeselectBtn; /*스케줄 추가,시간 선책 버튼*/
    MaterialCalendarView calendarView; /*Material 캘린더뷰*/
    DBHelper dbHelper; /*데이터베이스 테이블 생성/업뎃 클래스*/
    SQLiteDatabase db; /*SQLiteDatabase*/
    TextView alarmText; /*선택한 시간 정보를 텍스트로 입력받을 textView*/

    final static String dbName="calendar3.db"; /*db이름*/
    final static int dbVersion=1; /*db 버전*/

    /*추가한 데이터를 리스트 형태로 보여주기 위한 리스트뷰와 어댑터*/
    ListAdapter4 listAdapter4;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        titleText=(EditText)findViewById(R.id.calendarTitle);
        contentText=(EditText)findViewById(R.id.calendarContent);
        scheduleAddBtn=(Button)findViewById(R.id.scheduleAddBtn);
        timeselectBtn=(Button)findViewById(R.id.timeselectBtn);
        calendarView=(MaterialCalendarView) findViewById(R.id.mCalendarView);
        dbHelper=new DBHelper(this,dbName,null,dbVersion);
        db=dbHelper.getWritableDatabase();
        dbHelper.onCreate(db); /*데이터베이스 내에 테이블 생성*/
        alarmText=(TextView)findViewById(R.id.alarmText);
        listAdapter4=new ListAdapter4();
        listView=(ListView)findViewById(R.id.calendarList);
        listView.setAdapter(listAdapter4); /*리스트뷰와 어댑터 연결*/

        calendarView.setSelectedDate(CalendarDay.today());

        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator()
        );
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                //Toast.makeText(CalendarActivity.this,""+year+"/"+(month+1)+"/"+dayOfMonth,0).show();
//
//            }
//        });

        scheduleAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title=titleText.getText().toString().trim();
                String content=contentText.getText().toString().trim();
                String time=alarmText.getText().toString().trim();
                ContentValues values=new ContentValues();
                values.put("Title",title);
                values.put("Content",content);
                values.put("Alarm",time);
                /*오류*/
                listAdapter4.addItem(new ListViewData4(title,content,time)); /*리스트에 데이터 추가*/
                listAdapter4.notifyDataSetChanged(); /*업데이트*/
                db.insert("calendarContents2",null,values);
                Toast.makeText(CalendarActivity.this,"저장",Toast.LENGTH_SHORT).show();

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
                       alarmText.setText(hourOfDay+":"+minute);
                   }
               },mhour,mMinute,false);
               timePickerDialog.show();
           }
       });
    }

//    static class DBHelper extends SQLiteOpenHelper{
//        public  DBHelper(Context context,String name,SQLiteDatabase.CursorFactory cursorFactory,int version){
//            super(context,name,cursorFactory,version);
//        }
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            db.execSQL("CREATE TABLE IF NOT EXISTS calendar(title TEXT,content TEXT)");
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            db.execSQL("DROP TABLE IF EXISTS calendar");
//            onCreate(db);
//        }
//    }
}

