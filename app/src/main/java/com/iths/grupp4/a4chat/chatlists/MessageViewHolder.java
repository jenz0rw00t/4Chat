package com.iths.grupp4.a4chat.chatlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iths.grupp4.a4chat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

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
        String userStyle = message.user+":";
        textUser.setText(userStyle);
        textMessage.setText(message.message);
    }

    public void setData(MessageUserRef messageUserRef){
        messageUserRef.user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textUser.setText((String) documentSnapshot.get("name")+":");
            }
        });
        textMessage.setText(messageUserRef.message);
    }
}
