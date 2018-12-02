package com.iths.grupp4.a4chat.chatlists;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageUserRef {

    public DocumentReference user;
    public String message;
    public boolean isImage;
    @Exclude public String id;
    @ServerTimestamp public Date timeStamp;

    public MessageUserRef() {}

    public MessageUserRef(DocumentReference user, String message) {
        this.user = user;
        this.message = message;
    }

    public MessageUserRef(DocumentReference user, String message, boolean isImage) {
        this.user = user;
        this.message = message;
        this.isImage = isImage;
    }

    public DocumentReference getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
