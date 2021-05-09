package com.example.exercise_android_trainer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TITLE="Title";
    public static final String CONTENT="Content";
    public static final String ALARM="Alarm";
    public static String month;
    public static String day;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version,String month,String day) {
        super(context, name, factory, version);
        this.month=month;
        this.day=day;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableName=month+day;
        String schedule="schedule";
        /*테이블 이름 설정 방법*/
        /*데이터 삽입*/
        String sql="CREATE TABLE if not exists"+" "+"schedule"+tableName+"("+TITLE+" TEXT,"+CONTENT+" TEXT,"+ALARM+" TEXT);";
//        String sql="CREATE TABLE if not exists tableName("
//                + "_id integer primary key autoincrement,"
//                + TITLE+" TEXT,"
//                + CONTENT+ " TEXT,"
//                +ALARM+" TEXT);";
        db.execSQL(sql); /*sql문 실행*/
    }

    /*데이터베이스 업그레이드*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE if exists calendarContents";
        db.execSQL(sql);
        onCreate(db);
    }
}
