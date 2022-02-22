package com.example.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {
    private String fr_username;
    private ImageView profile_img;
    private TextView profile_uname;
    private TextView profile_name;
    private TextView profile_status;
    private TextView profile_no;
    private Switch is_friend;
    FirebaseFirestore db;
    FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Intent retrive = getIntent();
        fr_username = retrive.getStringExtra("username");
        is_friend = findViewById(R.id.is_friend);
        profile_img = findViewById(R.id.fr_img);
        profile_uname = findViewById(R.id.fr_uname);
        profile_name = findViewById(R.id.fr_name);
        profile_status = findViewById(R.id.fr_status);
        profile_no = findViewById(R.id.fr_phone);
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
            new databaseHandler().getdata(new databaseHandler.userCallback() {
                @Override
                public void onCallback(Map userData) {
                    if(userData!=null){
                        ArrayList<String> friends = (ArrayList<String>)(userData.get("friends"));
                        db.collection("Users").whereEqualTo("phone",fUser.getPhoneNumber().toString().substring(3)).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                               if (task.isSuccessful()) {
                                                                   String current_user = (task.getResult().getDocuments().get(0).get("username")).toString();
                                                                   is_friend.setChecked(friends.contains(current_user));
                                                               }
                                                           }
                                                       });

                        profile_uname.setText((userData.get("username")).toString());
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
    }

    public void onSelect(View view) {
        Toast.makeText(this,"Added as a friend" ,Toast.LENGTH_LONG).show();
    }

    public void onBack(View view) {
        finish();
    }
}