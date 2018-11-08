package com.example.grupp4.a4chat;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

class ListItem {

    private String user;
    private String title;
    private String message;

    ListItem(String user, String title, String message) {
        this.user = user;
        this.title = title;
        this.message = message;
    }

    String getUser() {
        return user;
    }

    String getDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return sdf.format(date);
    }

    String getTitle() {
        return title;
    }

    String getMessage() {
        return message;
    }

}
