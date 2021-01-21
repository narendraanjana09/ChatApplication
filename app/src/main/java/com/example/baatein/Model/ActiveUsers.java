package com.example.baatein.Model;

public class ActiveUsers {
    String name,number;
    byte[] image;

    public ActiveUsers(String name, String number, byte[] image) {
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public ActiveUsers() {
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
