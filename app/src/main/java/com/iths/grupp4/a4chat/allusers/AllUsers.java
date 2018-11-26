package com.iths.grupp4.a4chat.allusers;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AllUsers {
    public String name;
    public String avatar;
    public String email;
    public String userId;

    public AllUsers() {
    }

    public AllUsers(String name, String email, String avatar, String userId) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.userId = userId;
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
