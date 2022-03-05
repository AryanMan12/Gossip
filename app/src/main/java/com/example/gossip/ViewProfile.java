package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gossip.notification.ApiService;
import com.example.gossip.notification.Data;
import com.example.gossip.notification.MyResponse;
import com.example.gossip.notification.NotificationSender;
import com.example.gossip.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfile extends AppCompatActivity {
    private String fr_username;
    private CircleImageView profile_img;
    private TextView profile_uname;
    private TextView profile_name;
    private TextView profile_status;
    private TextView profile_no;
    private Button friends_btn;
    ApiService apiService;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Intent retrive = getIntent();
        fr_username = retrive.getStringExtra("username");
        profile_img = findViewById(R.id.fr_img);
        profile_uname = findViewById(R.id.fr_uname);
        profile_name = findViewById(R.id.fr_name);
        profile_status = findViewById(R.id.fr_status);
        profile_no = findViewById(R.id.fr_phone);
        friends_btn = findViewById(R.id.button);

        db = FirebaseFirestore.getInstance();
        new databaseHandler().getdata(new databaseHandler.userCallback() {
            @Override
            public void onCallback(Map userData) {
                if(userData!=null){
                    profile_uname.setText("@ " + (userData.get("username")).toString());
                    profile_status.setText((userData.get("status")).toString());
                    profile_no.setText((userData.get("phone")).toString());
                    profile_name.setText((userData.get("name")).toString());
                    try {
                        File tempFile = File.createTempFile("tempfile", ".jpg");
                        FirebaseStorage.getInstance().getReference("profile_photos/"+(userData.get("username")).toString()).getFile(tempFile)
                                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                            profile_img.setImageBitmap(bmp);
                                        }else{
                                            Toast.makeText(ViewProfile.this, "Cannot Load Profile Image", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        },fr_username);

        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                new databaseHandler().getdata(new databaseHandler.userCallback() {
                    @Override
                    public void onCallback(Map<String, Object> currUserData) {
                        new databaseHandler().getdata(new databaseHandler.userCallback() {
                            @Override
                            public void onCallback(Map<String, Object> frUserData) {
                                ArrayList<String> friendList = (ArrayList<String>) frUserData.get("friends");
                                ArrayList<String> frReqList = (ArrayList<String>) frUserData.get("requests");
                                ArrayList<String> crReqList = (ArrayList<String>) currUserData.get("requests");
                                if (friendList.contains(currUser)){
                                    friends_btn.setText("Remove Friend");
                                }else if (frReqList.contains(currUser)){
                                    friends_btn.setText("Accept");
                                }else if (crReqList.contains(fr_username)){
                                    friends_btn.setText("Cancel Request");
                                }else{
                                    friends_btn.setText("Add Friend");
                                }
                            }
                        }, fr_username);
                    }
                }, currUser);
            }
        });
    }

    public void onSelect(View view) {
        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                if (friends_btn.getText().toString().equals("Remove Friend")) {
                    db.collection("Users").document(currUser).update(
                            "friends", FieldValue.arrayRemove(fr_username)
                    );
                    db.collection("Users").document(fr_username).update(
                            "friends", FieldValue.arrayRemove(currUser)
                    );
                    String id_1 = fr_username + currUser;
                    String id_2 = currUser + fr_username;
                    new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                        @Override
                        public void onCallback(String chat_id) {
                            db.collection("Chats").document(chat_id)
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ViewProfile.this, "Removed Friend!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }, id_1, id_2);
                    friends_btn.setText("Add Friend");
                    finish();
                }
                else if(friends_btn.getText().toString().equals("Accept")){
                    db.collection("Users").document(currUser)
                            .update("friends", FieldValue.arrayUnion(fr_username));
                    db.collection("Users").document(fr_username)
                            .update("friends", FieldValue.arrayUnion(currUser),
                                    "requests", FieldValue.arrayRemove(currUser));
                    db.collection("NotifyToken").document(fr_username).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String userToken = (task.getResult().get("token")).toString();
                                        sendNotifications(userToken, "New Request",currUser+" accepted your Friend Request!");
                                    }else{
                                        Log.d("Send Notification", "Error");
                                    }
                                }
                            });
                    friends_btn.setText("Remove Friend");
                }
                else if(friends_btn.getText().toString().equals("Cancel Request")){
                    db.collection("Users").document(currUser)
                            .update("requests", FieldValue.arrayRemove(fr_username));
                    friends_btn.setText("Add Friend");
                }else{
                    db.collection("Users").document(currUser)
                            .update("requests", FieldValue.arrayUnion(fr_username));
                    db.collection("NotifyToken").document(fr_username).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String userToken = (task.getResult().get("token")).toString();
                                        sendNotifications(userToken, "New Request",currUser+" sent you Friend Request!");
                                    }else{
                                        Log.d("Send Notification", "Error");
                                    }
                                }
                            });
                    friends_btn.setText("Cancel Request");
                }
            }
        });
    }

    public void UpdateToken(){
        db = FirebaseFirestore.getInstance();
        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                db.collection("NotifyToken").document(currUser).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (task.isSuccessful()){
                                                        String refreshToken = task.getResult();
                                                        Token token1= new Token(refreshToken);
                                                        db.collection("NotifyToken").document(currUser).update("token", token1.getToken());
                                                    }else{
                                                        Log.d("Update Token:", "No Token Found");
                                                    }
                                                }
                                            });

                                }else{
                                    Toast.makeText(ViewProfile.this, "Failed to Send Notification", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void sendNotifications(String usertoken, String title, String message){
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200){
                    if (response.body().success != 1){
                        Toast.makeText(ViewProfile.this, "Failed to Notify", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    public void onBack(View view) {
        finish();
    }
}