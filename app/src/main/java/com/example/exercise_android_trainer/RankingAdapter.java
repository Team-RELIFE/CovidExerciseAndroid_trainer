package com.example.exercise_android_trainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.Holder> {

    ArrayList<RankingData> arrayList=new ArrayList<>();
    Context context;

    public RankingAdapter(ArrayList<RankingData> arrayList, Context context){
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_ranking, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onBind(arrayList.get(position));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView ranking,tr_name;
        ImageView tr_img;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ranking=itemView.findViewById(R.id.rankingText);
            tr_img=itemView.findViewById(R.id.tr_Img);
            tr_name=itemView.findViewById(R.id.tr_name);
        }

        void onBind(RankingData data){
            ranking.setText(data.getString1());
            tr_img.setImageDrawable(data.getDrawable());
            tr_name.setText(data.getString2());
        }
    }
}
