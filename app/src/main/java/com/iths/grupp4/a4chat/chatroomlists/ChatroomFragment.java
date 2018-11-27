package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iths.grupp4.a4chat.ChangePhotoDialog;
import com.iths.grupp4.a4chat.R;
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
import com.iths.grupp4.a4chat.UserProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment implements ChatroomNameDialog.OnNameReceivedListener {

    private String TAG;
    private String creatorName;

    public ChatroomFragment() {
        // Required empty public constructor
    }

    private List<Chatroom> chatroomList = new ArrayList<>();
    private ChatroomViewAdapter adapter;
    private FirebaseFirestore db;
    private DocumentReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chatroom_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = getActivity().findViewById(R.id.chatroom_recyclerView);

        db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = db.collection("users").document(userID);

        //Set adapter for recyclerView
        adapter = new ChatroomViewAdapter(chatroomList);
        recyclerView.setAdapter(adapter);

        // Create new Chatroom in database
        db.collection("users").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    creatorName = documentSnapshot.getString("name");

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        //Register for change events for documents stored in collection items on firestore
        db.collection("chatrooms")
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

        getActivity().findViewById(R.id.create_chatroom)
                .setOnClickListener(view -> {


                    // Create new Chatroom and set data also update to set ChatroomId as data
                    Chatroom chatroom = new Chatroom(creatorName, userID);
                    db.collection("chatrooms")
                            .add(chatroom)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    chatroom.setChatroomId(documentReference.getId());
                                    documentReference.update("chatroomId", documentReference.getId());
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
    public void getName(String name) {

    }

    private void openChatroomNameDialog(View view) {
        Log.d(TAG, "onClick: Image button clicked");
        ChatroomNameDialog dialog = new ChatroomNameDialog();
        dialog.setTargetFragment(ChatroomFragment.this, 1);
        dialog.show(getFragmentManager(), "ChatroomNameDialog");
    }
}
