package com.iths.grupp4.a4chat;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.firestore.CollectionReference;
import com.iths.grupp4.a4chat.allusers.AllUserListFragment;
import com.iths.grupp4.a4chat.chatlists.ChatReferenceFragment;
import com.iths.grupp4.a4chat.chatlists.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iths.grupp4.a4chat.chatroomlists.ChatroomFragment;
import com.iths.grupp4.a4chat.chatroomlists.PmFragment;
import com.iths.grupp4.a4chat.friend.FriendRequestListFragment;
import com.iths.grupp4.a4chat.friend.FriendsListFragment;
import com.iths.grupp4.a4chat.photos.FullScreenDialog;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivityTag";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private TextView navUserName;
    private ImageView navUserImage;
    private FirebaseFirestore reference;
    private FirebaseAuth mAuth;
    private BottomNavigationView navigation;
    private GoogleSignInClient mGoogleSignInClient;
    public static ClipboardManager sClipboardManager;
    public static FragmentManager sFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        sFragmentManager = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatroomFragment())
                .commit();

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();

        navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().setGroupCheckable(0,false,true);

        databaseReference.collection("users").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String name = documentSnapshot.getString("name");
                    String image = documentSnapshot.getString("avatar");
                    navUserName.setText(name);
                    Picasso.get().load(image)
                            .placeholder(R.drawable.default_avatar)
                            .transform(new CropCircleTransformation())
                            .into(navUserImage);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        navUserName = (TextView) headerView.findViewById(R.id.nav_username);
        navUserImage = (ImageView) headerView.findViewById(R.id.nav_user_image);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFullsizeAvatar(user_id);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getUid();

        if (user == null) {
            signInActivity();
        }else {
            reference = FirebaseFirestore.getInstance();
            reference.collection("users").document(user).update("online", true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser()!=null) {
        mAuth = FirebaseAuth.getInstance();

            String user = mAuth.getCurrentUser().getUid();

            reference = FirebaseFirestore.getInstance();
            reference.collection("users").document(user).update("online", false);
        }
    }

    private void signInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        }

        switch (item.getItemId()){
            case R.id.menu_languages:
                showLanguageDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            navigation.getMenu().setGroupCheckable(0,false,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UserProfileFragment())
                    .addToBackStack("LateTransaction").commit();
        } else if (id == R.id.nav_chatrooms) {
            navigation.getMenu().setGroupCheckable(0,false,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatroomFragment())
                    .commit();
        } else if (id == R.id.nav_logout) {
            mAuth = FirebaseAuth.getInstance();
            String user = mAuth.getCurrentUser().getUid();
            reference.collection("users").document(user).update("online", false);
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.nav_all_users) {
            navigation.getMenu().setGroupCheckable(0,false,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AllUserListFragment())
                    .addToBackStack("LateTransaction").commit();
        } else if (id == R.id.nav_chat_test) {
            navigation.getMenu().setGroupCheckable(0,true,true);
            navigation.setSelectedItemId(R.id.bottomNavigation_recents);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PmFragment())
                    .addToBackStack("LateTransaction").commit();
        } else if (id == R.id.nav_friends) {
            navigation.getMenu().setGroupCheckable(0,true,true);
            navigation.setSelectedItemId(R.id.bottomNavigation_friends);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FriendsListFragment())
                    .addToBackStack("LateTransaction").commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //When backpressed is pressed, checking if backstackcount is over 0, if it is then trying to pop allUsers backstack.
    //If no allUsers in backstack then going back to ChatRoomFragment.
    //If 0, exit application
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (!fragmentManager.popBackStackImmediate("allUsers", FragmentManager.POP_BACK_STACK_INCLUSIVE)) {
                fragmentManager.popBackStack("Chatrooms", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.popBackStack("LateTransaction", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } else {
            supportFinishAfterTransition();
        }
    }

    //Bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottomNavigation_recents:
                    navigation.getMenu().setGroupCheckable(0,true,true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PmFragment())
                            .addToBackStack("LateTransaction")
                            .commit();
                    return true;

                case R.id.bottomNavigation_friends:
                    navigation.getMenu().setGroupCheckable(0,true,true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FriendsListFragment())
                            .addToBackStack("LateTransaction")
                            .commit();
                    return true;

                case R.id.bottomNavigation_request:
                    navigation.getMenu().setGroupCheckable(0,true,true);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("from_request", true);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AllUserListFragment fragment = new AllUserListFragment();
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frameLayout, fragment)
                            .addToBackStack("LateTransaction")
                            .commit();
                    return true;
            }
            return false;
        }
    };




    public void updateNavProfileImage(Uri imagePath) {
        Log.d(TAG, "setNewImagePath: path is recieved" + imagePath);
        Picasso.get().load(imagePath.toString()).transform(new CropCircleTransformation()).placeholder(R.drawable.default_avatar).into(navUserImage);
    }

    public void updateNavProfileName(String name) {
        navUserName.setText(name);
    }

    private void displayFullsizeAvatar(String receiverUserid) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FullScreenDialog dialog = new FullScreenDialog();
        Bundle bundle = new Bundle();
        bundle.putString("receiver_user_id", receiverUserid);
        dialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        dialog.show(fragmentTransaction, FullScreenDialog.TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            signOut();
        }

    }

    private void signOut() {
        // Firebase sign out
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UserProfileFragment())
                    .addToBackStack("LateTransaction").commit();
        }
    }

    public void showBottomBar(Boolean visible){
        if (visible){
            navigation.setVisibility(View.VISIBLE);
        } else {
            navigation.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_language, menu);
        return true;
    }

    private void showLanguageDialog(){
        final String[] listItems = {"English", "Svenska"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.chooce_language);
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    setLocale("en");
                    recreate();
                }else if (which == 1){
                    setLocale("sv");
                    recreate();
                }

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext()
                .getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

    }

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }


}




