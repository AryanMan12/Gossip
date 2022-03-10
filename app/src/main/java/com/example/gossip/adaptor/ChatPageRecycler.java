package com.example.gossip.adaptor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.Encryption;
import com.example.gossip.R;
import com.example.gossip.UserFriends;
import com.example.gossip.ViewProfile;
import com.example.gossip.chatting_page;
import com.example.gossip.databaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private FirebaseUser fUser;
    Context context;
    ArrayList<String> username;
    ArrayList<String> status;
    String currentuser;
    String user;
    String bio;

    public ChatPageRecycler(ArrayList<String> username,ArrayList<String> status, String currentuser, Context context) {
        this.context = context;
        this.username =  username;
        this.status = status;
        this.currentuser = currentuser;
    }

    @NonNull
    @Override
    public ChatPageRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatPageRecycler.ViewHolder holder, int position) {
        user = username.get(position);
        bio = status.get(position);
        bio = new Encryption().decrypter(bio);
        holder.Username.setText(user);
        holder.Status.setText(bio);
        try {
            File tempFile = File.createTempFile("tempfile", ".jpg");
            FirebaseStorage.getInstance().getReference("profile_photos/"+user).getFile(tempFile)
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
        if (username != null){
            return username.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView Username;
        public TextView Status;
        public ImageButton iconButton;
        public CircleImageView profileimg;
        public CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            Username = itemView.findViewById(R.id.ch_username);
            Status = itemView.findViewById(R.id.ch_status);
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
                                    intent.putExtra("username",username.get(getAdapterPosition()));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    break;
                                case R.id.send_msg_ch:
                                    Intent intent1 = new Intent(context, chatting_page.class);
                                    intent1.putExtra("username", username.get(getAdapterPosition()));
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                    break;
                                case R.id.delete_ch:
                                    final Dialog dialog=new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.remove_fr_dialog_box);

                                    Button cancle = dialog.findViewById(R.id.cancle_btn);
                                    Button accept = dialog.findViewById(R.id.confirm_btn);

                                    cancle.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String id1 = currentuser + username.get(getAdapterPosition());
                                            String id2 = username.get(getAdapterPosition()) + currentuser;
                                            new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                                                @Override
                                                public void onCallback(String chatID) {
                                                    db = FirebaseFirestore.getInstance();
                                                    db.collection("Chats").document(chatID).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context, "Chat deleted!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Unable to delete!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }, id1, id2);
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.getWindow().getAttributes().windowAnimations= R.style.DialogAnimation;
                                    dialog.getWindow().setGravity(Gravity.CENTER);

                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            });

        }
        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");
            Intent intent1 = new Intent(context, chatting_page.class);
            intent1.putExtra("username",username.get(getAdapterPosition()));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
