package com.example.chatapp.user;

public class UserObject {

    private String name, uid, phone;
    public UserObject(String uid,String name,String phone){
        this.name = name;
        this.phone = phone;
        this.uid=uid;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
