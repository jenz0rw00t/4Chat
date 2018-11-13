package com.example.grupp4.a4chat;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserFireStore {

    public String name;
    public String email;
    public String avatar;
    public String userId;
    //Timestamp?

    public UserFireStore() {
    }

    public UserFireStore(String name, String email, String avatar, String userId) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.userId = userId;
    }
}