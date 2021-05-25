package com.example.exercise_android_trainer;

import android.graphics.drawable.Drawable;

public class ListViewData4 {

    String t1; /*scheduleTitle*/
    String t2; /*scheduleContent*/
    String t3; /*timeContent*/
    Drawable drawable;

    ListViewData4(String t1,String t2,String t3,Drawable drawable){
        this.t1=t1;
        this.t2=t2;
        this.t3=t3;
        this.drawable=drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public String getT2() {
        return t2;
    }

    public void setT2(String t2) {
        this.t2 = t2;
    }

    public String getT3() {
        return t3;
    }

    public void setT3(String t3) {
        this.t3 = t3;
    }
}
