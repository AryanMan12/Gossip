package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gossip.adaptor.chatRecycler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gossip.adaptor.RecyclerViewAdaptor;
import com.example.gossip.databinding.ActivityChattingPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatting_page extends AppCompatActivity {
    private String fr_username;
    private TextView userName;
    private CircleImageView profileImage;
    private ImageView send;
    private ImageView options;
    private ImageView back;

    private RecyclerView recyclerView;
    private chatRecycler recyclerViewAdapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_page);
        Intent retrive = getIntent();
        fr_username = retrive.getStringExtra("username");
        userName = findViewById(R.id.userName);
        profileImage = findViewById(R.id.profile_image);
        send = findViewById(R.id.send);
        options = findViewById(R.id.options);
        back = findViewById(R.id.backArrow);

        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.chatReclarView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new databaseHandler().getdata(new databaseHandler.userCallback() {
            @Override
            public void onCallback(Map<String, Object> userData) {
                userName.setText(fr_username);
                try {
                    File tempFile = File.createTempFile("tempfile", ".jpg");
                    FirebaseStorage.getInstance().getReference("profile_photos/"+(userData.get("username")).toString()).getFile(tempFile)
                            .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                        profileImage.setImageBitmap(bmp);
                                    }else{
                                        Toast.makeText(chatting_page.this, "Cannot Load Profile Image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, fr_username);

        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                String chatId1 = currUser+""+fr_username;
                String chatId2 = fr_username+""+currUser;

                new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                    @Override
                    public void onCallback(String chatId) {
                        System.out.println(chatId);
                        db.collection("Chats").document(chatId)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null){
                                            Log.e("FireStore error", error.getMessage());
                                            return;
                                        }
                                        if (value != null && value.exists()){
                                            Map<String, Object> data = value.getData();
                                            ArrayList<String> users = (ArrayList<String>) data.get("users");
                                            ArrayList<String> chats = (ArrayList<String>) data.get("chats");

                                            recyclerViewAdapter = new chatRecycler(chats, users,currUser, chatting_page.this);
                                            recyclerView.setAdapter(recyclerViewAdapter);

                                        }else{
                                            Log.e("FireStore error", "No Data");
                                        }
                                    }
                                });
                    }
                }, chatId1, chatId2);

            }
        });
    }

}