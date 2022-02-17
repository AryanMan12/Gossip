package com.example.gossip;

public class UserFriends {
    String username,status,profile_img;

    public UserFriends(){

    }
    public UserFriends(String username, String status, String profile_img) {
        this.username = username;
        this.status = status;
        this.profile_img = profile_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String desc) {
        this.status = desc;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
