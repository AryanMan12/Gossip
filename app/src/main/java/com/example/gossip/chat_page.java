package com.example.gossip;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gossip.adaptor.ChatPageRecycler;
import com.example.gossip.adaptor.RecyclerViewAdaptor;
import com.example.gossip.adaptor.RequestPageRecycler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class chat_page extends Fragment {
    private RecyclerView recyclerView;
    private ChatPageRecycler recyclerViewAdapter;
    FirebaseFirestore db;
    View view;
    SearchView searchView;
    ArrayList<String> username;
    ArrayList<String> status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_page, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        searchView = (SearchView) view.findViewById(R.id.search_chat_page);
        username = new ArrayList<>();
        status = new ArrayList<>();
        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                db.collection("Chats").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    username = new ArrayList<>();
                                    status = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc: task.getResult()){
                                        if (doc.getId().contains(currUser)){
                                            String fr_user = doc.getId().replace(currUser, "");
                                            ArrayList<String> temp = (ArrayList<String>) doc.getData().get("chats");
                                            String last_chat = temp.get(temp.size()-1);
                                            new databaseHandler().getdata(new databaseHandler.userCallback() {
                                                @Override
                                                public void onCallback(Map<String, Object> userData) {
                                                    username.add((userData.get("username")).toString());
                                                    status.add(last_chat);
                                                    recyclerViewAdapter.notifyDataSetChanged();
                                                }
                                            }, fr_user);
                                        }
                                    }
                                    recyclerViewAdapter = new ChatPageRecycler(username,status,currUser,getContext());
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                }else{
                                    Log.d("Chat page","No Chats found!");
                                }
                            }
                        });
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s.toLowerCase());
                    return true;
                }
            });
        }
    }

    private void search(String str) {
        if(str.trim().equals("")){
            new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                @Override
                public void onCallback(String currUser) {
                    db.collection("Chats").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        username = new ArrayList<>();
                                        status = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc: task.getResult()){
                                            if (doc.getId().contains(currUser)){
                                                String fr_user = doc.getId().replace(currUser, "");
                                                ArrayList<String> temp = (ArrayList<String>) doc.getData().get("chats");
                                                String last_chat = temp.get(temp.size()-1);
                                                new databaseHandler().getdata(new databaseHandler.userCallback() {
                                                    @Override
                                                    public void onCallback(Map<String, Object> userData) {
                                                        username.add((userData.get("username")).toString());
                                                        status.add(last_chat);
                                                        recyclerViewAdapter.notifyDataSetChanged();
                                                    }
                                                }, fr_user);
                                            }
                                        }
                                        recyclerViewAdapter = new ChatPageRecycler(username,status,currUser,getContext());
                                        recyclerView.setAdapter(recyclerViewAdapter);
                                    }else{
                                        Log.d("Chat page","No Chats found!");
                                    }
                                }
                            });
                }
            });
        }else{
            new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                @Override
                public void onCallback(String currUser) {
                    username = new ArrayList<>();
                    status = new ArrayList<>();
                    db.collection("Users").whereArrayContains("friends", currUser)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document: task.getResult()){
                                    if ((document.getId().toLowerCase()).contains(str) && !(document.getData().get("phone")).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3))) {
                                        username.add((document.getData().get("username")).toString());
                                        status.add((document.getData().get("status")).toString());

                                    }
                                }
                                recyclerViewAdapter = new ChatPageRecycler(username,status,currUser,getContext());
                                recyclerView.setAdapter(recyclerViewAdapter);
                            }
                        }
                    });
                }
            });
        }
    }


}