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
import com.google.firebase.firestore.FirebaseFirestore;
import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.chatlists.ChatFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatroomViewAdapter extends RecyclerView.Adapter<ChatroomViewHolder> {

    private List<Chatroom> chatroomList;
    private TextView textViewChatroomName;
    private ImageView imageViewDelete;
    private static final String CHATROOM_ID = "ChatroomId";
    private static final String USER_ID = "UserId";
    private static final String USER_NAME = "UserName";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private View view;

    public ChatroomViewAdapter(@NonNull List<Chatroom> chatroomList){
        this.chatroomList = chatroomList;
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
        Chatroom chatroom = chatroomList.get(i);
        chatroomViewHolder.setData(chatroom);

        int position = chatroomViewHolder.getAdapterPosition();
        String chatroomId = chatroomList.get(position).getChatroomId();

        textViewChatroomName = (TextView) view.findViewById(R.id.chatroom_item_name);
        textViewChatroomName.setText(chatroomList.get(position).getChatroomName());

        imageViewDelete = view.findViewById(R.id.chatroom_item_delete);
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser().getUid().equals(chatroomList.get(position).getCreatorId())) {
                    Toast.makeText(view.getContext(),chatroom.getChatroomName() + " deleted",Toast.LENGTH_SHORT).show();
                    db.collection("chatrooms").document(chatroomId).delete();
                    notifyDataSetChanged();
                }
                else {
                    Toast.makeText(view.getContext(),"You can't delete " + chatroomList.get(position).getChatroomId(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> user = new HashMap<>();
                user.put(USER_NAME,auth.getCurrentUser().getDisplayName());

                db.collection("chatrooms")
                        .document(chatroomId)
                        .collection("active_users")
                        .document(auth.getCurrentUser().getUid())
                        .set(user);

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
        return chatroomList.size();
    }

    void addItem(Chatroom chatroom){
        chatroomList.add(chatroom);
        this.notifyItemInserted(chatroomList.size()-1);
    }

    void removeItem(String chatroomId) {
        for (int i = 0; i < chatroomList.size(); i++) {
            if( chatroomList.get(i).chatroomId.equals(chatroomId) ) {
                removeItem(i);
                return;
            }
        }
    }

    private void removeItem(int index){
        if( index >= 0 && index < chatroomList.size()) {
            chatroomList.remove(index);
            this.notifyItemRemoved(index);
        }
    }
}
