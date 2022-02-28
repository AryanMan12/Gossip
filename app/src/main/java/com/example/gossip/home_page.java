package com.example.gossip;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class home_page extends Fragment {
    Button fragment_request,fragment_friends;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home_page() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static home_page newInstance(String param1, String param2) {
        home_page fragment = new home_page();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragment_request = view.findViewById(R.id.request_btn);
        fragment_friends= view.findViewById(R.id.friends_btn);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Request_Page()).commit();
        fragment_request.setBackgroundColor(Color.parseColor("#B6690D"));
        fragment_request.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                replacefragment(new Request_Page());
                fragment_friends.setBackgroundResource(R.drawable.box_grad);
                fragment_request.setBackgroundColor(Color.parseColor("#B6690D"));
            }
        });

        fragment_friends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                replacefragment(new Friends_Page());
                fragment_friends.setBackgroundColor(Color.parseColor("#B6690D"));
                fragment_request.setBackgroundResource(R.drawable.box_grad);
            }
        });
    }

    private void replacefragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout,fragment);
        transaction.commit();
    }

}