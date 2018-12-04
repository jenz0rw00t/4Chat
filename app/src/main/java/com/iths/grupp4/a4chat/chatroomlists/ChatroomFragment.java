package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.R;

import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment implements ChatroomNameDialog.OnNameReceivedListener {

    private List<Chatroom> chatroomList;
    private ChatroomViewAdapter adapter;
    private FirebaseFirestore db;
    private String TAG;
    private String chatroomId;
    private String userID;
    private DocumentReference userRef;

    public ChatroomFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatroom_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = getActivity().findViewById(R.id.chatroom_recyclerView);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        userRef = db.collection("users").document(userID);

        //Set adapter for recyclerView
        chatroomList = new ArrayList<>();
        adapter = new ChatroomViewAdapter(chatroomList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        db.collection("chatroomsBETA")
                .orderBy("timeStamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                String id = dc.getDocument().getId();

                                Chatroom chatroom = dc.getDocument().toObject(Chatroom.class);
                                chatroom.chatroomId = id;
                                adapter.addItem(chatroom);

                            } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                String id = dc.getDocument().getId();
                                adapter.removeItem(id);
                            }
                        }
                    }
                });

        getActivity().findViewById(R.id.create_chatroom).setOnClickListener(view -> {

            // Create new Chatroom and set data also update to set ChatroomId as data
            Chatroom chatroom = new Chatroom(userRef, userID);
            db.collection("chatroomsBETA")
                    .add(chatroom)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            chatroomId = documentReference.getId();
                            chatroom.setChatroomId(chatroomId);
                            documentReference.update("chatroomId", chatroomId);

                            Bundle bundle = new Bundle();
                            bundle.putString("ChatroomId", chatroomId);
                            ChatroomNameDialog dialog = new ChatroomNameDialog();
                            dialog.setArguments(bundle);
                            dialog.setTargetFragment(ChatroomFragment.this, 1);
                            dialog.show(getFragmentManager(), "ChatroomNameDialog");

                            Log.d("firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("firebase", "Error adding document", e);
                        }
                    });
        });
    }

    @Override
    public void getName(String chatroomId, String chatroomName) {
        db.collection("chatroomsBETA")
                .document(chatroomId)
                .update("chatroomName", chatroomName);
        for (Chatroom chatroom : chatroomList) {
            if (chatroom.getChatroomId().equals(chatroomId)) {
                chatroom.setChatroomName(chatroomName);
            }
        }
        adapter.notifyDataSetChanged();
    }


}
