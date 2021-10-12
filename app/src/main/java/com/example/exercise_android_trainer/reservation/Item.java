package com.example.exercise_android_trainer.reservation;

import java.util.ArrayList;

public class Item {
    //declare private data instead of public to ensure the privacy of data field of each class
    private int id;
    private String trainer;
    private String trainee;
    private int status;
    private int post;

    public Item(int id, String trainer, String trainee, int status, int post) {
        this.id = id;
        this.trainee = trainee;
        this.trainer = trainer;
        this.status = status;
        this.post = post;
    }

    public int getId() { return id; }

    public String getTrainee() {
        return trainee;
    }

    public String getTrainer() { return trainer; }

    public int getStatus() {
        return status;
    }

    public int getPost() { return post; }

    public static ArrayList<com.example.exercise_android_trainer.reservation.Item> getItem() {
        GetItemActivity getItemActivity = new GetItemActivity();
        return getItemActivity.item;
    }
}