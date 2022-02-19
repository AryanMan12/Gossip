package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class signup_page extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    EditText name, uname, mno,code;
    String Name,Uname,Mno,Code,verificationId, img;
    Button signup, otp;
    FirebaseFirestore Db;
    FirebaseUser user;
    Map<String, Object> userData;
    ProgressDialog progressDialog;
    String[] empArray;
    boolean getotpclicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        userData = new HashMap<String, Object>();

        mAuth = FirebaseAuth.getInstance();

        Db=FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        uname = findViewById(R.id.uname);
        mno = findViewById(R.id.pno);
        code=findViewById(R.id.code);
        signup = findViewById(R.id.button);
        otp = findViewById(R.id.button2);
    }

    public void sendotp(View view) {

        Mno = mno.getText().toString();
        Uname = uname.getText().toString();

        Db.collection("Users").whereEqualTo("username", Uname).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Db.collection("Users").whereEqualTo("phone", Mno).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty()){
                                                if (!getotpclicked){
                                                    if (Mno.length() != 10){
                                                        mno.setError("Enter a Valid Phone Number");
                                                        mno.requestFocus();
                                                    }else{
                                                        String phone_no = "+91"+Mno;
                                                        PhoneAuthOptions options =
                                                                PhoneAuthOptions.newBuilder(mAuth)
                                                                        .setPhoneNumber(phone_no)
                                                                        .setTimeout(60L, TimeUnit.SECONDS)
                                                                        .setActivity(signup_page.this)
                                                                        .setCallbacks(mCallBack)
                                                                        .build();
                                                        PhoneAuthProvider.verifyPhoneNumber(options);
                                                        progressDialog = new ProgressDialog(signup_page.this);
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.setMessage("Sending Otp...");
                                                        progressDialog.show();
                                                    }
                                                }else{
                                                    Toast.makeText(signup_page.this, "Enter Valid details", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(signup_page.this, "Phone Number Already Exists", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(signup_page.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        FirebaseAuth.getInstance().getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        signInWithCredential(credential);

    }

    private void signInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user !=null){
                                Toast.makeText(signup_page.this, "Account Created", Toast.LENGTH_SHORT).show();
                                Db.collection("Users").document(Uname).set(userData);
                                Intent intent = new Intent(signup_page.this, MainActivity.class);
                                startActivity(intent);
                                signup_page.this.finish();
                            }else {
                                Toast.makeText(signup_page.this, "Error Logging in..", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            progressDialog.dismiss();
            Toast.makeText(signup_page.this, "Otp Sent", Toast.LENGTH_SHORT).show();
            verificationId = s;
            otp.setBackgroundColor(Color.parseColor("#1DCDCDCD"));

            new CountDownTimer(60000, 1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    otp.setText(""+millisUntilFinished/1000);
                }

                @Override
                public void onFinish() {
                    getotpclicked = false;
                    otp.setBackgroundColor(Color.parseColor("#EC8D1D"));
                    otp.setClickable(true);
                    otp.setText("Send Otp");
                }
            }.start();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String rec_code = phoneAuthCredential.getSmsCode();
            if (rec_code != null){
                code.setText(rec_code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            getotpclicked=false;
            progressDialog.dismiss();
            Toast.makeText(signup_page.this, "Failed to get Otp", Toast.LENGTH_SHORT).show();
        }
    };

    public void signup(View view) {
        Name=name.getText().toString().trim();
        Uname=uname.getText().toString().trim();
        Mno=mno.getText().toString().trim();
        Code=code.getText().toString().trim();
        userData.put("name", Name);
        userData.put("username", Uname);
        userData.put("phone", Mno);
        userData.put("profile_photo", "img");
        userData.put("status", "");
        userData.put("friends", new ArrayList<String>());
        userData.put("requests", new ArrayList<String>());

        if(Name.equals("") || Uname.equals("") || Mno.equals("")){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        }else{
            Db.collection("Users").whereEqualTo("username", Uname).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()){
                                Db.collection("Users").whereEqualTo("phone", Mno).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.getResult().isEmpty()){
                                                    if (Mno.length() == 10 && !(Code.isEmpty())){
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                                                        progressDialog = new ProgressDialog(signup_page.this);
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.setMessage("Checking Details...");
                                                        progressDialog.show();
                                                        verifyCode(Code);
                                                    }else{
                                                        Toast.makeText(signup_page.this, "Enter Valid details", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    Toast.makeText(signup_page.this, "Phone Number Already Exists", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(signup_page.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }


    public void onloginnn(View view) {
        Intent intent= new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }
}