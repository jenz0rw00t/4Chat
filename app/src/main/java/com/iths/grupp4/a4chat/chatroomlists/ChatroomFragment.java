package com.iths.grupp4.a4chat.chatroomlists;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment implements ChatroomDialogEditName.OnEditNameListener, ChatroomDialogRemove.OnRemoveChatroomListener{

    private List<Chatroom> chatroomList;
    private ChatroomViewAdapter adapter;
    private FirebaseFirestore db;
    private String TAG;
    private String chatroomId;
    private String userID;
    private DocumentReference userRef;
    private SwipeController swipeController;
    private LinearLayoutManager layoutManager;

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
        recyclerView = getActivity().findViewById(R.id.chatroom_recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        userRef = db.collection("users").document(userID);

        //Set adapter for recyclerView
        chatroomList = new ArrayList<>();
        adapter = new ChatroomViewAdapter(chatroomList);
        recyclerView.setAdapter(adapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                super.onRightClicked(position);
                if (userRef.getId().equals(chatroomList.get(position).getCreatorId())) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("Position", position);

                    ChatroomDialogRemove dialog = new ChatroomDialogRemove();
                    dialog.setArguments(bundle);
                    dialog.setTargetFragment(ChatroomFragment.this, 1);
                    dialog.show(getFragmentManager(), "ChatroomDialogEditName");

                } else {

                    Toast.makeText(getContext(), "You can't remove " + chatroomList.get(position).getChatroomName(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                if (userRef.getId().equals(chatroomList.get(position).getCreatorId())) {

                    Bundle bundle = new Bundle();
                    bundle.putString("ChatroomId", chatroomList.get(position).getChatroomId());

                    ChatroomDialogEditName dialog = new ChatroomDialogEditName();
                    dialog.setArguments(bundle);
                    dialog.setTargetFragment(ChatroomFragment.this, 1);
                    dialog.show(getFragmentManager(), "ChatroomDialogEditName");

                } else {

                    Toast.makeText(getContext(), "You can't edit " + chatroomList.get(position).getChatroomName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

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
                                chatroom.setChatroomId(id);
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
                            ChatroomDialogEditName dialog = new ChatroomDialogEditName();
                            dialog.setArguments(bundle);
                            dialog.setTargetFragment(ChatroomFragment.this, 1);
                            dialog.show(getFragmentManager(), "ChatroomDialogEditName");

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
    public void editName(String chatroomId, String chatroomName) {

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

    @Override
    public void removeChatroom(int position) {

        db.collection("chatroomsBETA")
                .document(chatroomList.get(position).getChatroomId())
                .delete();

        chatroomList.remove(position);

        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }
}
