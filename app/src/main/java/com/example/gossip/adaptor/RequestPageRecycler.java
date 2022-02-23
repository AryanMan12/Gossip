package com.example.gossip.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.R;
import com.example.gossip.ViewProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestPageRecycler extends RecyclerView.Adapter<RequestPageRecycler.ViewHolder>{

    Context context;
    ArrayList<Map<String, Object>> reqList;
    Map<String, Object> user;

    public RequestPageRecycler(ArrayList<Map<String, Object>> reqList, Context context){
        this.context = context;
        this.reqList = reqList;
    }

    @NonNull
    @Override
    public RequestPageRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);
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
        holder.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProfile.class);
                intent.putExtra("username",(user.get("username")).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
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
        public ImageView iconButton;
        public CircleImageView profileimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            username = itemView.findViewById(R.id.req_username);
            status = itemView.findViewById(R.id.req_status);
            iconButton = itemView.findViewById(R.id.req_more_button);
            profileimg = itemView.findViewById(R.id.req_profile_image);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewProfile.class);
            intent.putExtra("username",(user.get("username")).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
