package com.example.gossip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gossip.adaptor.RecyclerViewAdaptor;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Map;

public class friends extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdaptor recyclerViewAdapter;
    private ArrayList<UserFriends> userArrayList;
    SearchView searchView;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    // TODO : Change current_user
    String current_user = "Aryan12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Feching Data.....");
        progressDialog.show();

        //Recyclerview initialization
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<UserFriends>();

        // Use your recyclerView
        recyclerViewAdapter = new RecyclerViewAdaptor(friends.this, userArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
        EventChangeListener();

        // SearchView
        searchView = findViewById(R.id.search_friends);

    }

    @Override
    protected void onStart() {
        super.onStart();
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
        RecyclerViewAdaptor adaptor = new RecyclerViewAdaptor(filterList);
        recyclerView.setAdapter(adaptor);
    }

    private void EventChangeListener() {
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
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                userArrayList.add(dc.getDocument().toObject(UserFriends.class));
                            }
                            recyclerViewAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }


}
