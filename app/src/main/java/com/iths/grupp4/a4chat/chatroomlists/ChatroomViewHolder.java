package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iths.grupp4.a4chat.R;

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
        textViewName.setText(chatroom.getChatName() + "'s Chatroom");
        textViewCreator.setText(chatroom.getCreatorName());
    }

}
