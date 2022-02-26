package com.example.gossip;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Request_Page extends Fragment {
    private RecyclerView recyclerView;
    private RequestPageRecycler recyclerViewAdapter;
    private ArrayList<Map<String, Object>> reqList;
    private Map<String, Object> current_userData;
    SearchView searchView;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    private FirebaseUser fUser;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_request__page, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data.....");
        progressDialog.show();

        //Recyclerview initialization
        recyclerView = (RecyclerView) view.findViewById(R.id.req_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reqList = new ArrayList<Map<String, Object>>();
        current_userData = new HashMap<String, Object>();



        // Use your recyclerView
        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                new databaseHandler().getdata(new databaseHandler.userCallback() {
                    @Override
                    public void onCallback(Map<String, Object> userData) {
                        current_userData = userData;
                        recyclerViewAdapter = new RequestPageRecycler(reqList, current_userData, getContext());
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }
                }, currUser);
            }
        });
        EventChangeListener();

        // SearchView
        searchView = (SearchView) view.findViewById(R.id.req_search_friends);

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
                    new databaseHandler().getdata(new databaseHandler.userCallback() {
                        @Override
                        public void onCallback(Map<String, Object> userData) {
                            current_userData = userData;
                            RequestPageRecycler adaptor = new RequestPageRecycler(reqList, current_userData, getContext());
                            recyclerView.setAdapter(adaptor);
                        }
                    }, currUser);
                }
            });
        }else {
            db.collection("Users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                                @Override
                                public void onCallback(String currUser) {
                                    new databaseHandler().getdata(new databaseHandler.userCallback() {
                                        @Override
                                        public void onCallback(Map<String, Object> userData) {
                                            ArrayList<Map<String, Object>> filterList = new ArrayList<Map<String, Object>>();
                                            current_userData = userData;
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if ((document.getId().toLowerCase()).contains(str) && !(document.getData().get("phone")).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3))) {
                                                        filterList.add(document.getData());
                                                    }
                                                }
                                            } else {
                                                Log.d("Search Requests", "No Data");
                                            }
                                            RequestPageRecycler adaptor = new RequestPageRecycler(filterList, current_userData, getContext());
                                            recyclerView.setAdapter(adaptor);
                                        }
                                    }, currUser);
                                }
                            });
                        }
                    });
        }
    }


    public void EventChangeListener(){
        db.collection("Users").whereEqualTo("phone",fUser.getPhoneNumber().toString().substring(3)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            String current_user = (task.getResult().getDocuments().get(0).get("username")).toString();
                            new databaseHandler().getdata(new databaseHandler.userCallback() {
                                @Override
                                public void onCallback(Map<String, Object> userData) {
                                    current_userData = userData;
                                    db.collection("Users").whereArrayContains("requests", current_user)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    if (error != null) {
                                                        if (progressDialog.isShowing())
                                                            progressDialog.dismiss();
                                                        Log.e("FireStore error", error.getMessage());
                                                        return;
                                                    }
                                                    if (value.isEmpty()){
                                                        if (progressDialog.isShowing())
                                                            progressDialog.dismiss();
                                                    }
                                                    for (DocumentChange dc : value.getDocumentChanges()) {
                                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                                            reqList.add(dc.getDocument().getData());
                                                        }
                                                        if (dc.getType() == DocumentChange.Type.REMOVED){
                                                            reqList.remove(dc.getDocument().getData());
                                                        }
                                                        if (dc.getType() == DocumentChange.Type.MODIFIED){
                                                            reqList.remove(dc.getDocument().getData());
                                                            reqList.add(dc.getDocument().getData());
                                                        }
                                                        recyclerViewAdapter.notifyDataSetChanged();
                                                        if (progressDialog.isShowing())
                                                            progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                }
                            }, current_user);
                        }else {
                            Toast.makeText(getActivity(), "Unable to get data!!", Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }

}