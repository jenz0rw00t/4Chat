package com.iths.grupp4.a4chat.chatroomlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iths.grupp4.a4chat.R;

import java.util.List;

public class ChatroomViewAdapter extends RecyclerView.Adapter<ChatroomViewHolder> {

    private List<Chatroom> list;

    public ChatroomViewAdapter(@NonNull List<Chatroom> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chatroom_item, viewGroup,false);

        return new ChatroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomViewHolder chatroomViewHolder, int i) {
        Chatroom chatroom = list.get(i);
        chatroomViewHolder.setData(chatroom);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(Chatroom chatroom){
        list.add(chatroom);
        this.notifyItemInserted(list.size()-1);
    }

    public void removeItem(int index){
        if( index >= 0 && index < list.size()) {
            list.remove(index);
            this.notifyItemRemoved(index);
        }
    }

    public void removeItem(String chatroomId) {
        for (int i = 0; i < list.size(); i++) {
            if( list.get(i).chatroomId.equals(chatroomId) ) {
                removeItem(i);
                return;
            }
        }
    }
}
