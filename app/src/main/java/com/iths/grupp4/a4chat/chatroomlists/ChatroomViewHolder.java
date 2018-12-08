package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatroomViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewName;
    private TextView textViewAdmin;
    private TextView textViewUsername;
    private TextView textViewCreatedOn;

    ChatroomViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.chatroom_item_name);
        textViewAdmin = itemView.findViewById(R.id.chatroom_item_admin);
        textViewUsername = itemView.findViewById(R.id.chatroom_item_username);
        textViewCreatedOn = itemView.findViewById(R.id.chatroom_item_createdOn);
    }

    public void setData(Chatroom chatroom){
        chatroom.creatorReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textViewUsername.setText((String) documentSnapshot.get("name"));
            }
        });
        textViewAdmin.setText(R.string.admin);
        textViewName.setText(chatroom.getChatroomName());
        Date date = chatroom.timeStamp;
        textViewCreatedOn.setText("");
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm");
            String time = dateFormat.format(date);
            textViewCreatedOn.setText(time);
        }
    }
}
