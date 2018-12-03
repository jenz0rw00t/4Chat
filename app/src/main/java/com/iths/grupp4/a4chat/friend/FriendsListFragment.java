package com.iths.grupp4.a4chat.friend;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.iths.grupp4.a4chat.allusers.AllUsers;
import com.iths.grupp4.a4chat.chatlists.ChatroomPrivateReferenceFragment;
import com.iths.grupp4.a4chat.chatroomlists.Chatroom;

import java.util.HashMap;
import java.util.Map;

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

        Query friendQuery = usersCollection;


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
                    getList(usersCollection);
                    adapter.startListening();
                    //Getting the name and saving it in searchQuery, then setting it in getList
                } else {
                    CollectionReference usersRef = db.collection("users").document(current_user.getUid()).collection("friends");
                    Query searchQuery = usersRef.orderBy("searchName").startAt(s.trim().toUpperCase()).endAt(s.trim().toUpperCase() + "\uf8ff");
                    getList(searchQuery);
                    adapter.startListening();
                }
                return false;
            }
        });

        db.collection("pms").document(current_user.getUid())
                .collection("messagesUserRef")
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

                            } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                String id = dc.getDocument().getId();
                            }
                        }
                    }
                });

        adapter.setOnItemClickListener(new FriendsAdapter.OnItemClicklistener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {

                String uniqueId = snapshot.getId();

                DocumentReference docRef = db.collection("pms").document(uniqueId)
                        .collection("messagesUserRef").document(current_user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Toast.makeText(getContext(), current_user.getUid() + " exists", Toast.LENGTH_SHORT).show();

                                Map<String, String> user = new HashMap<>();
                                user.put(USER_NAME, current_user.getDisplayName());

                                Bundle bundle = new Bundle();
                                bundle.putString(CHATROOM_ID, current_user.getUid());
                                ChatroomPrivateReferenceFragment chatroomPrivateReferenceFragment = new ChatroomPrivateReferenceFragment();
                                chatroomPrivateReferenceFragment.setArguments(bundle);
                                FragmentManager manager = getFragmentManager();
                                manager.beginTransaction()
                                        .addToBackStack("FriendsList")
                                        .replace(R.id.frameLayout, chatroomPrivateReferenceFragment, null)
                                        .commit();

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {

                                Toast.makeText(getContext(), current_user.getUid() + " didn't exist", Toast.LENGTH_SHORT).show();

                                // Document didn't exist, so it is created
                                Chatroom chatroom = new Chatroom(current_user.getDisplayName(), current_user.getUid());
                                db.collection("pms").document(uniqueId)
                                        .collection("messagesUserRef").document(current_user.getUid())
                                        .set(chatroom)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

                                                String chatroomName = "PM with " + snapshot.getString("name");
                                                chatroom.setChatroomId(current_user.getUid());
                                                chatroom.setChatroomName(chatroomName);

                                                Map<String, String> user = new HashMap<>();
                                                user.put(USER_NAME, current_user.getDisplayName());

                                                Bundle bundle = new Bundle();
                                                bundle.putString(CHATROOM_ID, current_user.getUid());
                                                ChatroomPrivateReferenceFragment chatroomPrivateReferenceFragment = new ChatroomPrivateReferenceFragment();
                                                chatroomPrivateReferenceFragment.setArguments(bundle);
                                                FragmentManager manager = getFragmentManager();
                                                manager.beginTransaction()
                                                        .addToBackStack("FriendsList")
                                                        .replace(R.id.frameLayout, chatroomPrivateReferenceFragment, null)
                                                        .commit();

                                                Log.d("firebase", "DocumentSnapshot added with ID: " + uniqueId);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("firebase", "Error adding document", e);
                                            }
                                        });

                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }

                        Toast.makeText(getContext(), "Friend Clicked!", Toast.LENGTH_SHORT).show();
                    }
                });

                /*
                // Create new Chatroom and set data also update to set ChatroomId as data
                Chatroom chatroom = new Chatroom(current_user.getDisplayName(), current_user.getUid());
                db.collection("pms")
                        .document(uniqueId)
                        .set(chatroom)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                String chatroomName = "PM with " + snapshot.getString("name");
                                chatroom.setChatroomId(uniqueId);
                                chatroom.setChatroomName(chatroomName);

                                Toast.makeText(getContext(), "Clicked!", Toast.LENGTH_SHORT).show();

                                Map<String, Object> allowed_users = new HashMap<>();
                                allowed_users.put("User1", current_user.getUid());
                                allowed_users.put("User2", snapshot.getId());

                                db.collection("pms")
                                        .document(uniqueId)
                                        .collection("private")
                                        .document("allowed_users")
                                        .set(allowed_users);

                                Map<String, String> user = new HashMap<>();
                                user.put(USER_NAME, current_user.getDisplayName());

                                db.collection("pms")
                                        .document(uniqueId)
                                        .collection("active_users")
                                        .document(current_user.getUid())
                                        .set(user);

                                Bundle bundle = new Bundle();
                                bundle.putString(CHATROOM_ID, uniqueId);
                                ChatroomReferenceFragment chatroomReferenceFragment = new ChatroomReferenceFragment();
                                chatroomReferenceFragment.setArguments(bundle);
                                FragmentManager manager = getFragmentManager();
                                manager.beginTransaction()
                                        .addToBackStack("Chatrooms")
                                        .replace(R.id.frameLayout, chatroomReferenceFragment, null)
                                        .commit();

                                Log.d("firebase", "DocumentSnapshot added with ID: " + uniqueId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("firebase", "Error adding document", e);
                            }
                        });

                        /*.add(chatroom)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String chatroomId = current_user.getUid() + snapshot.getId();

                                String chatroomName = "PM with " + snapshot.getString("name");
                                chatroom.setChatroomId(chatroomId);
                                chatroom.setChatroomName(chatroomName);
                                documentReference.update("chatroomId", chatroomId);
                                documentReference.update("chatroomName",chatroomName);

                                Toast.makeText(getContext(),"Clicked!",Toast.LENGTH_SHORT).show();

                                Map<String, Object> allowed_users = new HashMap<>();
                                allowed_users.put("User1",current_user.getUid());
                                allowed_users.put("User2",snapshot.getId());

                                db.collection("pms")
                                        .document(chatroomId)
                                        .collection("private")
                                        .document("allowed_users")
                                        .set(allowed_users);

                                Map<String, String> user = new HashMap<>();
                                user.put(USER_NAME, current_user.getDisplayName());

                                db.collection("pms")
                                        .document(chatroomId)
                                        .collection("active_users")
                                        .document(current_user.getUid())
                                        .set(user);

                                Bundle bundle = new Bundle();
                                bundle.putString(CHATROOM_ID, chatroomId);
                                ChatroomReferenceFragment chatroomReferenceFragment = new ChatroomReferenceFragment();
                                chatroomReferenceFragment.setArguments(bundle);
                                FragmentManager manager = getFragmentManager();
                                manager.beginTransaction()
                                        .addToBackStack("Chatrooms")
                                        .replace(R.id.frameLayout, chatroomReferenceFragment, null)
                                        .commit();

                                Log.d("firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("firebase", "Error adding document", e);
                            }
                        });*/
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
