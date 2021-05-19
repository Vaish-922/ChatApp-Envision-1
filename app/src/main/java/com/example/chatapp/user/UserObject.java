package com.example.chatapp.user;

public class UserObject {

    private String name, uid, phone;

    private Boolean selected = false;
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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
