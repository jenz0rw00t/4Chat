package com.example.grupp4.a4chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

            UserFireStore userFireStore = new UserFireStore(
                    currentUser.getDisplayName(),
                    currentUser.getEmail(),
                    currentUser.getPhotoUrl().toString(),
                    currentUser.getUid()
            );

            db.collection("users").document(currentUser.getUid())
                    .set(userFireStore)
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


/*
2018-11-12 16:25:42.334 10680-10680/com.example.grupp4.a4chat W/LoginActivity: signInWithCredential:failure
    com.google.firebase.FirebaseException: An internal error has occurred. [ Identity Toolkit API has not been used in project 365291550515 before or it is disabled. Enable it by visiting https://console.developers.google.com/apis/api/identitytoolkit.googleapis.com/overview?project=365291550515 then retry. If you enabled this API recently, wait a few minutes for the action to propagate to our systems and retry. ]
        at com.google.firebase.auth.api.internal.zzds.zzb(Unknown Source:18)
        at com.google.firebase.auth.api.internal.zzew.zza(Unknown Source:11)
        at com.google.firebase.auth.api.internal.zzeo.zzc(Unknown Source:33)
        at com.google.firebase.auth.api.internal.zzep.onFailure(Unknown Source:49)
        at com.google.firebase.auth.api.internal.zzdy.dispatchTransaction(Unknown Source:18)
        at com.google.android.gms.internal.firebase_auth.zzb.onTransact(Unknown Source:12)
        at android.os.Binder.execTransact(Binder.java:697)

SOVLED: Disable the Identity Toolkit API in Google API Console API Library and then enable it
2018-11-12 16:34:27.878 12154-12154/com.example.grupp4.a4chat D/LoginActivity: signInWithCredential:success
*/
