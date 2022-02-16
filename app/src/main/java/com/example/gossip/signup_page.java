package com.example.gossip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class signup_page extends AppCompatActivity {
EditText name, uname,mno, pwd, cpwd;
Button uploadimg;
String msg="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        name=findViewById(R.id.name);
        uname=findViewById(R.id.uname);
        mno=findViewById(R.id.pno);
        pwd=findViewById(R.id.pwd);
        cpwd=findViewById(R.id.confirmpwd);

    }

    public void verifyotp(View view) {
    }

    public void signup(View view) {
    }
}