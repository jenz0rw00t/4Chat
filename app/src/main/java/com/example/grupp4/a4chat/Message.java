package com.example.grupp4.a4chat;

public class Message {

    public String user;
    public String message;
    public String id;

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
