package com.example.baatein.Model;

public class UserObject {
    private String name,phone;

    public UserObject(String name,String phone){
        this.name=name;
        this.phone=phone;

    }
    public UserObject(){}

    public String getName(){
        return name;
    }
    public String getPhone(){
        return phone;
    }
    public void setName(){
        this.name=name;
    }
    public void setPhone(){
        this.phone=phone;
    }
}
