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
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    EditText titleText,contentText;
    String filename;
    Button saveBtn,scheduleAddBtn;
    CalendarView calendarView;
    DBHelper dbHelper;
    //TimePickerDialog timePickerDialog;

    final static String dbName="calendar.db";
    final static int dbVersion=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        titleText=(EditText)findViewById(R.id.calendarTitle);
        contentText=(EditText)findViewById(R.id.calendarContent);
        saveBtn=(Button)findViewById(R.id.timeselectBtn);
        scheduleAddBtn=(Button)findViewById(R.id.scheduleAddBtn);
        calendarView=(CalendarView)findViewById(R.id.calendarView);
        dbHelper=new DBHelper(this,dbName,null,dbVersion);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Toast.makeText(CalendarActivity.this,""+year+"/"+(month+1)+"/"+dayOfMonth,0).show();

            }
        });

        scheduleAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db;
                String sql;
                String title=titleText.getText().toString().trim();
                String content=contentText.getText().toString().trim();
                db=dbHelper.getWritableDatabase();
                //dbHelper.onCreate(db);
                ContentValues values=new ContentValues();
                values.put("Title",title);
                values.put("Content",content);
                db.insert("calendar",null,values);
                Toast.makeText(CalendarActivity.this,"저장",Toast.LENGTH_SHORT).show();
            }
        });

       saveBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar c=Calendar.getInstance();
               int mhour=c.get(Calendar.HOUR);
               int mMinute=c.get(Calendar.MINUTE);

               TimePickerDialog timePickerDialog=new TimePickerDialog(CalendarActivity.this, R.style.themeOnverlay_timePicker, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                   }
               },mhour,mMinute,false);

               timePickerDialog.show();
           }
       });
    }

    static class DBHelper extends SQLiteOpenHelper{
        public  DBHelper(Context context,String name,SQLiteDatabase.CursorFactory cursorFactory,int version){
            super(context,name,cursorFactory,version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS calendar(title TEXT,content TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS calendar");
            onCreate(db);
        }
    }
}

