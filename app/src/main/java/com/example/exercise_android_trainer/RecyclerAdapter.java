package com.example.exercise_android_trainer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> implements  ItemTouchHelperListener,OnDialogListener{

    private ArrayList<ListViewData4> arrayList=new ArrayList<>();
    Context context;
    String month,day,alarm;
    int requestCode1=((CalendarActivity)CalendarActivity.contextCalendar).requestCode1;

    SQLiteDatabase db;
    final static String dbName="calendar3.db"; /*db이름*/
    DBHelper dbHelper;

    public RecyclerAdapter(Context context,String month,String day,String alarm){ //수정 -> 매개변수에 alarm 추가
        this.context=context;
        this.month=month;
        this.day=day;
        this.alarm=alarm;
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

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {

        //ListViewData4 data4=arrayList.get(position);
        dbHelper=new DBHelper(context,dbName,null,1,month,day);
        db=dbHelper.getReadableDatabase();
        dbHelper.deleteDBcontent(db,arrayList.get(position).getT1());

        if (alarm!=null){ //알람 시간을 설정했을 경우
            //알람 취소, 요청코드 : 월+일+시+00
            String alarmText=arrayList.get(position).getT3(); //선택한 위치의 알람 문자열 get
            alarmText=alarmText.replace(":",""); //알람 문자열 사이의 : 문자 제거
            String srequestCode=month+day+alarmText+"00";
            requestCode1=Integer.parseInt(srequestCode); //요청코드 문자열을 정수로 변환

            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(context,AlarmReceiver.class);
            //original requestCode=1 , original flag==FLAG_UPDATE_CURRENT
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,requestCode1,intent,PendingIntent.FLAG_UPDATE_CURRENT); //0
            alarmManager.cancel(pendingIntent);
        }

        Log.w("InRecyclerAdapter_RC", String.valueOf(requestCode1));
        arrayList.remove(position);
        notifyItemRemoved(position);
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
