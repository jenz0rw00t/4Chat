package com.example.grupp4.a4chat.allusers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupp4.a4chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class AllUserProfileFragment extends Fragment {

    private static final String TAG = "Error";
    private MenuItem itemAddFriend;
    private MenuItem itemRemoveFriend;
    private Button addFriend;
    private Button removeFriend;

    private FirebaseAuth mAuth;
    private FirebaseFirestore friendRequestReference;
    private FirebaseUser current_user;

    private String current_state;
    String sender_user_id;
    String receiver_user_id;


    public AllUserProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_user_profile, container, false);

        FirebaseFirestore userFireStoreReference = FirebaseFirestore.getInstance();
        TextView allUserName = (TextView) view.findViewById(R.id.allUserProfileName);
        TextView allUserEmail = (TextView) view.findViewById(R.id.allUserProfileEmail);
        ImageView allUserImage = (ImageView) view.findViewById(R.id.allUserProfileImage);
        addFriend = (Button) view.findViewById(R.id.addFriend);
        removeFriend = (Button) view.findViewById(R.id.removeFriend);

        friendRequestReference = FirebaseFirestore.getInstance();
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_state = "not_friends";

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        receiver_user_id = bundle.getString("visit_user_id");


        userFireStoreReference.collection("users").document(receiver_user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String image = documentSnapshot.getString("avatar");
                    allUserName.setText(name);
                    allUserEmail.setText(email);
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(allUserImage);

                    friendRequestReference.collection("friend_request").document(current_user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.contains(current_user.getUid())){
                                String request_type = documentSnapshot.getString("reques_type");


                                //dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                if(request_type.equals("received")){

                                    current_state = "request_received";
                                    addFriend.setText("Accept Friend Request");

                                    removeFriend.setVisibility(View.VISIBLE);
                                    removeFriend.setEnabled(true);


                                } else if(request_type.equals("sent")) {

                                    current_state = "request_sent";
                                    addFriend.setText("Cancel Friend Request");

                                    removeFriend.setVisibility(View.INVISIBLE);
                                    removeFriend.setEnabled(false);

                                }
                            }
                        }
                    });
                    /*
                    friendRequestReference.collection("friend_request")
                            .document(current_user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                            @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot.contains(current_user.getUid())){
                                String request_type = documentSnapshot.getString("request_type");


                                //dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                if(request_type.equals("received")){

                                    current_state = "request_received";
                                    addFriend.setText("Accept Friend Request");

                                    removeFriend.setVisibility(View.VISIBLE);
                                    removeFriend.setEnabled(true);


                                } else if(request_type.equals("sent")) {

                                    current_state = "request_sent";
                                    addFriend.setText("Cancel Friend Request");

                                    removeFriend.setVisibility(View.INVISIBLE);
                                    removeFriend.setEnabled(false);

                                }
                            }
                        }
                    });
                    */

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend.setEnabled(false);

                /*
                When you press on add friend button it will create new collection and document
                in firestore, that you have sent a friend request.
                */
                if (current_state.equals("not_friends")){
                    Map<String, Object> sent = new HashMap<>();
                    sent.put("request_type", "sent");

                    friendRequestReference.collection("friend_request").document(current_user.getUid())
                            .collection(receiver_user_id).document("request")
                            .set(sent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Map<String, Object> received = new HashMap<>();
                                received.put("request_type", "received");
                                friendRequestReference.collection("friend_request").document(receiver_user_id)
                                        .collection(current_user.getUid()).document("request")
                                        .set(received).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        addFriend.setEnabled(true);
                                        current_state = "request_sent";
                                        addFriend.setText("Cancel Friend Request");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
                /*
                When you have sent friend request to the other user the state will be changed to request sent
                and you can cancel the request where the collection and document will be removed.
                 */
                if (current_state.equals("request_sent")){
                    friendRequestReference.collection("friend_request").document(current_user.getUid())
                            .collection(receiver_user_id).document("request").delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestReference.collection("friend_request").document(receiver_user_id).
                                    collection(current_user.getUid()).document("request")
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    addFriend.setEnabled(true);
                                    current_state = "not_friends";
                                    addFriend.setText("Send friend request");
                                }
                            });
                        }
                    });

                }

                Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();

            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friend, menu);

        itemAddFriend = menu.findItem(R.id.menu_addFriend);
        itemRemoveFriend = menu.findItem(R.id.menu_removeFriend);

        itemRemoveFriend.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_addFriend:
                itemAddFriend.setVisible(false);
                itemRemoveFriend.setVisible(true);
                return true;

            case R.id.menu_removeFriend:
                itemRemoveFriend.setVisible(false);
                itemAddFriend.setVisible(true);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


}
