package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.codewithharry.dbdemo.adapter.RecyclerViewAdapter;
import com.codewithharry.dbdemo.data.MyDbHandler;
import com.codewithharry.dbdemo.model.Contact;
import com.example.gossip.adaptor.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class friends extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
//    private  ArrayList<Friend> friendArrayList;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //Recyclerview initialization
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Integer> arr=new ArrayList<Integer>(Arrays.asList(1,2,3));
        //Create a new ArrayList
        ArrayList<Integer> new_arr=new ArrayList<Integer>();
        for(int i=0;i<arr.size();i++){
                new_arr.add(arr.get(i));
        }

//        Use your recyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(friends.this,new_arr);
        recyclerView.setAdapter(recyclerViewAdapter);



    }
}