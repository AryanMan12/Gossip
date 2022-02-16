package com.example.gossip;

public class UserFriends {
    String username,desc,profile_img;

    public UserFriends(){

    }
    public UserFriends(String username, String desc, String profile_img) {
        this.username = username;
        this.desc = desc;
        this.profile_img = profile_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
