package com.example.baatein.Model;

import android.net.Uri;

public class Messages {

    private String sender;
    private String receiver;
    private String message;
    private String imagelink;
    private String imageuri;
    private String videolink;
    private String videouri;
    private String time;
    private boolean isseen;
    private boolean messagedeleted;

    public Messages(String sender, String receiver, String message, String imagelink,String imageuri,String videolink, String videouri, String time, boolean isseen, boolean messagedeleted) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.imagelink=imagelink;
        this.imageuri=imageuri;
        this.videolink=videolink;
        this.videouri=videouri;
        this.time = time;
        this.isseen = isseen;
        this.messagedeleted=messagedeleted;
    }

    public Messages() {
    }

    public String getVideolink() {
        return videolink;
    }

    public void setVideolink(String videolink) {
        this.videolink = videolink;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getVideouri() {
        return videouri;
    }

    public void setVideouri(String videouri) {
        this.videouri = videouri;
    }

    public boolean isMessagedeleted() {
        return messagedeleted;
    }

    public void setMessagedeleted(boolean messagedeleted) {
        this.messagedeleted = messagedeleted;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
