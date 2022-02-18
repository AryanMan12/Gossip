package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class signup_page extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    EditText name, uname, mno, pwd, cpwd,code;
    String Name,Uname,Mno,Pwd,Cpwd,Code,verificationId, img;
    Button signup, otp;
    FirebaseFirestore Db;
    FirebaseUser user;
    Map userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        userData = new HashMap();

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
        if (Mno.length() != 10){
            Toast.makeText(this, "Phone Number is not Valid", Toast.LENGTH_SHORT).show();
        }else{
            String phone_no = "+91"+Mno;
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phone_no)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(mCallBack)
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
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
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user !=null){
                                Toast.makeText(signup_page.this, "Account Created", Toast.LENGTH_SHORT).show();
                                Db.collection("Users").document(Uname).set(userData);
                                Intent intent = new Intent(signup_page.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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
            Toast.makeText(signup_page.this, "Otp Sent", Toast.LENGTH_SHORT).show();
            verificationId = s;
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
            Toast.makeText(signup_page.this, "Failed to get Otp", Toast.LENGTH_SHORT).show();
        }
    };


    public void signup(View view) {
        Name=name.getText().toString().trim();
        Uname=uname.getText().toString().trim();
        Mno=mno.getText().toString().trim();
        Pwd=pwd.getText().toString().trim();
        Cpwd=cpwd.getText().toString().trim();
        Code=code.getText().toString().trim();
        userData.put("name", Name);
        userData.put("username", Uname);
        userData.put("phone", Mno);
        userData.put("profile_photo", img);
        userData.put("status", "");

        if(Name.equals("") || Uname.equals("") || Mno.equals("") || Pwd.equals("") || Cpwd.equals("") || Code.equals("")){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        }else if (Db.collection("Users").whereEqualTo("username",Uname).get().getResult().size() == 1){
            Toast.makeText(this, "Username already existing", Toast.LENGTH_LONG).show();
        }else if (Db.collection("Users").whereEqualTo("phone",Mno).get().getResult().size() == 1){
            Toast.makeText(this, "Already have an account", Toast.LENGTH_LONG).show();
        }else if (!(Pwd.equals(Cpwd))){
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        }else{
            verifyCode(Code);
        }

    }


    public void onloginnn(View view) {
        Intent intent= new Intent(this,Login.class);
        startActivity(intent);
    }
}