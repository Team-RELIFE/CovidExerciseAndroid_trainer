package com.example.exercise_android_trainer.reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.exercise_android_trainer.R;
import com.example.exercise_android_trainer.board.Post;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomItemAdapter extends ArrayAdapter {
    public CustomItemAdapter(Context context, ArrayList<Item> item) {
        super(context, 0, item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservation, parent, false);
        }

        // Get the data item for this position
        Item item = (Item) getItem(position);

        // Lookup view for data population
        TextView post = convertView.findViewById(R.id.postTv2);
        TextView trainee = convertView.findViewById(R.id.traineeTv2);
        Button acceptBtn = convertView.findViewById(R.id.acceptBtn);
        Button rejectBtn = convertView.findViewById(R.id.rejectBtn);
        TextView status0 = convertView.findViewById(R.id.statusTv0);
        TextView status1 = convertView.findViewById(R.id.statusTv1);
        TextView address = convertView.findViewById(R.id.address);
        ImageView alarmImg = convertView.findViewById(R.id.alarmImg);

        // 예약 신청 승인 여부에 따라 다르게 표시
        if (item.getStatus() == 0) { //승인되지 않은 상태
            status0.setVisibility(View.VISIBLE);
            status1.setVisibility(View.INVISIBLE);
            address.setVisibility(View.INVISIBLE);
            alarmImg.setVisibility(View.INVISIBLE);
        } else if (item.getStatus() == 1) { //승인된 상태
            status1.setVisibility(View.VISIBLE);
            status0.setVisibility(View.INVISIBLE);
        }

        acceptBtn.setOnClickListener( view -> {
            ProcessReservation prn = new ProcessReservation();
            prn.ConnectServer(item.getId(), 1);
        });

        rejectBtn.setOnClickListener( view -> {
            ProcessReservation prn = new ProcessReservation();
            prn.ConnectServer(item.getId(), 0);
        });

        // Populate the data into the template view using the data object
        post.setText(String.valueOf(item.getPost()));
        trainee.setText(item.getTrainee());
        // Return the completed view to render on screen
        return convertView;
    }

}
