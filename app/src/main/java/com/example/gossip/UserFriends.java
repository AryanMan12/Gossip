package com.example.gossip;

public class UserFriends {
    String username,status;

    public UserFriends(){

    }
    public UserFriends(String username, String status) {
        this.username = username;
        this.status = status;
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

}
