package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iths.grupp4.a4chat.R;

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
    }

    public void setData(Chatroom chatroom){
        textViewName.setText(chatroom.getChatroomName());
        textViewCreator.setText(chatroom.getCreatorName());
    }
}
