package com.example.grupp4.a4chat;

import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

class ListItem {

    private ArrayList<Drawable> avatarList;
    private Random rng = new Random();
    private String user;
    private String message;

    ListItem(ArrayList<Drawable> drawableList, String user, String message) {
        this.avatarList = drawableList;
        this.user = user;
        this.message = message;
    }

    Drawable getAvatar() {
        return avatarList.get(rng.nextInt(avatarList.size()));
    }

    String getUser() {
        return user;
    }

    String getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return sdf.format(date);
    }

    String getMessage() {
        return message;
    }

}
