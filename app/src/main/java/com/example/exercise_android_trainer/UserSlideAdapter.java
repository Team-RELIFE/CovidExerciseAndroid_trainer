package com.example.exercise_android_trainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserSlideAdapter extends RecyclerView.Adapter<UserSlideAdapter.Holder> {

    ArrayList<newUserData> arrayList;
    Context context;

    public UserSlideAdapter(Context context, ArrayList<newUserData> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.new_user, parent, false);
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
        ImageView user_img;
        TextView user_nickname;

        public Holder(@NonNull View itemView) {
            super(itemView);
            user_img=itemView.findViewById(R.id.user_img);
            user_nickname=itemView.findViewById(R.id.user_nick);
        }

        void onBind(newUserData data){
            user_img.setImageDrawable(data.getDrawable());
            user_nickname.setText((CharSequence) data.getTextView1());
        }
    }
}
