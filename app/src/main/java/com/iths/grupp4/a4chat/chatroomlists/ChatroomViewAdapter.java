package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.allusers.AllUserAdapter;
import com.iths.grupp4.a4chat.chatlists.ChatFragment;

import java.util.List;

public class ChatroomViewAdapter extends RecyclerView.Adapter<ChatroomViewHolder> {

    private List<Chatroom> list;
    private ImageView imageViewDelete;
    private static final String CHATROOM_ID = "ChatroomId";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private View view;

    public ChatroomViewAdapter(@NonNull List<Chatroom> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chatroom_item, viewGroup,false);

        return new ChatroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomViewHolder chatroomViewHolder, int i) {
        Chatroom chatroom = list.get(i);
        chatroomViewHolder.setData(chatroom);

        int position = chatroomViewHolder.getAdapterPosition();
        String chatroomId = list.get(position).getChatroomId();

        imageViewDelete = view.findViewById(R.id.chatroom_item_delete);
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser().getUid().equals(list.get(position).getCreatorId())) {
                    Toast.makeText(view.getContext(),chatroomId + " deleted",Toast.LENGTH_SHORT).show();
                    removeItem(chatroomId);
                }
                else {
                    Toast.makeText(view.getContext(),auth.getCurrentUser().getUid() + " isn't " + list.get(position).getCreatorId(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),chatroomId + " clicked",Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString(CHATROOM_ID,chatroomId);
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                FragmentManager manager = ((MainActivity) v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .addToBackStack("Chatrooms")
                        .replace(R.id.frameLayout,chatFragment,null)
                        .commit();
            }
        });
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
