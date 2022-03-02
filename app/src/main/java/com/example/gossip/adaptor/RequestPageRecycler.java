package com.example.gossip.adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.R;
import com.example.gossip.ViewProfile;
import com.example.gossip.databaseHandler;
import com.google.android.gms.common.internal.ResourceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestPageRecycler extends RecyclerView.Adapter<RequestPageRecycler.ViewHolder>{

    Context context;
    ArrayList<Map<String, Object>> reqList;
    Map<String, Object> user;
    Map<String, Object> current_user;
    FirebaseFirestore db;
    List<String> user_friends;
    List<String> user_requests;
    List<String> curr_requests;
    View.OnClickListener mOnClickListener;
    int posi;

    public RequestPageRecycler(ArrayList<Map<String, Object>> reqList, Map<String, Object> current_user, Context context){
        this.context = context;
        this.reqList = reqList;
        this.current_user = current_user;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        posi = position;
        user = reqList.get(position);
        user_friends = (ArrayList<String>)user.get("friends");
        user_requests = (ArrayList<String>)user.get("requests");
        curr_requests = (ArrayList<String>)current_user.get("requests");
        if (user_friends.contains((current_user.get("username")).toString())){
            return 1;
        }else if(curr_requests.contains((user.get("username")).toString())) {
            return 2;
        }else if (!(user_requests.contains((current_user.get("username")).toString()))){
            return 3;
        }else{
            return 4;
        }
    }

    @NonNull
    @Override
    public RequestPageRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_friend_row, parent, false);
        }else if(viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remove_request_row, parent, false);
        }else if (viewType == 3){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_req_row, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);
        }
        view.setOnClickListener(mOnClickListener);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user = reqList.get(position);
        holder.username.setText((user.get("username")).toString());
        holder.status.setText((user.get("status")).toString());

        try {
            File tempFile = File.createTempFile("tempfile", ".jpg");
            FirebaseStorage.getInstance().getReference("profile_photos/"+(user.get("username")).toString()).getFile(tempFile)
                    .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                holder.profileimg.setImageBitmap(bmp);
                            }else{
                                Toast.makeText(context, "Cannot Load Profile Image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (reqList != null){
            return  reqList.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView username;
        public TextView status;
        public ImageView acceptReq, rejectReq, addReq, removeReq, chatReq;
        public CircleImageView profileimg;
        boolean x = true;
        boolean y = true;
        boolean z = true;
        int w = 0;


        View.OnClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            username = itemView.findViewById(R.id.req_username);
            status = itemView.findViewById(R.id.req_status);
            profileimg = itemView.findViewById(R.id.req_profile_image);
            acceptReq = itemView.findViewById(R.id.accept_req);
            rejectReq = itemView.findViewById(R.id.reject_req);
            chatReq = itemView.findViewById(R.id.req_chat);
            removeReq = itemView.findViewById(R.id.req_remove);
            addReq = itemView.findViewById(R.id.req_add);


            profileimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewProfile.class);
                    intent.putExtra("username",(reqList.get(getAdapterPosition()).get("username")).toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            if (getAdapterPosition() != -1){
                user = reqList.get(getAdapterPosition());
            }
            user_friends = (ArrayList<String>)user.get("friends");
            user_requests = (ArrayList<String>)user.get("requests");
            curr_requests = (ArrayList<String>)current_user.get("requests");
            if (user_friends.contains((current_user.get("username")).toString())){
                chatReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, com.example.gossip.chatting_page.class);
                        intent.putExtra("username",(reqList.get(getAdapterPosition()).get("username")).toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }else if(curr_requests.contains((user.get("username")).toString())) {

                removeReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(x){
                            db.collection("Users").document((current_user.get("username")).toString())
                                    .update("requests", FieldValue.arrayRemove((reqList.get(getAdapterPosition()).get("username")).toString()));
                            removeReq.setImageResource(R.drawable.ic_baseline_person_add_24);
                        }else{
                            db.collection("Users").document((current_user.get("username")).toString())
                                    .update("requests", FieldValue.arrayUnion((reqList.get(getAdapterPosition()).get("username")).toString()));
                            removeReq.setImageResource(R.drawable.ic_baseline_person_remove_24);
                        }
                        x = !x;
                        notifyDataSetChanged();
                    }
                });
            }else if (!(user_requests.contains((current_user.get("username")).toString()))){
                addReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(y) {
                            db.collection("Users").document((current_user.get("username")).toString())
                                    .update("requests", FieldValue.arrayUnion((reqList.get(getAdapterPosition()).get("username")).toString()));
                            addReq.setImageResource(R.drawable.ic_baseline_person_remove_24);
                        }else{
                            db.collection("Users").document((current_user.get("username")).toString())
                                    .update("requests", FieldValue.arrayRemove((reqList.get(getAdapterPosition()).get("username")).toString()));
                            addReq.setImageResource(R.drawable.ic_baseline_person_add_24);
                        }
                        y = !y;
                        notifyDataSetChanged();
                    }
                });
            }else{
                acceptReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!z){
                            Intent intent = new Intent(context, com.example.gossip.chatting_page.class);
                            intent.putExtra("username",(reqList.get(getAdapterPosition()).get("username")).toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else{
                            db.collection("Users").document((current_user.get("username")).toString())
                                    .update("friends", FieldValue.arrayUnion((reqList.get(getAdapterPosition()).get("username")).toString()));
                            db.collection("Users").document((reqList.get(getAdapterPosition()).get("username")).toString())
                                    .update("friends", FieldValue.arrayUnion((current_user.get("username")).toString()),
                                            "requests", FieldValue.arrayRemove((current_user.get("username")).toString()));
                            acceptReq.setVisibility(View.GONE);
                            rejectReq.setImageResource(R.drawable.ic_baseline_chat_24);
                        }
                        notifyDataSetChanged();

                    }
                });
                rejectReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(w == 1){
                            db.collection("Users").document((reqList.get(getAdapterPosition()).get("username")).toString())
                                    .update("requests", FieldValue.arrayRemove((current_user.get("username")).toString()));
                            notifyDataSetChanged();
                            acceptReq.setVisibility(View.GONE);
                            rejectReq.setImageResource(R.drawable.ic_baseline_person_add_24);
                            w = 2;
                        }else if(w == 2){
                            db.collection("Users").document((current_user.get("username")).toString())
                                .update("requests", FieldValue.arrayUnion((reqList.get(getAdapterPosition()).get("username")).toString()));
                            rejectReq.setImageResource(R.drawable.ic_baseline_person_remove_24);
                            w = 1;
                        }else{
                            db.collection("Users").document((current_user.get("username")).toString())
                                    .update("requests", FieldValue.arrayRemove((reqList.get(getAdapterPosition()).get("username")).toString()));
                            rejectReq.setImageResource(R.drawable.ic_baseline_person_add_24);
                            w = 2;
                        }

                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, com.example.gossip.ViewProfile.class);
            intent.putExtra("username", (reqList.get(getAdapterPosition()).get("username")).toString());
            context.startActivity(intent);
        }

    }

}
