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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gossip.adaptor.RecyclerViewAdaptor;
import com.example.gossip.databinding.ActivityChattingPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatting_page extends AppCompatActivity {
    private String fr_username;
    private TextView userName;
    private CircleImageView profileImage;
    private EditText msg;
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
        msg = findViewById(R.id.message);

        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.chatReclarView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new databaseHandler().getdata(new databaseHandler.userCallback() {
            @Override
            public void onCallback(Map<String, Object> userData) {
                userName.setText(fr_username);
                try {
                    File tempFile = File.createTempFile("tempfile", ".jpg");
                    FirebaseStorage.getInstance().getReference("profile_photos/" + (userData.get("username")).toString()).getFile(tempFile)
                            .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                        profileImage.setImageBitmap(bmp);
                                    } else {
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
                String chatId1 = currUser + "" + fr_username;
                String chatId2 = fr_username + "" + currUser;

                new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                    @Override
                    public void onCallback(String chatId) {
                        db.collection("Chats").document(chatId)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Log.e("FireStore error", error.getMessage());
                                            return;
                                        }
                                        if (value != null && value.exists()) {
                                            db.collection("Chats").document(chatId)
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        ArrayList<String> users;
                                                        ArrayList<String> chats;
                                                        if (task.getResult().exists()) {
                                                            users = (ArrayList<String>) task.getResult().get("users");
                                                            chats = (ArrayList<String>) task.getResult().get("chats");
                                                        } else {
                                                            users = new ArrayList<>();
                                                            chats = new ArrayList<>();
                                                        }
                                                        recyclerViewAdapter = new chatRecycler(chats, users, currUser, chatting_page.this);
                                                        recyclerView.setAdapter(recyclerViewAdapter);
                                                    } else {
                                                        Toast.makeText(chatting_page.this, "Failed to load Chats", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.e("FireStore error", "No Data");
                                        }
                                    }
                                });
                    }
                }, chatId1, chatId2);

            }
        });

    }

    public void onViewProfile(View view) {
        Intent intent = new Intent(this, ViewProfile.class);
        intent.putExtra("username", fr_username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onSendMessage(View view) {
        String message = msg.getText().toString();
        if (message.trim().equals("")) {
            Toast.makeText(this, "Message cannot be Empty", Toast.LENGTH_SHORT).show();
        } else {
            new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                @Override
                public void onCallback(String currUser) {
                    String chatId1 = currUser + "" + fr_username;
                    String chatId2 = fr_username + "" + currUser;
                    new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                        @Override
                        public void onCallback(String chatId) {
                            db.collection("Chats").document(chatId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                ArrayList<String> users = (ArrayList<String>) task.getResult().get("users");
                                                ArrayList<String> chats = (ArrayList<String>) task.getResult().get("chats");
                                                if (users == null || chats == null) {
                                                    Map<String, Object> chatData = new HashMap<>();
                                                    users = new ArrayList<String>();
                                                    chats = new ArrayList<String>();
                                                    chatData.put("chats", chats);
                                                    chatData.put("users", users);
                                                    db.collection("Chats").document(chatId)
                                                            .set(chatData);
                                                }
                                                users.add(currUser);
                                                chats.add(message);
                                                db.collection("Chats").document(chatId)
                                                        .update("users", users);
                                                db.collection("Chats").document(chatId)
                                                        .update("chats", chats);
                                            } else {
                                                Log.d("Firebase Error", "Adding User in chat");
                                            }
                                        }
                                    });
                        }
                    }, chatId1, chatId2);
                }
            });
            msg.setText("");
        }

    }

    public void onBack(View view) {
        finish();
    }

    public void onMenu(View view) {
        ImageView btn1;
        btn1 = findViewById(R.id.options);
        PopupMenu popupMenu = new PopupMenu(chatting_page.this, btn1);
        popupMenu.getMenuInflater().inflate(R.menu.chattingpage_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile_view_chp:
                        Intent intent = new Intent(chatting_page.this, ViewProfile.class);
                        intent.putExtra("username",fr_username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case R.id.search_chp:
                        Toast.makeText(chatting_page.this, "Search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.delete_chp:
                        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                            @Override
                            public void onCallback(String currentuser) {
                                String id1 = currentuser + fr_username;
                                String id2 = fr_username + currentuser;
                                new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                                    @Override
                                    public void onCallback(String chatID) {
                                        db = FirebaseFirestore.getInstance();
                                        db.collection("Chats").document(chatID).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(chatting_page.this, "Chat deleted!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(chatting_page.this, "Unable to delete!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }, id1, id2);
                            }
                        });
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}