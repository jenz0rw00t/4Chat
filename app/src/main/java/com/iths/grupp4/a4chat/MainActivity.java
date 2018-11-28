package com.iths.grupp4.a4chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iths.grupp4.a4chat.allusers.AllUserListFragment;
import com.iths.grupp4.a4chat.chatlists.ChatFragment;
import com.iths.grupp4.a4chat.chatlists.ChatReferenceFragment;
import com.iths.grupp4.a4chat.chatlists.LoginActivity;
import com.iths.grupp4.a4chat.friend.FriendsListFragment;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
      {


    private static final String TAG = "MainActivityTag";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private TextView navUserName;
    private ImageView navUserImage;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();

        databaseReference.collection("users").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String name = documentSnapshot.getString("name");
                    String image = documentSnapshot.getString("avatar");
                    navUserName.setText(name);
                    Picasso.get().load(image).transform(new CropCircleTransformation()).placeholder(R.drawable.default_avatar).into(navUserImage);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        navUserName = (TextView)headerView.findViewById(R.id.nav_username);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UserProfileFragment()).commit();
        }else if (id == R.id.nav_chatAndFriends){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatFragment()).commit();
        }else if (id == R.id.nav_logout){
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else if (id == R.id.nav_all_users){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AllUserListFragment()).commit();
        }else if (id == R.id.nav_chat_test){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatReferenceFragment()).commit();
        }else if(id == R.id.nav_friends){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FriendsListFragment()).commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

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
      }


