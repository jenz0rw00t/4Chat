package com.iths.grupp4.a4chat.allusers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.iths.grupp4.a4chat.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.iths.grupp4.a4chat.UserProfileFragment;
import com.iths.grupp4.a4chat.friend.Friends;


public class AllUserListFragment extends Fragment{
    private static final String TAG = "AllUserListFragment";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference allUsers = db.collection("users");

    FirebaseAuth firebaseAuth;
    private AllUserAdapter mUserAdapter;
    public SearchView search_users;

    public AllUserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_user_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        Query query = allUsers;


        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean("from_request")) {
                query = query.whereArrayContains("request_sent", FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        }

        FirestoreRecyclerOptions<Friends> recyclerOptions = new FirestoreRecyclerOptions.Builder<Friends>()
                .setQuery(query, Friends.class)
                .build();

        mUserAdapter = new AllUserAdapter(recyclerOptions);
        RecyclerView recyclerView = view.findViewById(R.id.allUser_listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mUserAdapter);

        onUserClick();

        search_users = view.findViewById(R.id.searchUsers);

        search_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_users.setIconified(false);
            }
        });

        search_users.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                //Returning the recyclerview to its original view. If user types letter, then delets letter.
                if (s.trim().isEmpty()){
                    getList(allUsers);
                    onUserClick();
                    mUserAdapter.startListening();
                    //Getting the name and saving it in searchQuery, then setting it in getList
                }else {
                    CollectionReference usersRef = db.collection("users");
                    Query searchQuery = usersRef.orderBy("searchName").startAt(s.trim().toUpperCase()).endAt(s.trim().toUpperCase() +"\uf8ff");
                    getList(searchQuery);
                    onUserClick();
                    mUserAdapter.startListening();
                }
                return false;
            }
        });
        return view;
    }

    //Sets the recyclerview with a new query
    private void getList(Query q) {

        FirestoreRecyclerOptions<Friends> recyclerOptions = new FirestoreRecyclerOptions.Builder<Friends>()
                .setQuery(q, Friends.class)
                .build();

        mUserAdapter = new AllUserAdapter(recyclerOptions);
        RecyclerView recyclerView = getView().findViewById(R.id.allUser_listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mUserAdapter);
    }
    //Handles click to users viewcard
    public void onUserClick (){
        mUserAdapter.setOnItemClickListener(new AllUserAdapter.OnItemClicklistener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                AllUsers allUsers = snapshot.toObject(AllUsers.class);
                String id = snapshot.getId();
                Bundle bundle = new Bundle();
                bundle.putString("visit_user_id", id);
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (id.equals(userID)) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    UserProfileFragment fragment = new UserProfileFragment();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack("allUsers")
                            .commit();
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AllUserProfileFragment fragment = new AllUserProfileFragment();
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack("allUsers")
                            .commit();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserAdapter.startListening();
    }
}