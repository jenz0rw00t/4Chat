package com.iths.grupp4.a4chat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iths.grupp4.a4chat.chatlists.ChatReferenceFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullScreenDialog extends DialogFragment {

    public static String TAG = "FullScreenDialog";
    private String userId;
    private ImageView mImageView;
    private Toolbar toolbar;
    private ProgressBar mProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fullscreen, container, false);
        mProgressBar = view.findViewById(R.id.dialog_fullscreen_progressbar);
        showLoader();

        Bundle bundle = getArguments();
        userId = bundle.getString("receiver_user_id");

        toolbar = view.findViewById(R.id.dialog_fullscreen_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        if (getDialog()!= null) {
            toolbar.setNavigationOnClickListener(view1 -> getDialog().dismiss());
        }
        mImageView = view.findViewById(R.id.dialog_fullscreen_imageview);

        setFullsizeAvatar();

        return view;
    }

    private void setFullsizeAvatar() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore userFireStoreReference = FirebaseFirestore.getInstance();

        userFireStoreReference.collection("users").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String fullsizeAvatar = documentSnapshot.getString("fullSizeAvatar");

                    setImage(fullsizeAvatar, mImageView);

                    Log.d(TAG, "onComplete: fullsizeavatar = : " + fullsizeAvatar);
                } else {

                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void setImage(String imageUrl, ImageView imageView) {
        showLoader();
        Picasso.get().load(imageUrl).
                into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        hideLoader();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }


    private void showLoader() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    //Hides the progressbar when loading finished
    private void hideLoader() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


}

