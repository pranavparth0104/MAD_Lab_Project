package com.example.spacegram.model;


import com.google.firebase.Timestamp;

public class Posts {
    private String caption;
    private String imageUrl;
    private String userId;
    private String uid;
    private Timestamp timeadded;
    private  String time;
    private String username;

    public Posts() {
    }


    public Posts(String caption, String imageUrl,String uid, String userId, Timestamp timeadded, String username, String time) {
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeadded = timeadded;
        this.username = username;
        this.time = time;
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Timestamp getTimeadded() {
        return timeadded;
    }

    public void setTimeadded(Timestamp timeadded) {
        this.timeadded = timeadded;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
