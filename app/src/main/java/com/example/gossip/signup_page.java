package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class signup_page extends AppCompatActivity {
    EditText name, uname, mno, pwd, cpwd;
    Button signup, otp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        name = findViewById(R.id.name);
        uname = findViewById(R.id.uname);
        mno = findViewById(R.id.pno);
        pwd = findViewById(R.id.pwd);
        cpwd = findViewById(R.id.confirmpwd);
        signup = findViewById(R.id.button);
        otp = findViewById(R.id.button2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void verifyotp(View view) {
    }
}