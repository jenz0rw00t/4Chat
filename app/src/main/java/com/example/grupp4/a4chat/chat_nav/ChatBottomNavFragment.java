package com.example.grupp4.a4chat.chat_nav;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.grupp4.a4chat.R;
import com.example.grupp4.a4chat.chatlists.ChatFragment;
import com.example.grupp4.a4chat.chatlists.ChatReferenceFragment;


public class ChatBottomNavFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Button navButton;

    public ChatBottomNavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_bottom_nav, container, false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)view.findViewById(R.id.bottomNavigationId);
        navButton = (Button)view.findViewById(R.id.buttonNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        return view;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id){
            case R.id.globalChat:
                navButton.setText("Enter Global chat");
                navButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatReferenceFragment()).commit();

                    }
                });
                return true;

            case R.id.bottomNavPrivateChat:
                navButton.setText("Private chat");
                navButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatFragment()).commit();
                    }
                });
                return true;
        }
        return false;
    }
}
