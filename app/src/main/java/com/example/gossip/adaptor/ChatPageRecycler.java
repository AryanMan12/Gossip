package com.example.gossip.adaptor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.R;
import com.example.gossip.UserFriends;
import com.example.gossip.ViewProfile;
import com.example.gossip.chatting_page;
import com.example.gossip.databaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPageRecycler extends RecyclerView.Adapter<ChatPageRecycler.ViewHolder> {
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    private FirebaseUser fUser;
    Context context;
    ArrayList<UserFriends> userArrayList;
    UserFriends user;

    public ChatPageRecycler(ArrayList<UserFriends> userArrayList, Context context) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public ChatPageRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatPageRecycler.ViewHolder holder, int position) {
        user = userArrayList.get(position);
        holder.username.setText(user.getUsername());
        holder.status.setText(user.getStatus());
        try {
            File tempFile = File.createTempFile("tempfile", ".jpg");
            FirebaseStorage.getInstance().getReference("profile_photos/"+user.getUsername()).getFile(tempFile)
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
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username;
        public TextView status;
        public ImageButton iconButton;
        public CircleImageView profileimg;
        public CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            username = itemView.findViewById(R.id.ch_username);
            status = itemView.findViewById(R.id.ch_status);
            iconButton = itemView.findViewById(R.id.ch_more_button);
            profileimg = itemView.findViewById(R.id.ch_profile_image);

            iconButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)  {
                    PopupMenu popupMenu = new PopupMenu(context, iconButton);
                    popupMenu.getMenuInflater().inflate(R.menu.chatpage_more_btn, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.view_profile_ch:
                                    Intent intent = new Intent(context, ViewProfile.class);
                                    intent.putExtra("username",userArrayList.get(getAdapterPosition()).getUsername());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    break;
                                case R.id.send_msg_ch:
                                    Intent intent1 = new Intent(context, chatting_page.class);
                                    intent1.putExtra("username", userArrayList.get(getAdapterPosition()).getUsername());
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                    break;
                                case R.id.remove_ch:
                                    new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                                        @Override
                                        public void onCallback(String currUser) {
                                            System.out.println(currUser);
                                            db = FirebaseFirestore.getInstance();
                                            db.collection("Users").document(currUser).update(
                                                    "friends", FieldValue.arrayRemove(userArrayList.get(getAdapterPosition()).getUsername())
                                            );
                                            db.collection("Users").document(userArrayList.get(getAdapterPosition()).getUsername()).update(
                                                    "friends", FieldValue.arrayRemove(currUser)
                                            );
                                            String id_1 = userArrayList.get(getAdapterPosition()).getUsername() + currUser;
                                            String id_2 = currUser + userArrayList.get(getAdapterPosition()).getUsername();
                                            new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                                                @Override
                                                public void onCallback(String chat_id) {
                                                    db.collection("Chats").document(chat_id)
                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(context, "Removed Friend!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }, id_1, id_2);
                                        }
                                    });
                                    break;
                                case R.id.delete_ch:
                                    Toast.makeText(context, "Chat deleted!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            });

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(context, chatting_page.class);
                    intent1.putExtra("username", userArrayList.get(getAdapterPosition()).getUsername());
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
            });

        }
        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");

        }
    }
}
