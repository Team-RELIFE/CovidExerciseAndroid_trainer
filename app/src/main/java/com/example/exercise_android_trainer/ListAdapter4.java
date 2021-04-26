package com.example.exercise_android_trainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter4 extends BaseAdapter {
    ArrayList<ListViewData4> arrayList=new ArrayList<>();
    Context context;
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context=parent.getContext();
        ListViewData4 listViewData4=arrayList.get(position);

        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.item4,parent,false);
        }
        TextView textView1=convertView.findViewById(R.id.scheduleTitle);
        TextView textView2=convertView.findViewById(R.id.scheduleContent);
        textView1.setText(listViewData4.getT1());
        textView2.setText(listViewData4.getT2());
        return convertView;
    }

    public void addItem(ListViewData4 data4){
        arrayList.add(data4);
    }
    public void removeData(ListViewData4 data4){
        arrayList.remove(data4);
    }
}
