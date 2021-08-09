package com.example.exercise_android_trainer;

import android.graphics.drawable.Drawable;

public class RankingData {
    String string1; //ranking
    Drawable drawable; //img
    String string2; //name

    public RankingData(Drawable drawable,String string1,String string2){
        this.drawable=drawable;
        this.string1=string1;
        this.string2=string2;
    }

    public String getString1() {
        return string1;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getString2() {
        return string2;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }
}
