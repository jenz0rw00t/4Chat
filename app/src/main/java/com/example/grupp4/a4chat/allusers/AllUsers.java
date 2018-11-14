package com.example.grupp4.a4chat.allusers;

public class AllUsers {
    public String name;
    public String avatar;
    public String email;

    public AllUsers() {
    }

    public AllUsers(String name, String avatar, String email) {
        this.name = name;
        this.avatar = avatar;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
