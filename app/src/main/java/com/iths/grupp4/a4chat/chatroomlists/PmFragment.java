package com.iths.grupp4.a4chat.chatroomlists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.R;

import java.util.ArrayList;
import java.util.List;

public class PmFragment extends Fragment {

    private List<Chatroom> chatroomList;
    private PmViewAdapter adapter;
    private FirebaseFirestore db;
    private String TAG;
    private String chatroomId;
    private String userID;
    ListenerRegistration reg;


    public PmFragment() {

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

        //Set adapter for recyclerView
        chatroomList = new ArrayList<>();
        adapter = new PmViewAdapter(chatroomList);
        recyclerView.setAdapter(adapter);

        getActivity().findViewById(R.id.create_chatroom).setVisibility(View.INVISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();

        reg = db.collection("pmsBETA").whereArrayContains("users", userID)
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
    }

    @Override
    public void onResume() {
        super.onResume();

        chatroomList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();

        reg.remove();
    }
}
