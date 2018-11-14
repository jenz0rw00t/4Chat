package com.example.grupp4.a4chat.allusers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grupp4.a4chat.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class AllUserListFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference allUsers = db.collection("users");

    private AllUserAdapter mUserAdapter;

    public AllUserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setUpRecyclerView();

        return inflater.inflate(R.layout.fragment_all_user_list, container, false);
    }

    private void setUpRecyclerView(){
        Query query = allUsers.orderBy("name",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<AllUsers> recyclerOptions = new FirestoreRecyclerOptions.Builder<AllUsers>()
                .setQuery(query, AllUsers.class)
                .build();

        mUserAdapter = new AllUserAdapter(recyclerOptions);
        RecyclerView recyclerView = getView().findViewById(R.id.allUser_listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mUserAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUserAdapter.stopListening();
    }
}
