package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Root extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
    }

    public void signup_btn(View view) {
        Intent intent=new Intent(this,signup_page.class);
        startActivity(intent);
    }

    public void login_btn(View view) {
        Intent intent=new Intent(this,Login.class);
        startActivity(intent);
    }
}