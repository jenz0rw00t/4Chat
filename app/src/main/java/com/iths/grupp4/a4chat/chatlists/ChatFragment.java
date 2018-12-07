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

import com.iths.grupp4.a4chat.R;
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

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        String uid = mFirebaseAuth.getCurrentUser().getUid();
        messageField = getActivity().findViewById(R.id.messageField);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            chatroomId = bundle.getString(CHATROOM_ID,null);
        }

        //Set adapter for recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new MyRecyclerViewAdapter(messagesList);
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
                .collection("messages")
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
                        recyclerView.smoothScrollToPosition(messagesList.size() -1);
                    }
                    else if(dc.getType() == DocumentChange.Type.REMOVED){
                        String id = dc.getDocument().getId();
                        adapter.removeItem(id);
                    }
                }
            }
        });

        getActivity().findViewById(R.id.send_button_plane).setOnClickListener(view -> {
            // Create a new message with username and message
            Message chatMessage = new Message(mFirebaseAuth.getCurrentUser().getDisplayName(), messageField.getText().toString());
            messageField.setText("");

            // Add a new document with a generated ID
            db.collection("chatrooms")
                    .document(chatroomId)
                    .collection("messages")
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.collection("chatrooms")
                .document(chatroomId)
                .collection("active_users")
                .document(mFirebaseAuth.getCurrentUser().getUid())
                .delete();
    }
}
