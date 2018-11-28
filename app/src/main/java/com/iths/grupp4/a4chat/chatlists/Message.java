package com.iths.grupp4.a4chat.chatlists;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    public String user;
    public String message;
    @Exclude public String id;
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
