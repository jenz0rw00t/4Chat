package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.chatlists.ChatFragment;

public class ChatroomViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    public TextView textViewName;
    public TextView textViewCreator;
    public ImageView imageViewDelete;

    public ChatroomViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textViewName = itemView.findViewById(R.id.chatroom_item_name);
        textViewCreator = itemView.findViewById(R.id.chatroom_item_creator);
        imageViewDelete = itemView.findViewById(R.id.chatroom_item_delete);
    }

    public void setData(Chatroom chatroom){
        textViewName.setText(chatroom.getChatroomId());
        textViewCreator.setText(chatroom.getCreatorName());
    }

}
