package com.example.grupp4.a4chat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    public String user;
    public String message;
    public String id;
    @ServerTimestamp
    public Date timeStamp;

    public Message() {}

    public Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
