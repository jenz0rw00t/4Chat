package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.chatlists.ChatroomReferenceFragment;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatroomViewAdapter extends RecyclerView.Adapter<ChatroomViewHolder> {

    private List<Chatroom> chatroomList;
    private static final String CHATROOM_ID = "ChatroomId";
    private static final String USER = "User";
    private FirebaseFirestore db;
    private String userId;
    private DocumentReference current_user;
    private View view;
    private String TAG;
    private int numberOfMessages;

    public ChatroomViewAdapter(@NonNull List<Chatroom> chatroomList) {
        this.chatroomList = chatroomList;
    }


    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chatroom_item, viewGroup, false);

        return new ChatroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomViewHolder chatroomViewHolder, int i) {
        Chatroom chatroom = chatroomList.get(i);
        chatroomViewHolder.setData(chatroom);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        current_user = db.collection("users").document(userId);

        int position = chatroomViewHolder.getAdapterPosition();
        String chatroomId = chatroomList.get(position).getChatroomId();

        TextView textViewChatroomName = (TextView) view.findViewById(R.id.chatroom_item_name);
        textViewChatroomName.setText(chatroomList.get(position).getChatroomName());

        TextView textViewActiveUsers = (TextView) view.findViewById(R.id.chatroom_item_active_users);
        CollectionReference activeUsers = db.collection("chatrooms_BETA")
                .document(chatroomId).collection("active_users");

        activeUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;

                }

                if (!value.isEmpty()) {

                    int numberOfUsers = 0;
                    for (QueryDocumentSnapshot doc : value) {
                        numberOfUsers++;
                    }

                    StringBuilder sb = new StringBuilder();
                    if (numberOfUsers < 2) {
                        sb.append(numberOfUsers).append(" ").append(view.getContext().getText(R.string.active_user));
                    } else {
                        sb.append(numberOfUsers).append(" ").append(view.getContext().getText(R.string.active_users));
                    }

                    textViewActiveUsers.setText(sb.toString());
                } else {

                    textViewActiveUsers.setText(view.getContext().getText(R.string.no_active_users));
                }

            }
        });

        TextView textViewMessages = view.findViewById(R.id.chatroom_item_messages);
        CollectionReference messages = db.collection("chatroomsBETA")
                .document(chatroomId).collection("messagesUserRef");
        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (!value.isEmpty()) {
                    numberOfMessages = 0;
                    for (QueryDocumentSnapshot doc : value) {
                        numberOfMessages++;
                    }

                    StringBuilder sb = new StringBuilder();
                    if (numberOfMessages < 2) {
                        sb.append(numberOfMessages).append(" ").append(view.getContext().getText(R.string.message));
                    } else {
                        sb.append(numberOfMessages).append(" ").append(view.getContext().getText(R.string.messages));
                    }

                    textViewMessages.setText(sb.toString());
                } else {

                    textViewMessages.setText(view.getContext().getText(R.string.no_messages));
                }

            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, DocumentReference> user = new HashMap<>();
                user.put(USER, current_user);

                db.collection("chatroomsBETA")
                        .document(chatroomId)
                        .collection("active_users")
                        .document(userId)
                        .set(user);

                Bundle bundle = new Bundle();
                bundle.putString(CHATROOM_ID, chatroomId);
                ChatroomReferenceFragment chatroomReferenceFragment = new ChatroomReferenceFragment();
                chatroomReferenceFragment.setArguments(bundle);
                FragmentManager manager = ((MainActivity) v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .addToBackStack("Chatrooms")
                        .replace(R.id.frameLayout, chatroomReferenceFragment, null)
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

    public void removeItem(String chatroomId) {
        for (int i = 0; i < chatroomList.size(); i++) {
            if (chatroomList.get(i).chatroomId.equals(chatroomId)) {
                removeItem(i);
            }
        }
    }

    private void removeItem(int index) {
        if (index >= 0 && index < chatroomList.size()) {
            chatroomList.remove(index);
            this.notifyItemRemoved(index);
            this.notifyItemChanged(index);
        }
    }
}
