package com.example.baatein.Notifications;

public class Data {
    private String user,username, body, title, sented;
    private int icon;

    public Data(String user,String username,int icon,String body, String title   ,String sented) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.icon = icon;
        this.username=username;

    }

    public Data() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}


