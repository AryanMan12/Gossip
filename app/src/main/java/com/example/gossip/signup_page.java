package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class signup_page extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    EditText name, uname, mno, pwd, cpwd,code;
    String Name,Uname,Mno,Pwd,Cpwd,Code,verificationId;
    Button signup, otp;
    FirebaseFirestore Db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        mAuth = FirebaseAuth.getInstance();

        Db=FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        uname = findViewById(R.id.uname);
        mno = findViewById(R.id.pno);
        pwd = findViewById(R.id.pwd);
        cpwd = findViewById(R.id.confirmpwd);
        code=findViewById(R.id.code);
        signup = findViewById(R.id.button);
        otp = findViewById(R.id.button2);


    }

    public void sendotp(View view) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(Mno)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyCode(String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        FirebaseAuth.getInstance().getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        signInWithCredential(credential);


    }
    private void signInWithCredential(PhoneAuthCredential credential){

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }
    };


    public void signup(View view) {
        Name=name.getText().toString().trim();
        Uname=uname.getText().toString().trim();
        Mno=mno.getText().toString().trim();
        Pwd=pwd.getText().toString().trim();
        Cpwd=cpwd.getText().toString().trim();
        Code=code.getText().toString().trim();
        if(Name.equals("") || Uname.equals("") || Mno.equals("") || Pwd.equals("") || Cpwd.equals("") || Code.equals("")){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        }
        else if (Db.collection("Users").whereEqualTo("username",Uname).get().isSuccessful()){
            Toast.makeText(this, "Username already existing", Toast.LENGTH_LONG).show();
        }
        else if (Db.collection("Users").whereEqualTo("phone",Mno).get().isSuccessful()){
            Toast.makeText(this, "Already have an account", Toast.LENGTH_LONG).show();
        }
        else{
            verifyCode(Code);
        }

    }


}