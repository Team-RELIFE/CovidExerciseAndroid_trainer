package com.example.exercise_android_trainer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

public class JoinActivity2 extends AppCompatActivity {

    ImageButton joinOkBtn; /*회원가입 완료 버튼*/
    ImageButton galleryBtn; /*갤러리 이동 버튼*/
    ImageButton NicknameModifyBtn; /*닉네임 수정 버튼*/
    RecyclerView recyclerView;
    ArrayList<Data> arrayList;
    RecyclerAdapter recyclerAdapter;
    RecyclerDecoration recyclerDecoration;
    Button addBtn;
    EditText houredt1,houredt2;
    String edt1,edt2,sp;
    LinearLayoutManager linearLayoutManager;
    Spinner spinner;
    ArrayAdapter arrayAdapter;
    DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        joinOkBtn=(ImageButton)findViewById(R.id.JoinOkBtn);
        galleryBtn=(ImageButton)findViewById(R.id.galleryBtn);
        NicknameModifyBtn=(ImageButton)findViewById(R.id.nickname_modify);

        /*리사이클러뷰*/
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerAdapter=new RecyclerAdapter();
        arrayList=new ArrayList<>();

        recyclerDecoration=new RecyclerDecoration(10);
        dividerItemDecoration=new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(this).getOrientation());

        addBtn=(Button)findViewById(R.id.timeaddBtn);/*아이템 추가 버튼*/

        /*시간 입력 edittext*/
        houredt1=(EditText)findViewById(R.id.hourEdit1);
        houredt2=(EditText)findViewById(R.id.hourEdit2);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);


        spinner=(Spinner)findViewById(R.id.daySpinner); /*요일 선택 스피너*/
        arrayAdapter=ArrayAdapter.createFromResource(this,R.array.day_list, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        recyclerView.addItemDecoration(recyclerDecoration);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ImageView[] imageViews=new ImageView[5];

        /*아이콘 색상 변경*/
        imageViews[0]=(ImageView)findViewById(R.id.JoinImage1);
        imageViews[1]=(ImageView)findViewById(R.id.JoinImage2);
        imageViews[2]=(ImageView)findViewById(R.id.JoinImage3);
        imageViews[3]=(ImageView)findViewById(R.id.JoinImage4);
        imageViews[4]=(ImageView)findViewById(R.id.JoinImage5);

        for(int i=0;i<5;i++)
            imageViews[i].setColorFilter(Color.parseColor("#000000"));

        NicknameModifyBtn.setColorFilter(Color.parseColor("#000000"));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1=houredt1.getText().toString().trim();
                edt2=houredt2.getText().toString().trim();
                sp=spinner.getSelectedItem().toString();
                recyclerAdapter.addItem(new Data(edt1,edt2,sp));

                recyclerAdapter.notifyDataSetChanged();
            }
        });

        /*jsp와 연동하여 db에 회원정보 저장하는 알고리즘 필요*/
        /*회원가입 성공 토스트메세지 필요*/
        /*위 과정 완료 후 joinokBtn 누르면 MainActivity2로 이동*/
        joinOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(JoinActivity2.this,MainActivity2.class);
                startActivity(intent);
            }
        });

        /*클릭 시 갤러리로 이동*/
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

    }

    /*갤러리에서 선택한 이미지를 버튼에 띄움*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            if(resultCode==RESULT_OK){
                try {
                    InputStream in=getContentResolver().openInputStream(data.getData());

                    Bitmap img= BitmapFactory.decodeStream(in);
                    in.close();

                    galleryBtn.setImageBitmap(img);
                }catch (Exception e){
                }
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}