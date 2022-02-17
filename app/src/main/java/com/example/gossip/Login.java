package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {


        EditText user,pass;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            user=findViewById(R.id.login_name);
            pass=findViewById(R.id.login_pass);
        }
        public void onlogin(View view) {
            String Username=user.getText().toString();
            String Password=pass.getText().toString();
//            Intent intent=new
//                    Intent(this,Secondactivity.class);
//            intent.putExtra("User",Username);
//            intent.putExtra("Pass",Password);
//            startActivity(intent);
        }

    }
