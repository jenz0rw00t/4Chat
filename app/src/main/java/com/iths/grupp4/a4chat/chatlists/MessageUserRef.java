package com.iths.grupp4.a4chat.chatlists;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageUserRef {

    public DocumentReference user;
    public String message;
    @Exclude public String id;
    @ServerTimestamp public Date timeStamp;

    public MessageUserRef() {}

    public MessageUserRef(DocumentReference user, String message) {
        this.user = user;
        this.message = message;
    }

    public DocumentReference getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
