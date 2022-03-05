package com.example.gossip.adaptor;

import static com.example.gossip.R.menu.friends_more_btn;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.Friends_Page;
import com.example.gossip.Login;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder>{
    Context context;
    ArrayList<UserFriends> userArrayList;
    FirebaseFirestore db;
    FirebaseUser fUser;
    UserFriends user;
    private Friends_Page friends_page;

    public RecyclerViewAdaptor(ArrayList<UserFriends> userArrayList,Context context) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    // Where to get the single card as viewholder Object
    @NonNull
    @Override
    public RecyclerViewAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    // What will happen after we create the viewholder object
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    // How many items?
    @Override
    public int getItemCount() {
        if (userArrayList.isEmpty()){
            return 0;
        }
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username;
        public TextView status;
        public ImageButton iconButton;
        public CircleImageView profileimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            username = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.status);
            iconButton = itemView.findViewById(R.id.more_button);
            profileimg = itemView.findViewById(R.id.profile_image);

            profileimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog dialog=new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.view_profile_dialog);

                    ImageView gallery_img =dialog.findViewById(R.id.profile_dialog);
                    TextView view_full_profile=dialog.findViewById(R.id.view_profile_dialog);

                    db = FirebaseFirestore.getInstance();
                    fUser = FirebaseAuth.getInstance().getCurrentUser();
                    try {
                        File tempFile = File.createTempFile("tempfile", ".jpg");
                        FirebaseStorage.getInstance().getReference("profile_photos/"+((userArrayList.get(getAdapterPosition()).getUsername()))).getFile(tempFile)
                                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                            gallery_img.setImageBitmap(bmp);
                                        }else{
                                            Toast.makeText(context, "Cannot Load Profile Image", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    view_full_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ViewProfile.class);
                            intent.putExtra("username",(userArrayList.get(getAdapterPosition()).getUsername()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations= R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.CENTER);
                }
            });

            iconButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)  {
                    PopupMenu popupMenu = new PopupMenu(context, iconButton);
                    popupMenu.getMenuInflater().inflate(R.menu.friends_more_btn, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.view_profile_fr:
                                    Intent intent = new Intent(context, ViewProfile.class);
                                    intent.putExtra("username",userArrayList.get(getAdapterPosition()).getUsername());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    break;
                                case R.id.send_msg_fr:
                                    Intent intent1 = new Intent(context,chatting_page.class);
                                    intent1.putExtra("username", userArrayList.get(getAdapterPosition()).getUsername());
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent1);
                                    break;
                                case R.id.remove_fr:
                                    final Dialog dialog=new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.remove_fr_dialog);

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
                                            if (fUser != null){
                                                new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                                                    @Override
                                                    public void onCallback(String currUser) {

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
//                                    userArrayList.remove(getAdapterPosition());
//                                    notifyDataSetChanged();
                                            }
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

        }
    }
}
