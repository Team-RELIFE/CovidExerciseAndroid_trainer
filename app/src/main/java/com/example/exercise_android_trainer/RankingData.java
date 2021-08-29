package com.example.exercise_android_trainer;

import android.graphics.drawable.Drawable;

public class RankingData {
    Drawable ranking; //ranking
    Drawable drawable; //img
    String string2; //name

    public RankingData(Drawable drawable,Drawable ranking,String string2){
        this.drawable=drawable;
        this.ranking=ranking;
        this.string2=string2;
    }

    public Drawable getRanking() {
        return ranking;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getString2() {
        return string2;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

}
