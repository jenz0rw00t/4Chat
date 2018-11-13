package com.example.grupp4.a4chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {

    private ImageView userProfileImage;
    private TextView userProfileName;
    private TextView userProfileEmail;
    private FirebaseAuth mFirebaseAuth;
    private String TAG;

    public UserProfileFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        String profile_user_id = mFirebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore userFireStoreReference = FirebaseFirestore.getInstance();

        userProfileImage = (ImageView) getView().findViewById(R.id.userProfileImage);
        userProfileName = (TextView)getView().findViewById(R.id.userProfileName);
        userProfileEmail = (TextView)getView().findViewById(R.id.userProfileEmail);

        userFireStoreReference.collection("users").document(profile_user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String image = documentSnapshot.getString("avatar");
                    userProfileName.setText(name);
                    userProfileEmail.setText(email);
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(userProfileImage);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
}
