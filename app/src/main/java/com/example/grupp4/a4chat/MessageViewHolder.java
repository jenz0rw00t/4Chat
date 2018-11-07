package com.example.grupp4.a4chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    public TextView textUser;
    public TextView textMessage;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textUser = itemView.findViewById(R.id.textUser);
        textMessage = itemView.findViewById(R.id.textMessage);
    }

    public void setData(Message message){
        textUser.setText(message.user);
        textMessage.setText(message.message);
    }
}
