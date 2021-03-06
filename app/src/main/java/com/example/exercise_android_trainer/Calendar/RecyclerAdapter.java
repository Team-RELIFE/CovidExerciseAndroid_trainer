package com.example.exercise_android_trainer.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise_android_trainer.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> implements ItemTouchHelperListener, OnDialogListener {

    private ArrayList<ListViewData4> arrayList=new ArrayList<>();
    Context context;
    String year,month,day,alarm;
    int requestCode1=((CalendarActivity)CalendarActivity.contextCalendar).requestCode1;
    int iYear=CalendarDay.today().getYear();
    int iMonth,iDay;

    SQLiteDatabase db,db2;
    final static String dbName="calendar3.db"; /*db이름*/
    final static String dbName2="calendar_monthDay";
    DBHelper dbHelper;
    com.example.exercise_android_trainer.Calendar.dotspanDBHelper dotspanDBHelper;

    public RecyclerAdapter(Context context,String month,String day,String alarm){ //수정 -> 매개변수에 alarm 추가
        this.context=context;
        this.month=month;
        this.day=day;
        this.alarm=alarm;
        iMonth=Integer.parseInt(month);
        iDay=Integer.parseInt(day);
        year=Integer.toString(iYear);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 생성
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.item4,parent,false);
        return new RecyclerAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) { //뷰홀더가 재활용될때 실행되는 메서드
        //holder가 관리하는 view의 position에 해당하는 데이터 바인딩
        holder.onBind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    void addItem(ListViewData4 data){
        arrayList.add(data);
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        ListViewData4 data4=arrayList.get(from_position);
        arrayList.remove(from_position);
        arrayList.add(to_position,data4);

        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        String originTitle=arrayList.get(position).getT1();
        String originContent=arrayList.get(position).getT2();
        String originAlarm=arrayList.get(position).getT3();
        ((CalendarActivity)CalendarActivity.contextCalendar).modifyCalendarDialog(originTitle,originContent,originAlarm);
    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {

        //도트 삭제를 위해 CalendarActivity의 캘린더뷰 + 데코레이터에 접근 -> 최근에 저장한 날짜가 삭제되는 오류
        MaterialCalendarView calendarView=((CalendarActivity)CalendarActivity.contextCalendar).calendarView;
        HashMap<String, EventDecorator> eventMap=((CalendarActivity)CalendarActivity.contextCalendar).eventMap;
        String evKey=year+month+day;

        dbHelper=new DBHelper(context,dbName,null,1,month,day);
        db=dbHelper.getReadableDatabase();
        dbHelper.deleteDBcontent(db,arrayList.get(position).getT1());

        if (alarm!=null){ //알람 시간을 설정했을 경우
            //알람 취소, 요청코드 : 월+일+시+00
            String alarmText=arrayList.get(position).getT3(); //선택한 위치의 알람 문자열 get
            if (alarmText!=null){
                alarmText=alarmText.replace(":",""); //알람 문자열 사이의 : 문자 제거
                String srequestCode=month+day+alarmText+"00";
                requestCode1=Integer.parseInt(srequestCode); //요청코드 문자열을 정수로 변환

                AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent intent=new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent=PendingIntent.getBroadcast(context,requestCode1,intent,PendingIntent.FLAG_UPDATE_CURRENT); //0
                alarmManager.cancel(pendingIntent);
            }
        }
        Log.w("alarm is canceled", String.valueOf(requestCode1));
        arrayList.remove(position);
        notifyItemRemoved(position);

        //클릭한 일정 목록이 비었다면
        //일정 여러개를 한꺼번에 삭제하면 도트 삭제 안됨
        //hashmap 에서 value를 계속 덮어쓰는데 이게 문제인가..흠 근데 한개씩은 삭제 잘 되는디
        //아 반복문?흠
        //데코레이터 객체 갱신? 으에에에에
        //가장 마지막으로 삭제한 일정의 데코레이터 객체 가져오기?
        //객체 종류는 상관 없는 것 같은데..
        //왜 삭제 안돼애
        if (arrayList.size()==0 && eventMap.get(evKey)!=null){
            dotspanDBHelper=new dotspanDBHelper(context,dbName2,null,1);
            db2=dotspanDBHelper.getReadableDatabase();
            dotspanDBHelper.deleteDBcontent(db2,month,day);
            calendarView.removeDecorator(eventMap.get(evKey)); //도트 삭제
            eventMap.remove(evKey);
            //System.out.println("Deleted Event Decorator:" + eventMap.get(evKey));
            Log.i("Event Delete","dot is eliminated");
        }
    }

    @Override
    public void onFinish(int position, ListViewData4 item) {

    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView textView1;
        TextView textView2;
        TextView textView3;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView1=itemView.findViewById(R.id.scheduleTitle);
            textView2=itemView.findViewById(R.id.scheduleContent);
            textView3=itemView.findViewById(R.id.timeContent);
        }

        void onBind(ListViewData4 data4){
            textView1.setText(data4.getT1());
            textView2.setText(data4.getT2());
            textView3.setText(data4.getT3());
        }
    }
}
