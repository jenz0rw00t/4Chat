package com.iths.grupp4.a4chat.chatlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.iths.grupp4.a4chat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    public TextView textUser;
    public TextView textMessage;
    public ImageView imageUser;
    public TextView textTime;
    public int viewType;

    public MessageViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        this.itemView = itemView;
        this.viewType = viewType;
        textUser = itemView.findViewById(R.id.textUser);
        textMessage = itemView.findViewById(R.id.textMessage);
        imageUser = itemView.findViewById(R.id.imageUser);
        textTime = itemView.findViewById(R.id.textTime);
    }

    public void setData(Message message){
        String userStyle = message.user+":";
        textUser.setText(userStyle);
        textMessage.setText(message.message);
    }

    public void setData(MessageUserRef messageUserRef){
        if (viewType == 2) {
            messageUserRef.user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    textUser.setText((String) documentSnapshot.get("name"));
                    String avatar = (String) documentSnapshot.get("avatar");
                    Picasso.get().load(avatar)
                            .placeholder(R.drawable.default_avatar)
                            .transform(new CropCircleTransformation())
                            .into(imageUser);
                }
            });
        }
        textMessage.setText(messageUserRef.message);
        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }

    }

    public void setDataSent(MessageUserRef messageUserRef) {
        textMessage.setText(messageUserRef.message);
        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }
    }

    public void setDataReceived(MessageUserRef messageUserRef) {
        messageUserRef.user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textUser.setText((String) documentSnapshot.get("name"));
                String avatar = (String) documentSnapshot.get("avatar");
                Picasso.get().load(avatar)
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CropCircleTransformation())
                        .into(imageUser);
            }
        });
        textMessage.setText(messageUserRef.message);
        Date date = messageUserRef.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textTime.setText(time);
        }
    }
}
