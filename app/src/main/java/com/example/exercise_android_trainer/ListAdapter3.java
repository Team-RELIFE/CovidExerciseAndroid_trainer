package com.example.exercise_android_trainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter3 extends BaseAdapter {

    ArrayList<ListViewData3> arrayList=new ArrayList<>();
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
        ListViewData3 listViewData3=arrayList.get(position);

        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.item,parent,false);
        }
        TextView textView1=convertView.findViewById(R.id.hourText1);
        TextView textView2=convertView.findViewById(R.id.hourText2);
        TextView textView3=convertView.findViewById(R.id.dayText);
        textView1.setText(listViewData3.getStartTime());
        textView2.setText(listViewData3.getEndTime());
        textView3.setText(listViewData3.getSelectedDay());

        return convertView;
    }

    public void addItem(ListViewData3 data3){
        arrayList.add(data3);
    }
    public void removeItem(int position){
        arrayList.remove(position);
    }
}
