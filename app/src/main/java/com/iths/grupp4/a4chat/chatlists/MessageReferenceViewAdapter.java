package com.iths.grupp4.a4chat.chatlists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.iths.grupp4.a4chat.R;

import java.util.List;

public class MessageReferenceViewAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<MessageUserRef> list;

    public MessageReferenceViewAdapter(@NonNull List<MessageUserRef> list){
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        MessageUserRef message = list.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.user.getId())){
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
             return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if (i == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.message_item_received, viewGroup, false);
            return new MessageViewHolder(view);
        } else if (i == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.message_item_sent, viewGroup, false);
            return new MessageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        MessageUserRef messageUserRef = list.get(i);
        messageViewHolder.setData(messageUserRef);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(MessageUserRef messageUserRef){
        list.add(messageUserRef);
        this.notifyItemInserted(list.size()-1);
    }

    public void removeItem(int index){
        if( index >= 0 && index < list.size()) {
            list.remove(index);
            this.notifyItemRemoved(index);
        }
    }

    public void removeItem(String id) {
        for (int i = 0; i < list.size(); i++) {
            if( list.get(i).id.equals(id) ) {
                removeItem(i);
                return;
            }
        }
    }
}
