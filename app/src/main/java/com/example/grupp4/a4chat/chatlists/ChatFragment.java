package com.example.grupp4.a4chat.chatlists;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    private List<Message> messagesList = new ArrayList<>();
    private MyRecyclerViewAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    private EditText messageField;

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

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        String uid = mFirebaseAuth.getCurrentUser().getUid();
        messageField = getActivity().findViewById(R.id.messageField);

        //Set adapter for recyclerView
        adapter = new MyRecyclerViewAdapter(messagesList);
        recyclerView.setAdapter(adapter);

        //Register for change events for documents stored in collection items on firestore
        db.collection("messages")
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

                        Message message = dc.getDocument().toObject(Message.class);
                        message.id = id;
                        adapter.addItem(message);
                        //TODO Hur funkar detta
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                    else if(dc.getType() == DocumentChange.Type.REMOVED){
                        String id = dc.getDocument().getId();
                        adapter.removeItem(id);
                    }
                }
            }
        });

        getActivity().findViewById(R.id.button2).setOnClickListener(view -> {
            // Create a new message with username and message
            Message chatMessage = new Message(mFirebaseAuth.getCurrentUser().getDisplayName(), messageField.getText().toString());
            messageField.setText("");

            // Add a new document with a generated ID
            db.collection("messages")
                    .add(chatMessage)
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
}
