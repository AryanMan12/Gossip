package com.example.gossip;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile_page#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile_page extends Fragment {
    private CircleImageView profile;
    private TextView change_photo;
    private TextView profile_uname;
    private EditText profile_name;
    private EditText profile_status;
    private TextView profile_no;

    ProgressDialog progressDialog;
    Uri tempUri;

    private FirebaseUser fUser;
    private FirebaseFirestore db;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profile_page() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile_page.
     */
    // TODO: Rename and change types and number of parameters
    public static profile_page newInstance(String param1, String param2) {
        profile_page fragment = new profile_page();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profile=view.findViewById(R.id.profile);
        change_photo=view.findViewById(R.id.change_photo);
        profile_uname=view.findViewById(R.id.profile_uname);
        profile_name=view.findViewById(R.id.profile_name);
        profile_status=view.findViewById(R.id.profile_status);
        profile_no=view.findViewById(R.id.profile_no);
        Button btnSave= view.findViewById(R.id.btnSave);
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("phone",fUser.getPhoneNumber().toString().substring(3)).get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    new databaseHandler().getdata(new databaseHandler.userCallback() {
                        @Override
                        public void onCallback(Map userData) {
                            System.out.println(userData);
                            if(userData!=null){

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
                                                        profile.setImageBitmap(bmp);
                                                        tempUri = getImageUri(bmp);
                                                    }else{
                                                        Toast.makeText(getActivity(), "Cannot Load Profile Image", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }, (task.getResult().getDocuments().get(0).get("username")).toString());
                }
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            open_dialog();
         //       Intent intent=new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
          //      startActivityForResult(intent,11);
            }
        });
    }
    public void open_dialog(){
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_dialog);

        LinearLayout gallery=dialog.findViewById(R.id.gallery);
        LinearLayout cam=dialog.findViewById(R.id.cam);
        LinearLayout del=dialog.findViewById(R.id.del);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Profile Photo Set",Toast.LENGTH_SHORT).show();
            }
        });
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Profile Photo Set",Toast.LENGTH_SHORT).show();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Profile Photo Deleted",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations= R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public Uri getImageUri(Bitmap inImage) {
        try {
            File tempDir= getActivity().getCacheDir();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        profile_page.super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Bitmap bmp=(Bitmap)data.getExtras().get("data");
            profile.setImageBitmap(bmp);
            tempUri = getImageUri(bmp);
        }
    }

    private void updateProfile(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();
        db.collection("Users").whereEqualTo("phone", fUser.getPhoneNumber().substring(3)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String userName = (task.getResult().getDocuments().get(0).get("username")).toString();
                        if(task.isSuccessful()){
                            FirebaseStorage.getInstance().getReference("profile_photos/"+userName).putFile(tempUri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                db.collection("Users").document(userName).update(
                                                        "name",profile_name.getText().toString(),
                                                        "status",profile_status.getText().toString()
                                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Photo Uploaded, but failed to upload details", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }else{
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Failed to Update Profile", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed to Update Profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }
}