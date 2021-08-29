package com.example.exercise_android_trainer;

public class ListViewData3 {

    String startTime;
    String endTime;
    String selectedDay;

    ListViewData3(String s,String e,String sel){
        startTime=s;
        endTime=e;
        selectedDay=sel;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSelectedDay() {
        return selectedDay;
    }

}

