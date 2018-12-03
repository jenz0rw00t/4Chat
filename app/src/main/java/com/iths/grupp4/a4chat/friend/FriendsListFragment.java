package com.iths.grupp4.a4chat.friend;


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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.allusers.AllUserAdapter;
import com.iths.grupp4.a4chat.allusers.AllUsers;
import com.iths.grupp4.a4chat.chatlists.Message;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private FirebaseUser current_user;
    private FriendsAdapter adapter;
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
        usersCollection = db.collection("users").document(current_user.getUid())
                .collection("friends");
        Query friendQuery = db.collection("users").whereArrayContains("friends", current_user.getUid());


        FirestoreRecyclerOptions<AllUsers> options = new FirestoreRecyclerOptions.Builder<AllUsers>()
                .setQuery(friendQuery, AllUsers.class)
                .build();

        adapter = new FriendsAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.friendsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        search_friends = view.findViewById(R.id.searchFriends);

        search_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_friends.setIconified(false);
            }
        });

        search_friends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                //Returning the recyclerview to its original view. If user types letter, then delets letter.
                if (s.trim().isEmpty()){
                    getList(usersCollection);
                    adapter.startListening();
                    //Getting the name and saving it in searchQuery, then setting it in getList
                }else {
                    CollectionReference usersRef = db.collection("users").document(current_user.getUid()).collection("friends");
                    Query searchQuery = usersRef.orderBy("searchName").startAt(s.trim().toUpperCase()).endAt(s.trim().toUpperCase() +"\uf8ff");
                    getList(searchQuery);
                    adapter.startListening();
                }
                return false;
            }
        });

        return view;
    }

    private void getList(Query q) {

        FirestoreRecyclerOptions<AllUsers> recyclerOptions = new FirestoreRecyclerOptions.Builder<AllUsers>()
                .setQuery(q, AllUsers.class)
                .build();

        adapter = new FriendsAdapter(recyclerOptions);
        RecyclerView recyclerView = getView().findViewById(R.id.friendsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


}
