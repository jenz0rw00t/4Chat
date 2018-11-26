package com.iths.grupp4.a4chat.chatroomlists;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chatroom {

    public String creatorId;
    public String creatorName;
    public String chatName;
    public String chatroomId;
    @ServerTimestamp
    public Date timeStamp;

    public Chatroom() {
    }

    public Chatroom(String creatorName, String creatorId) {
        this.creatorName = creatorName;
        this.chatName = creatorName;
        this.creatorId = creatorId;

    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getChatName() {
        return chatName;
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
