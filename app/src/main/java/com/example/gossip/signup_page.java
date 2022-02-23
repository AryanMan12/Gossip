package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class signup_page extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    EditText name, uname, mno,code;
    String Name,Uname,Mno,Code,verificationId;
    Button signup, otp;
    CircleImageView profile;
    FirebaseFirestore Db;
    FirebaseUser user;
    Map<String, Object> userData;
    ProgressDialog progressDialog;
    Uri tempUri;
    StorageReference storageReference;
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
        profile = findViewById(R.id.profile);
        try {
            File tempFile = File.createTempFile("tempfile", ".jpg");
            FirebaseStorage.getInstance().getReference("profile_photos/default_profile.jpg").getFile(tempFile)
                    .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                profile.setImageBitmap(bmp);
                                tempUri = getImageUri(bmp);
                            }else{
                                Toast.makeText(signup_page.this, "Cannot Load Default Image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                                storageReference = FirebaseStorage.getInstance().getReference("profile_photos/"+Uname);
                                storageReference.putFile(tempUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(signup_page.this, "Account Created", Toast.LENGTH_SHORT).show();
                                            Db.collection("Users").document(Uname).set(userData);
                                            Intent intent = new Intent(signup_page.this, MainActivity.class);
                                            startActivity(intent);
                                            signup_page.this.finish();
                                        }else{
                                            progressDialog.dismiss();
                                            Toast.makeText(signup_page.this, "Error Uploading Data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

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

    public void uploadimg(View view) {
        Intent intent=new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        startActivityForResult(intent,11);

    }

    public Uri getImageUri(Bitmap inImage) {
        try {
            File tempDir= this.getCacheDir();
            File tempFile = File.createTempFile("tempImage", ".jpg", tempDir);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return Uri.fromFile(tempFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Bitmap bmp=(Bitmap)data.getExtras().get("data");
            profile.setImageBitmap(bmp);
            tempUri = getImageUri(bmp);
        }
    }
}