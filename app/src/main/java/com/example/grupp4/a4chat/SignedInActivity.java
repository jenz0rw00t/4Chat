package com.example.grupp4.a4chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

public class SignedInActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "SignedInActivity";
    private TextView userId;
    private TextView userEmail;
    private TextView userName;
    private ImageView userPicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

         userId = findViewById(R.id.user_display_id);
         userEmail = findViewById(R.id.user_display_email);
         userName = findViewById(R.id.user_name);
         userPicture = findViewById(R.id.user_profile_picture);

        findViewById(R.id.sign_out_button).setOnClickListener(buttonClickListener);
        findViewById(R.id.profile_picture_btn).setOnClickListener(buttonClickListener);

        getUserDetails();
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_picture_btn:
                    openGalleryIntent();
                    break;

                case R.id.sign_out_button:
                    signOut(view);
                    break;
            }
        }
    };

    //opens up users phone gallery
    private void openGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    //when picture is picked, this displays through Picasso and is set to User object in Firestore database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            Picasso.get()
                    .load(selectedImage)
                    .resize(400, 400)
                    .centerCrop()
                    .into(userPicture);
            Log.d(TAG, "onActivityResult: Picturename is" + selectedImage + "");
            //behöver man minska filstorleken innan man skickar bilden till firebase tro?

            setPictureFireBaseUser(selectedImage);
          }
    }

    //Here is method for setting selected image to User object in Firestore databas
    private void setPictureFireBaseUser(Uri image) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(image)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: UserObject profile updated");
                            //make sure to have user details change before update to view
                            //getUserDetails();
                        }
                    });
        }
    }

    //displays user information to view
    private void getUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String uid = user.getUid();
            String email = user.getEmail();
            String name = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();

            Log.d(TAG, "getUserDetails: picture is " + photoUrl + "");

            userId.setText("UserId: " + uid);
            userEmail.setText("Email: " + email);
            userName.setText("Name: " + name);

            Picasso.get()
                    .load(photoUrl)
                    .resize(400, 400)
                    //.placeholder(getApplicationContext().getResources().getDrawable(R.drawable.unknown))
                    .centerCrop()
                    .into(userPicture);
        }
    }

    /*
    currently method is only logging out user from Firebase, not from google,
    which means user is remembered next time the user clicks the Google Button Sign in.
    Is this the better alternative?? Or should user be revoked?
    */
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        redirectLoginActivity();
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //if user == null, user is not authenticated and is redirected to loginscreen
    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen");

            Intent intent = new Intent(SignedInActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //clear entire activitystack
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "checkAuthenticationState: user is authenticated");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();  //checks authenticationState for extra security
    }


}

