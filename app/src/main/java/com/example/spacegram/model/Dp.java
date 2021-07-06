package com.example.spacegram.model;


public class Dp {
    private String imageUrl;
    private String userId;
    private String username;

    public Dp() {
    }

    public Dp(String imageUrl, String userId, String username) {
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
