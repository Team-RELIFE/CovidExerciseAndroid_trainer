package com.example.exercise_android_trainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter2 extends BaseAdapter {
    ArrayList<ListviewData2> arrayList=new ArrayList<>();
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
        ListviewData2 listviewData2=arrayList.get(position);

        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.item3,parent,false);
        }
        TextView textView=convertView.findViewById(R.id.prizeTextInList);
        textView.setText(listviewData2.getText());
        return convertView;
    }

    public void addItem(ListviewData2 data2){
        arrayList.add(data2);
    }
}
