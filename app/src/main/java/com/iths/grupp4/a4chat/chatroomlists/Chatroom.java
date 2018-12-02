package com.iths.grupp4.a4chat.chatroomlists;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chatroom {

    public String creatorId;
    public String creatorName;
    public String chatroomName;
    public String chatroomId;
    public DocumentReference creatorReference;
    @ServerTimestamp
    public Date timeStamp;

    public Chatroom() {

    }

    public Chatroom(DocumentReference creatorReference, String creatorId){
        this.creatorReference = creatorReference;
        this.creatorId = creatorId;
    }

    public Chatroom(String creatorName, String creatorId) {
        this.creatorName = creatorName;
        this.creatorId = creatorId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
