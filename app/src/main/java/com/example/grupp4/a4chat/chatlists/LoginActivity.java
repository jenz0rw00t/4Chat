package com.example.grupp4.a4chat.chatlists;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.grupp4.a4chat.MainActivity;
import com.example.grupp4.a4chat.R;
import com.example.grupp4.a4chat.allusers.AllUsers;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final int RC_SIGN_IN = 9001; //Code for Google Sign in intent
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar mProgressBar;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /*
       // Background video function.

       mVideoView = (VideoView)findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_background_video);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0,0);

            }
        });
        */

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        mProgressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.sign_in_btn).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.sign_in_btn:
                    signIn();
                    break;
            }
        }
    };

    // START signIn which opens up Google signIn intent
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    //now start authenticate with firebase with googleaccount
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        showLoader();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            setUserToDatabase();
                            finish(); //finish so the user cant go back to login screen after they´ve logged in
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            makeToast("Failed to log in");

                        }
                        hideLoader();
                    }
                });
    }
    // END auth_with_google

    private void signOut() {
        // Firebase sign out
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mAuth.signOut();
        }
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    //Shows a progressbar when loading
    private void showLoader() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    //Hides the progressbar when loading finished
    private void hideLoader() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    //makes toasts
    private void makeToast(String toastText) {
        Toast.makeText(LoginActivity.this, toastText, Toast.LENGTH_LONG).show();
    }

    //Ensures user will log out from Google as long as user has logged out from Firebase. Good or not?
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Logga ut usern");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            signOut();
        }
    }

    //Method should be used if user wants to disconnect their Google account from our App
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        // Google revoke access
        //https://developers.google.com/identity/sign-in/android/disconnect  (info on revoking,
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    //duplicates the the authenticate user to a userObject and sets it to the database
    //if current usedID already exists, there will be NO new userFireStore object created
    private void setUserToDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            if (db.collection("users").document(currentUser.getUid()) == null) {

                AllUsers allUsers = new AllUsers(
                        currentUser.getDisplayName(),
                        currentUser.getEmail(),
                        currentUser.getPhotoUrl().toString(),
                        currentUser.getUid()
                );

                db.collection("users").document(currentUser.getUid())
                        .set(allUsers)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("firestore", "Error adding document", e);
                            }
                        });
            }
        }
    }
}


