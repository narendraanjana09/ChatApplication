package com.example.baatein.Model;

import java.io.Serializable;

public class User implements Serializable {

    public String name;
    public String number;
    public String dob;
    public String gender;
    public String imagelink;
    public String status;
    public String statusVideoLink;



    public User(String name, String number, String dob, String gender, String imagelink, String status,String statusVideoLink) {
        this.name = name;
        this.number = number;
        this.dob = dob;
        this.gender = gender;
        this.imagelink = imagelink;
        this.status = status;
        this.statusVideoLink = statusVideoLink;
    }

    public User() {
    }

    public String getStatusVideoLink() {
        return statusVideoLink;
    }

    public void setStatusVideoLink(String statusVideoLink) {
        this.statusVideoLink = statusVideoLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}