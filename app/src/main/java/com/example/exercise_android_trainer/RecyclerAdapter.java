package com.example.exercise_android_trainer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {

    ArrayList<Data> listData=new ArrayList<>();

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(Data data){
        listData.add(data);
    }


    class Holder extends RecyclerView.ViewHolder{
        TextView Text1,Text2,Text3;

        public Holder(@NonNull View itemView) {
            super(itemView);
            Text1=itemView.findViewById(R.id.hourText1);
            Text2=itemView.findViewById(R.id.hourText2);
            Text3=itemView.findViewById(R.id.dayText);
        }

        void onBind(Data data){
            Text1.setText(data.getStartTime());
            Text2.setText(data.getEndTime());
            Text3.setText(data.getSelectedDay());
        }
    }
}
