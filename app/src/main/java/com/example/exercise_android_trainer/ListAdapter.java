package com.example.exercise_android_trainer;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    ArrayList<ListviewData> arrayList=new ArrayList<>();
    Context context;

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position); /*해당 위치에 존재하는 아이템 리턴*/
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context=parent.getContext();
        ListviewData listviewData=arrayList.get(position);

        /*item2.xml inflate*/
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.item2,parent,false);
        }
        TextView textView1=convertView.findViewById(R.id.workAreaTextinList);
        textView1.setText(listviewData.getText1());
        return convertView;
    }

    public void addItem(ListviewData data){
        arrayList.add(data);
    }
    public void removeItem(int position){
        arrayList.remove(position);
    }
}
