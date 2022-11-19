package com.wrydhub.wryd.wrydapp.models;

public class User {

    public String name, lastMessage, lastMsgTime, phoneNo, country;
    public int imageId;
    public long lastSeenTime;
    public String imageUrl;
    public String notificationType;
    public int personId;
    public int notificationId;

    public User(String name, String lastMessage, String lastMsgTime, long lastSeenTime, String phoneNo, String country, int imageId) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMsgTime = lastMsgTime;
        this.phoneNo = phoneNo;
        this.country = country;
        this.imageId = imageId;
        this.lastSeenTime = lastSeenTime;
        this.imageUrl = "https://api.multiavatar.com/"+ name +".png";
    }

    public void setImageUrl(String newImageUrl)
    {
        this.imageUrl = newImageUrl;
    }

    public void setNotificationType(String pnotification)
    {
        this.notificationType = pnotification;
    }

    public void setPersonId(int myPersonId)
    {
        this.personId=myPersonId;
    }

    public void setNotificationId(int notificationId)
    {
        this.notificationId = notificationId;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }
}
