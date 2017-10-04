package com.abdoo.android.propius.models;


import android.util.Log;

public class User {
    private String username;
    private String image;
    private String status;
    private String thumb_img;

    public User() {

    }

    public User(String username, String image, String status) {

        this.username = username;
        this.image = image;
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {

        return status;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        Log.d("ffff", username);
        return username;
    }

    public String getThumbImage() {
        return thumb_img;
    }
}
