package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.chatlists.PmReferenceFragment;

import java.util.List;

public class PmViewAdapter extends RecyclerView.Adapter<PmViewHolder> {

    private List<Chatroom> chatroomList;
    private static final String CHATROOM_ID = "ChatroomId";
    private static final String USER_NAME = "UserName";
    private FirebaseFirestore db;
    private FirebaseUser current_user;
    private View view;
    private String TAG;

    public PmViewAdapter(@NonNull List<Chatroom> chatroomList) {
        this.chatroomList = chatroomList;
    }


    @NonNull
    @Override
    public PmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.pms_item, viewGroup, false);

        return new PmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PmViewHolder pmViewHolder, int i) {
        Chatroom chatroom = chatroomList.get(i);
        pmViewHolder.setData(chatroom);

        db = FirebaseFirestore.getInstance();
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        int position = pmViewHolder.getAdapterPosition();
        String chatroomId = chatroomList.get(position).getChatroomId();

        TextView textViewChatroomName = (TextView) view.findViewById(R.id.chatroom_item_name);
        textViewChatroomName.setText(chatroomList.get(position).getChatroomName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString(CHATROOM_ID, chatroomId);
                PmReferenceFragment pmReferenceFragment = new PmReferenceFragment();
                pmReferenceFragment.setArguments(bundle);
                FragmentManager manager = ((MainActivity) v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .addToBackStack("allUsers")
                        .replace(R.id.frameLayout, pmReferenceFragment, "")
                        .commit();

            }
        });
    }


    @Override
    public int getItemCount() {
        return chatroomList.size();
    }

    public void addItem(Chatroom chatroom) {
        chatroomList.add(chatroom);
        this.notifyItemInserted(chatroomList.size() - 1);
    }

    public void removeItem(int index){
        if( index >= 0 && index < chatroomList.size()) {
            chatroomList.remove(index);
            this.notifyItemRemoved(index);
        }
    }

    public void removeItem(String chatroomId) {
        for (int i = 0; i < chatroomList.size(); i++) {
            if( chatroomList.get(i).chatroomId.equals(chatroomId) ) {
                removeItem(i);
                return;
            }
        }
    }

}
