package com.example.gossip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gossip.adaptor.RecyclerViewAdaptor;
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

public class Friends_Page extends Fragment {
    private RecyclerView recyclerView;

    private RecyclerViewAdaptor recyclerViewAdapter;
    private ArrayList<UserFriends> userArrayList;
    SearchView searchView;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    private FirebaseUser fUser;
    View view;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends__page, container, false);
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
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userArrayList = new ArrayList<UserFriends>();

        // SearchView
        searchView = (SearchView) view.findViewById(R.id.search_chat_page);
        EventChangeListener();
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                recyclerViewAdapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Use your recyclerView
        recyclerViewAdapter = new RecyclerViewAdaptor(userArrayList,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);


        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return true;
                }
            });
        }
    }

    private void search(String str) {
        ArrayList<UserFriends> filterList = new ArrayList<>();
        for (UserFriends username: userArrayList){
            if (username.getUsername().toLowerCase().contains(str.toLowerCase())){
                filterList.add(username);
            }
        }

        RecyclerViewAdaptor adaptor = new RecyclerViewAdaptor(filterList ,getContext());
        recyclerView.setAdapter(adaptor);
    }

    private void EventChangeListener() {
        db.collection("Users").whereEqualTo("phone",fUser.getPhoneNumber().toString().substring(3)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                          String current_user = (task.getResult().getDocuments().get(0).get("username")).toString();
                            db.collection("Users").whereArrayContains("friends",current_user)
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
                                                    userArrayList.add(dc.getDocument().toObject(UserFriends.class));
                                                }
                                                if (dc.getType() == DocumentChange.Type.REMOVED) {
                                                    userArrayList.remove(dc.getDocument().toObject(UserFriends.class));
                                                }
                                                if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                                    userArrayList.remove(dc.getDocument().toObject(UserFriends.class));
                                                    userArrayList.add(dc.getDocument().toObject(UserFriends.class));
                                                }
                                                recyclerViewAdapter.notifyDataSetChanged();
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(getActivity(), "Unable to get data!!", Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });

    }

}