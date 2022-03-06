package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gossip.notification.Token;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

        EditText user, code;
        String Username, Code, verificationId;
        Button otp;
        FirebaseFirestore db;
        FirebaseUser cur_user;
        ProgressDialog progressDialog;
        boolean getotpclicked = false;
        FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            user=findViewById(R.id.login_name);
            code=findViewById(R.id.login_pass);
            db = FirebaseFirestore.getInstance();
            otp = findViewById(R.id.button6);
            mAuth = FirebaseAuth.getInstance();
        }

        public void onLogin(View view) {
            Username= user.getText().toString().trim();
            Code= code.getText().toString().trim();

            if (Username.equals("") || Code.equals("")){
                Toast.makeText(this, "Fields Cannot be Empty", Toast.LENGTH_SHORT).show();
            }else{
                db.collection("Users").whereEqualTo("username", Username).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()){
                                    user.setError("Username doesn't exists!!");
                                    user.requestFocus();
                                }else{
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                                    progressDialog = new ProgressDialog(Login.this);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Checking Details...");
                                    progressDialog.show();
                                    verifyCode(Code);
                                }
                            }
                        });
            }

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
                                cur_user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user !=null){
                                    Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    Login.this.finish();
                                }else {
                                    Toast.makeText(Login.this, "Error Logging in..", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }


        public void sendOtp(View view) {
                Username = user.getText().toString();
            db.collection("Users").whereEqualTo("username", Username).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!(task.getResult().isEmpty())){
                                String Mno = (task.getResult().getDocuments().get(0).get("phone")).toString();
                                if (!getotpclicked) {
                                    String phone_no = "+91" + Mno;
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(mAuth)
                                                    .setPhoneNumber(phone_no)
                                                    .setTimeout(60L, TimeUnit.SECONDS)
                                                    .setActivity(Login.this)
                                                    .setCallbacks(mCallBack)
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);
                                    progressDialog = new ProgressDialog(Login.this);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Sending Otp...");
                                    progressDialog.show();
                                }
                            }else{
                                user.setError("Username doesn't exists!!");
                                user.requestFocus();
                            }
                        }
                    });
        }

        private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            progressDialog.dismiss();
            Toast.makeText(Login.this, "Otp Sent", Toast.LENGTH_SHORT).show();
            verificationId = s;
            otp.setBackgroundColor(Color.parseColor("#1DCDCDCD"));
            otp.setTextColor(Color.parseColor("#FFFFFF"));

            new CountDownTimer(60000, 1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    otp.setText(""+millisUntilFinished/1000);
                }

                @Override
                public void onFinish() {
                    getotpclicked = false;
                    otp.setBackgroundColor(Color.parseColor("#EC8D1D"));
                    otp.setTextColor(Color.parseColor("#000000"));
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
            Toast.makeText(Login.this, "Failed to get Otp", Toast.LENGTH_SHORT).show();
        }
    };


        public void toggle_sign_up(View view) {
            Intent intent = new Intent(this,signup_page.class);
            startActivity(intent);
            finish();
        }
}

