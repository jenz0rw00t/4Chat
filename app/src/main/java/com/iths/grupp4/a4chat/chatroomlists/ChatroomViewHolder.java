package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.iths.grupp4.a4chat.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ChatroomViewHolder extends RecyclerView.ViewHolder {

    private View itemView;
    private TextView textViewName;
    private TextView textViewCreator;
    private TextView textViewCreatedOn;
    private ImageView imageViewDelete;

    ChatroomViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textViewName = itemView.findViewById(R.id.chatroom_item_name);
        textViewCreator = itemView.findViewById(R.id.chatroom_item_creator);
        textViewCreatedOn = itemView.findViewById(R.id.chatroom_item_createdOn);
        imageViewDelete = itemView.findViewById(R.id.chatroom_item_delete);
    }

    public void setData(Chatroom chatroom){
        chatroom.creatorReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textViewCreator.setText((String) documentSnapshot.get("name"));
            }
        });
        textViewName.setText(chatroom.getChatroomName());
        Date date = chatroom.timeStamp;
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(date);
            textViewCreatedOn.setText(time);
        }
    }
}
