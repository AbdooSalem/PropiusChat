package com.abdoo.android.propius;


import android.util.Log;

public class Users {
    public String username;
    public String image;
    public String status;

    public Users() {

    }

    public Users(String username, String image, String status) {

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
}
