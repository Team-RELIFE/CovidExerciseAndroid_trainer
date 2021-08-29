package com.example.exercise_android_trainer;

public class User {
    public static String id = null;
    public static String phoneNum;
    public static String name;
    public static String nickname;

    //생성자
    public User() {
    }

    //생성자 (신체 정보 X)
    public User(String email, String phoneNum, String name, String nickname) {
        this.id = email;
        this.phoneNum = phoneNum;
        this.name = name;
        this.nickname = nickname;
    }

    //현재 접속 중인 유저 정보 얻기
    public User getCurrentUser() {
        User user = new User(this.id, this.phoneNum, this.name, this.nickname);
        return user;
    }

    //현재 접속 중인 유저 정보 업데이트 (신체 정보 X)
    public void setCurrentUser(String id, String phone, String name) {
        this.id = id;
        this.phoneNum = phone;
        this.name = name;
    }
}
