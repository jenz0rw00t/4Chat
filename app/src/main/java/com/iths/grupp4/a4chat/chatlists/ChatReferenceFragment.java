package com.iths.grupp4.a4chat.chatlists;


import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.iths.grupp4.a4chat.MainActivity;
import com.iths.grupp4.a4chat.photos.ChangePhotoDialog;
import com.iths.grupp4.a4chat.photos.PhotoUploader;
import com.iths.grupp4.a4chat.R;
import com.iths.grupp4.a4chat.allusers.AllUsers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ChatReferenceFragment extends Fragment implements
        ChangePhotoDialog.OnPhotoReceivedListener, PhotoUploader.ImageUploadCallback {

    private static final String TAG = "ChatReferenceFragment";

    public ChatReferenceFragment() {
        // Required empty public constructor
    }

    List<MessageUserRef> messagesList = new ArrayList<>();
    private MessageReferenceViewAdapter adapter;
    FirebaseFirestore db;
    EditText messageField;
    AllUsers user;
    DocumentReference userRef;
    String snapshotId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);

        db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = db.collection("users").document(userID);

        messageField = getActivity().findViewById(R.id.messageField);

        messageField.setOnClickListener(view -> {
            ClipData clipData = MainActivity.sClipboardManager.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            messageField.setText(item.getText().toString());
        });

        //Set adapter for recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new MessageReferenceViewAdapter(messagesList);
        recyclerView.setAdapter(adapter);

        //Sets the recyclerview to bottom if keybord is visible
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //If bottom < oldBottom, keyboard is up.
                if (bottom < oldBottom) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (messagesList.size() > 0) {
                                recyclerView.smoothScrollToPosition(
                                        recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });

        //Register for change events for documents stored in collection items on firestore
        db.collection("messagesUserRef")
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

                                MessageUserRef messageUserRef = dc.getDocument().toObject(MessageUserRef.class);
                                messageUserRef.id = id;
                                adapter.addItem(messageUserRef);
                                recyclerView.smoothScrollToPosition(messagesList.size() -1);

                            } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                String id = dc.getDocument().getId();
                                adapter.removeItem(id);
                            }
                        }
                    }
                });

        getActivity().findViewById(R.id.send_button_plane).setOnClickListener(view -> {


            // Create a new message with username and message
           MessageUserRef info = new MessageUserRef(userRef, messageField.getText().toString());
           messageField.setText("");

            // Add a new document with a generated ID
            db.collection("messagesUserRef")
                    .add(info)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
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


        getActivity().findViewById(R.id.attachment).setOnClickListener(view -> {
            openChangePhotoDialog(view);
        });
    }


    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: imagepath is " + imagePath);

        if (!imagePath.toString().equals("")) {
            Context context = getActivity();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            PhotoUploader uploader = new PhotoUploader(userId, context, true, this);
            uploader.uploadFullSizeNewPhoto(imagePath);
        }

        MessageUserRef loadingImage = new MessageUserRef(userRef, "default", true);

        db.collection("messagesUserRef")
                .add(loadingImage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                        snapshotId = documentReference.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase", "Error adding document", e);
                    }
                });
    }


    @Override
    public void updateImageUrl(String downloadUrl) {
        Log.d(TAG, "messageImageUrl: downUrl is: " + downloadUrl);
        Log.d(TAG, "snapshotid Is: " + snapshotId);

        db.collection("messagesUserRef")
                .document(snapshotId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        MessageUserRef uploadedImage = new MessageUserRef(userRef, "" + downloadUrl, true);

                        db.collection("messagesUserRef")
                                .add(uploadedImage)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("firebase", "Error adding document", e);
                                    }
                                });
                    }
                });
    }

    private void openChangePhotoDialog(View view) {
        Log.d(TAG, "onClick: Image button clicked");
        ChangePhotoDialog dialog = new ChangePhotoDialog();
        Bundle bundle = new Bundle();
        bundle.putString("position", "bottom_position");
        dialog.setArguments(bundle);
        dialog.setTargetFragment(ChatReferenceFragment.this, 1);
        dialog.show(getFragmentManager(), "ChangePhotoDialog");

    }

}
