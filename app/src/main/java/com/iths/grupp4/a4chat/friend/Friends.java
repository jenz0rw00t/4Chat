package com.iths.grupp4.a4chat.friend;

public class Friends {
    public String name;
    public String avatar;
    public String fullSizeAvatar;
    public String email;
    public String userId;
    public String searchName;
    public String token;
    public boolean online;

    public Friends() {
    }

    public Friends(String name, String email, String avatar, String fullSizeAvatar, String userId,
                    String token, boolean online) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.fullSizeAvatar = fullSizeAvatar;
        this.userId = userId;
        this.searchName = name.toUpperCase();
        this.token = token;

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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
