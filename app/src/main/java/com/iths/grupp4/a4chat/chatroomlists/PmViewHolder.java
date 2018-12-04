package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.iths.grupp4.a4chat.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PmViewHolder extends RecyclerView.ViewHolder {

    private View itemView;
    private TextView textViewName;
    private TextView textViewCreator;
    private TextView textViewCreatedOn;
    private ImageView imageViewDelete;

    PmViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textViewName = itemView.findViewById(R.id.chatroom_item_name);
        textViewCreator = itemView.findViewById(R.id.chatroom_item_creator);
        textViewCreatedOn = itemView.findViewById(R.id.chatroom_item_createdOn);
        imageViewDelete = itemView.findViewById(R.id.chatroom_item_delete);
    }


    //TODO Sätta användaren som inte är jag som textViewName

    public void setData(Chatroom chatroom){
       chatroom.user1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userID = FirebaseAuth.getInstance().getUid();
                String user1 = (String) documentSnapshot.get("userID");
                String user1Name = (String) documentSnapshot.get("name");
                if (!userID.equals(user1)){
                    textViewName.setText(user1Name);
                }
            }
        });
        chatroom.user2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userID = FirebaseAuth.getInstance().getUid();
                String user2 = (String) documentSnapshot.get("userID");
                String user2Name = (String) documentSnapshot.get("name");
                if (!userID.equals(user2)){
                    textViewName.setText(user2Name);
                }
            }
        });
        Date date = chatroom.timeStamp;
        textViewCreatedOn.setText("");
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm");
            String time = dateFormat.format(date);
            textViewCreatedOn.setText(time);
        }
    }
}
