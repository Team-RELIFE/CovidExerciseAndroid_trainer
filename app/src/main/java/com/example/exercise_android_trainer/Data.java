package com.example.exercise_android_trainer;

public class Data {

   String startTime;
   String endTime;
   String selectedDay;

   public Data(String startTime,String endTime,String selectedDay){
       this.startTime=startTime;
       this.endTime=endTime;
       this.selectedDay=selectedDay;
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

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSelectedDay(String selectedDay) {
        this.selectedDay = selectedDay;
    }
}
