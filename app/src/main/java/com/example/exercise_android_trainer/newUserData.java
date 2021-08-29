package com.example.exercise_android_trainer;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class newUserData {
    private Drawable drawable;
    String textView1;

    public newUserData(Drawable drawable,String textView1){
        this.drawable=drawable;
        this.textView1=textView1;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getTextView1() {
        return textView1;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}

