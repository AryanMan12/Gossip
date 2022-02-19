package com.example.gossip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


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
    private EditText profile_uname;
    private EditText profile_name;
    private EditText profile_status;
    private EditText profile_no;

    private FirebaseUser fUser;
    private FirebaseFirestore db;
    private Map MapUserData;

    String currentUser;

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
                Intent intent=new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
                getActivity().startActivityForResult(intent,11);

            }

            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                profile_page.super.onActivityResult(requestCode, resultCode, data);
                Bitmap bmp=(Bitmap)data.getExtras().get("data");
                profile.setImageBitmap(bmp);

         }
        });
    }
    private void updateProfile(){
        HashMap<String,Object> map= new HashMap<>();
        map.put("username",profile_uname.getText().toString());
        map.put("name",profile_name.getText().toString());
        map.put("status",profile_status.getText().toString());
        map.put("phone",profile_no.getText().toString());
        db.collection("Users").whereEqualTo("phone", fUser.getPhoneNumber().substring(3)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            db.collection("Users").document((task.getResult().getDocuments().get(0).get("username")).toString()).update(
                                    "name",profile_name.getText().toString(),
                                    "status",profile_status.getText().toString()
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });



    }
}