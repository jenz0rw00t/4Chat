package com.example.grupp4.a4chat.allusers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grupp4.a4chat.R;


public class AllUserProfileFragment extends Fragment {


    public AllUserProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_user_profile, container, false);
    }

}
