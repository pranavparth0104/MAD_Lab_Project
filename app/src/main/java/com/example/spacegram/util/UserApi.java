package com.example.spacegram.util;
import android.app.Application;

public class UserApi extends Application {

    private String username;
    private String userid;
    private static UserApi instance;

    public static  UserApi getInstance(){
        if(instance == null){
            instance = new UserApi();
        }
        return  instance;
    }

    public  UserApi(){

    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
