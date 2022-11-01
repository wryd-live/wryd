package com.wrydhub.wryd.wrydapp;

public class User {

    String name, lastMessage, lastMsgTime, phoneNo, country;
    int imageId;
    String imageUrl;


    public User(String name, String lastMessage, String lastMsgTime, String phoneNo, String country, int imageId) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMsgTime = lastMsgTime;
        this.phoneNo = phoneNo;
        this.country = country;
        this.imageId = imageId;
        this.imageUrl = "https://api.multiavatar.com/"+ name +".png";
    }
}
