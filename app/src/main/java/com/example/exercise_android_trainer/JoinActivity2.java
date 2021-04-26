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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JoinActivity2 extends AppCompatActivity {

    ImageButton joinOkBtn,galleryBtn,NicknameModifyBtn; /*회원가입 완료,갤러리 이동,닉네임 수정 버튼*/
    Button addBtn,workaddBtn,prizeaddBtn; /*시간 등록,근무지 추가 버튼*/
    ListView listView,listView2,listView3;
    ListAdapter listAdapter; /*listview용 어댑터*/
    ListAdapter2 listAdapter2;
    ListAdapter3 listAdapter3;
    EditText houredt1,houredt2,workAreaEdit,prizeEdit;
    String edt1,edt2,sp,edt3,edt4; /*edittext,spinner를 string형태로 변환*/
    Spinner spinner; /*요일 선택 스피너*/
    ArrayAdapter arrayAdapter; /*spinner용 어댑터*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);
        
        joinOkBtn=(ImageButton)findViewById(R.id.JoinOkBtn);
        galleryBtn=(ImageButton)findViewById(R.id.galleryBtn);
        NicknameModifyBtn=(ImageButton)findViewById(R.id.nickname_modify);
        addBtn=(Button)findViewById(R.id.timeaddBtn);
        workaddBtn=(Button)findViewById(R.id.workAddBtn);
        prizeaddBtn=(Button)findViewById(R.id.prizeAddBtn);

        //recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        //recyclerAdapter=new RecyclerAdapter();
        //arrayList=new ArrayList<>();

        //recyclerDecoration=new RecyclerDecoration(10);
        //dividerItemDecoration=new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(this).getOrientation());

        //linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false); /*리스트 형태 수직으로*/
        //recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setAdapter(recyclerAdapter); /*리사이클러뷰와 어댑터 연결*/

        spinner=(Spinner)findViewById(R.id.daySpinner);
        arrayAdapter=ArrayAdapter.createFromResource(this,R.array.day_list, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter); /*스피너와 어댑터 연결*/

        //recyclerView.addItemDecoration(recyclerDecoration);
        //recyclerView.addItemDecoration(dividerItemDecoration);

        listView=(ListView)findViewById(R.id.listView);
        listAdapter=new ListAdapter();
        listView.setAdapter(listAdapter);
        workAreaEdit=(EditText)findViewById(R.id.workAreaEdit);

        listView2=(ListView)findViewById(R.id.listView2);
        listAdapter2=new ListAdapter2();
        listView2.setAdapter(listAdapter2);
        prizeEdit=(EditText)findViewById(R.id.prizeEdit);

        listView3=(ListView)findViewById(R.id.listView3);
        listAdapter3=new ListAdapter3();
        listView3.setAdapter(listAdapter3);
        /*시간 입력 edittext*/
        houredt1=(EditText)findViewById(R.id.hourEdit1);
        houredt2=(EditText)findViewById(R.id.hourEdit2);

        /*아이콘 배열*/
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

        /*항목 선택 시 삭제*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter.removeItem(position);
                listAdapter.notifyDataSetChanged();
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter2.removeItem(position);
                listAdapter2.notifyDataSetChanged();
            }
        });

        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter3.removeItem(position);
                listAdapter3.notifyDataSetChanged();
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.JoinOkBtn: /*가입 완료 -> 메인 화면으로 이동*/
                        Intent intent1=new Intent(JoinActivity2.this,MainActivity2.class);
                        startActivity(intent1);
                        break;
                    case R.id.galleryBtn: /*클릭 시 갤러리로 이동*/
                        Intent intent2=new Intent();
                        intent2.setType("image/*");
                        intent2.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent2, 1);
                        break;
                    case R.id.timeaddBtn: /*edittext와 spinner의 텍스트 값을 string으로 변환해서 리스트로 출력*/
                        edt1=houredt1.getText().toString().trim();
                        edt2=houredt2.getText().toString().trim();
                        sp=spinner.getSelectedItem().toString();
                        listAdapter3.addItem(new ListViewData3(edt1,edt2,sp));
                        listAdapter3.notifyDataSetChanged();
                        break;
                    case R.id.workAddBtn:
                        edt3=workAreaEdit.getText().toString().trim();
                        listAdapter.addItem(new ListviewData(edt3));
                        listAdapter.notifyDataSetChanged();
                        break;
                    case R.id.prizeAddBtn:
                        edt4=prizeEdit.getText().toString().trim();
                        listAdapter2.addItem(new ListviewData2(edt4));
                        listAdapter2.notifyDataSetChanged();
                        break;
                }
            }
        };
        joinOkBtn.setOnClickListener(onClickListener);
        galleryBtn.setOnClickListener(onClickListener);
        addBtn.setOnClickListener(onClickListener);
        workaddBtn.setOnClickListener(onClickListener);
        prizeaddBtn.setOnClickListener(onClickListener);
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