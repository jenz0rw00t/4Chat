package com.example.grupp4.a4chat.friend;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grupp4.a4chat.R;
import com.example.grupp4.a4chat.allusers.AllUserAdapter;
import com.example.grupp4.a4chat.allusers.AllUserProfileFragment;
import com.example.grupp4.a4chat.allusers.AllUsers;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference friendsCollection;
    private DocumentReference friends;
    private DocumentReference users;
    private FirebaseUser current_user;

    private AllUserAdapter adapter;
    public SearchView search_friends;


    public FriendsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);


        current_user = FirebaseAuth.getInstance().getCurrentUser();

        //FIREBASE
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        friendsCollection = db.collection("friends");
        friends = friendsCollection.document(current_user.getUid());

        Query friendQuery = usersCollection;

        FirestoreRecyclerOptions<AllUsers> options = new FirestoreRecyclerOptions.Builder<AllUsers>()
                .setQuery(friendQuery, AllUsers.class)
                .build();

        adapter = new AllUserAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.friendsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


}
