package com.example.exercise_android_trainer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

//리시버 manifest에 등록하기!!
//android:exported 속성을 false로 설정하면 리시버가 내 앱으로부터 전달되는 인텐트만 받음
public class AlarmReceiver extends BroadcastReceiver{

    String alarm_title=((CalendarActivity)CalendarActivity.contextCalendar).title;

    public AlarmReceiver(){}

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID="channel";
    private static String CHANNEL_NAME="Channel1";

    //intent를 받으면 Notification 띄움
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        builder=null;
        manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            manager.createNotificationChannel( //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
                    new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder=new NotificationCompat.Builder(context, CHANNEL_ID);

        }else {
            builder=new NotificationCompat.Builder(context);
        }

        Intent intent2=new Intent(context, MyService.class);
        int requestCode2=intent.getExtras().getInt("requestCode");

        Log.w("AlarmReceiver is Called", String.valueOf(requestCode2));
        PendingIntent pendingIntent=PendingIntent.getActivity(context,requestCode2,intent2,PendingIntent.FLAG_UPDATE_CURRENT); //Activity를 시작하는 인텐트 생성

        builder.setContentTitle(alarm_title); //알림창 제목
        builder.setSmallIcon(R.drawable.clocks);
        builder.setAutoCancel(true); //알림창 터치시 자동 삭제
        builder.setContentIntent(pendingIntent);
        Notification notification=builder.build(); //Notification 객체 생성
        manager.notify(1,notification); //NotificationManager에게 알림 요청

    }
}
