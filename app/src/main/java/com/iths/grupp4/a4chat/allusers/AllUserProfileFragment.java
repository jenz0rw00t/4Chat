package com.iths.grupp4.a4chat.allusers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import com.iths.grupp4.a4chat.photos.FullScreenDialog;
import com.iths.grupp4.a4chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


public class AllUserProfileFragment extends Fragment {

    private static final String TAG = "Error";
    private MenuItem itemAddFriend;
    private MenuItem itemRemoveFriend;
    private Button addFriend;
    private Button removeFriend;

    private FirebaseAuth mAuth;
    private FirebaseFirestore friendRequestReference;
    private FirebaseFirestore acceptedFriendReference;
    private CollectionReference requestReference;
    private CollectionReference friendsReference;
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
        acceptedFriendReference = FirebaseFirestore.getInstance();
        friendsReference = acceptedFriendReference.collection("users");
        requestReference = friendRequestReference.collection("friend_request");
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_state = "not_friends";

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        receiver_user_id = bundle.getString("visit_user_id");
        removeFriend.setVisibility(View.INVISIBLE);


        allUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFullsizeAvatar(receiver_user_id);
            }
        });

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
                    Picasso.get().load(image)
                            .placeholder(R.drawable.default_avatar)
                            .transform(new CropCircleTransformation())
                            .into(allUserImage);

                    /* OM NI ÄNDRAR NÅGOT PÅ DENNA FRAGMENTEN SÅ SÄG TILL MIG INNAN NI GÖR DET // Kivanc
                    TODO Fixa problemet när friend_request på firestore inte finns så crashar appen.

                    This function checks if you have sent a request to other users and it updates
                    their button if you have sent one.

                     */
                    requestReference.document(current_user.getUid()).collection(receiver_user_id).document("request").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                            if (documentSnapshot.exists()){
                                String request_type = documentSnapshot.getString("request_type");
                                if(request_type.equals("received")){

                                    current_state = "request_received";
                                    addFriend.setText("Accept Friend Request");

                                    removeFriend.setText("Decline friend Request");
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
                    friendsReference.whereArrayContains("friends", current_user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                List<AllUsers> list = queryDocumentSnapshots.toObjects(AllUsers.class);
                                for (AllUsers user:list) {
                                    if (user.userId.equals(receiver_user_id)){
                                        current_state = "friends";
                                        addFriend.setVisibility(View.INVISIBLE);
                                        removeFriend.setVisibility(View.VISIBLE);
                                        removeFriend.setEnabled(true);
                                        removeFriend.setText("Unfriend this person");
                                   }
                                }
                            }
                        }
                    });
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

                NOT FRIENDS !
                */
                if (current_state.equals("not_friends")){
                    Map<String, Object> sent = new HashMap<>();
                    sent.put("request_type", "sent");
                    removeFriend.setVisibility(View.INVISIBLE);

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

                REQUEST SENT !
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
                                    current_state = "not_friends";
                                    addFriend.setText("Send friend request");
                                }
                            });
                        }
                    });

                    addFriend.setEnabled(true);
                }


                // REQUEST RECEIVED

                if(current_state.equals("request_received")){
                    userFireStoreReference.collection("users").document(receiver_user_id)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            acceptedFriendReference.collection("users").document(current_user.getUid())
                                    .update("friends", FieldValue.arrayUnion(receiver_user_id))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            userFireStoreReference.collection("users").document(current_user.getUid())
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    friendRequestReference.collection("users").document(receiver_user_id)
                                                            .update("friends", FieldValue.arrayUnion(current_user.getUid()))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
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
                                                                                            current_state = "friends";
                                                                                            addFriend.setText("Unfriend this person");
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    });

                }
            }
        });

        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_state.equals("friends")){
                    friendsReference.document(current_user.getUid()).collection("friends")
                            .document(receiver_user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsReference.document(receiver_user_id).collection("friends").document(current_user.getUid())
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    current_state = "not_friends";
                                    addFriend.setVisibility(View.VISIBLE);
                                    addFriend.setText("Send friend request");

                                    removeFriend.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                }else if (current_state.equals("request_received")){
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
                                            current_state = "not_friends";
                                            addFriend.setText("Send friend request");
                                            removeFriend.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                }
            }
        });


        return view;
    }

    private void displayFullsizeAvatar(String receiverUserid) {
        FragmentManager fragmentManager = getFragmentManager();
        FullScreenDialog dialog = new FullScreenDialog();
        Bundle bundle = new Bundle();
        bundle.putString("receiver_user_id", receiverUserid);
        dialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        dialog.show(fragmentTransaction, FullScreenDialog.TAG);
    }

}
