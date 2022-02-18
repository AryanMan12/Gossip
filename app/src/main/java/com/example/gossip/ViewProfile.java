package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ViewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
    }

    public void onSelect(View view) {
        Toast.makeText(this,"Added as a friend" ,Toast.LENGTH_LONG).show();
    }

    public void onBack(View view) {

    }
}