package com.iths.grupp4.a4chat.chatlists;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.allusers.AllUsers;

import java.util.ArrayList;
import java.util.List;


public class ChatroomReferenceFragment extends Fragment {


    public ChatroomReferenceFragment() {
        // Required empty public constructor
    }

    List<MessageUserRef> messagesList = new ArrayList<>();
    private MessageReferenceViewAdapter adapter;
    FirebaseFirestore db;
    EditText messageField;
    AllUsers user;
    DocumentReference userRef;
    private static final String CHATROOM_ID = "ChatroomId";
    private String chatroomId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            chatroomId = bundle.getString(CHATROOM_ID,null);
        }

        db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = db.collection("users").document(userID);

        messageField = getActivity().findViewById(R.id.messageField);

        //Set adapter for recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new MessageReferenceViewAdapter(messagesList);
        recyclerView.setAdapter(adapter);

        //Sets the recyclerview to bottom if keybord is visible
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //If bottom < oldBottom, keyboard is up.
                if (bottom < oldBottom) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (messagesList.size() > 0) {
                                recyclerView.smoothScrollToPosition(
                                        recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });

        //Register for change events for documents stored in collection items on firestore
        db.collection("chatrooms")
                .document(chatroomId)
                .collection("messagesUserRef")
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

                                MessageUserRef messageUserRef = dc.getDocument().toObject(MessageUserRef.class);
                                messageUserRef.id = id;
                                adapter.addItem(messageUserRef);
                                recyclerView.smoothScrollToPosition(messagesList.size() - 1);

                            } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                String id = dc.getDocument().getId();
                                adapter.removeItem(id);
                            }
                        }
                    }
                });

        getActivity().findViewById(R.id.button2).setOnClickListener(view -> {

            
            // Create a new message with username and message
            MessageUserRef info = new MessageUserRef(userRef, messageField.getText().toString());
            messageField.setText("");

            // Add a new document with a generated ID
            db.collection("chatrooms")
                    .document(chatroomId)
                    .collection("messagesUserRef")
                    .add(info)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
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
    public void onDestroyView() {
        super.onDestroyView();
        db.collection("chatrooms")
                .document(chatroomId)
                .collection("active_users")
                .document(userRef.getId())
                .delete();
    }
}
