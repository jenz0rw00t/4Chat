package com.iths.grupp4.a4chat.friend;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.allusers.AllUsers;
import com.iths.grupp4.a4chat.chatlists.PmReferenceFragment;
import com.iths.grupp4.a4chat.chatroomlists.Chatroom;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private FirebaseUser current_user;
    private FriendsAdapter adapter;
    public SearchView search_friends;
    private static final String CHATROOM_ID = "ChatroomId";
    private static final String RECEIVER_ID = "Receiver";
    private static final String USER_NAME = "UserName";
    private String TAG;


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
                if (s.trim().isEmpty()) {
                    getList(friendQuery);
                    adapter.startListening();
                    //Getting the name and saving it in searchQuery, then setting it in getList
                } else {
                    Query searchQuery = friendQuery.orderBy("searchName").startAt(s.trim().toUpperCase()).endAt(s.trim().toUpperCase() + "\uf8ff");
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
        adapter.setOnItemClickListener(new FriendsAdapter.OnItemClicklistener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                db.collection("pmsBETA")
                        .whereArrayContains("users", current_user.getUid() + snapshot.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                String chatroomId = document.getDocuments().get(0).getId();
                                Bundle bundle = new Bundle();
                                bundle.putString(CHATROOM_ID, chatroomId);
                                PmReferenceFragment pmReferenceFragment = new PmReferenceFragment();
                                pmReferenceFragment.setArguments(bundle);
                                FragmentManager manager = getFragmentManager();
                                manager.beginTransaction()
                                        .replace(R.id.frameLayout, pmReferenceFragment, null)
                                        .commit();
                            } else {
                                // Document didn't exist, so it is created
                                DocumentReference user1 = db.collection("users").document(current_user.getUid());
                                DocumentReference user2 = db.collection("users").document(snapshot.getId());
                                Chatroom chatroom = new Chatroom(user1, user2);
                                db.collection("pmsBETA")
                                        .add(chatroom).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.update("users", Arrays.asList(
                                                current_user.getUid(),
                                                snapshot.getId(),
                                                current_user.getUid()+snapshot.getId(),
                                                snapshot.getId()+current_user.getUid()));
                                        Bundle bundle = new Bundle();
                                        bundle.putString(CHATROOM_ID, documentReference.getId());
                                        PmReferenceFragment pmReferenceFragment = new PmReferenceFragment();
                                        pmReferenceFragment.setArguments(bundle);
                                        FragmentManager manager = getFragmentManager();
                                        manager.beginTransaction()
                                                .replace(R.id.frameLayout, pmReferenceFragment, null)
                                                .commit();
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
    }


}
