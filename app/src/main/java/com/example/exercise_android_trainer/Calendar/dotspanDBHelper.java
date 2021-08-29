package com.example.exercise_android_trainer.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * 일정 이벤트 전용 데이터베이스
 */
public class dotspanDBHelper extends SQLiteOpenHelper { //생성된 일정의 월과 일을 저장하는 db

    public static final String MONTH="Month"; //column
    public static final String DAY="Day"; //column
    String tableName="tablenameList"; //table name

    //db 생성
    public dotspanDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE if not exists"+" "+tableName+"("+MONTH+" TEXT,"+DAY+" TEXT);";
        db.execSQL(sql); /*sql문 실행*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE if exists "+tableName;
        db.execSQL(sql);
        onCreate(db);
    }

    public void insertDBcontent(SQLiteDatabase db, String month,String day){ //테이블명 삽입 + 중복값(월,일) 검사
        int i=0;
        Cursor cursor=db.rawQuery("SELECT * FROM "+tableName,null);
        if (cursor!=null){ //데이터가 하나라도 존재할 시
            if (cursor.moveToFirst()){
                do {
                    String m=cursor.getString(cursor.getColumnIndex("Month"));
                    String d=cursor.getString(cursor.getColumnIndex("Day"));
                    if (m.equals(month) && d.equals(day))
                        break;
                    else //동일한 값이 존재하지 않으면 i의 값이 row의 수만큼 증가
                        i++;
                }while (cursor.moveToNext()); //동일한 값이 존재하지 않으면 새로 삽입
            }
            if (i==cursor.getCount()){
                ContentValues values=new ContentValues();
                values.put("Month",month);
                values.put("Day",day);
                db.insert(tableName,null,values); /*db에 저장*/
            }
        }else { //데이터 존재하지 않으면 새로 삽입
            ContentValues values=new ContentValues();
            values.put("Month",month);
            values.put("Day",day);
            db.insert(tableName,null,values); /*db에 저장*/
        }

//        String sql="INSERT "+"INTO "+tableName+"("+MONTH+")"+" VALUES"+"("+"'"+month+"'"+")"+";";
//        String sql="INSERT OR REPLACE "+"INTO "+tableName+"("+MONTH+","+DAY+")"+" VALUES"+"("+"'"+month+"'"+","+"'"+day+"'"+")"+";";
//        db.execSQL(sql);
        Log.w("TAG","Table row insert execute in tableNameList");
    }

    public void deleteDBcontent(SQLiteDatabase db,String month,String day){
        String sql="DELETE FROM " +tableName+ " WHERE Month=" + "'" + month + "'" + " AND " + "Day=" + "'" + day + "'" + ";";
        db.execSQL(sql);
        Log.i("TAG","Table row delete execute in calendar_monthDay.db");
    }
}
