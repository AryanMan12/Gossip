package com.example.gossip.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.R;
import com.example.gossip.UserFriends;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {
    Context context;
    ArrayList<UserFriends> userArrayList;

    public RecyclerViewAdaptor(Context context, ArrayList<UserFriends> userArrayList) {
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
        UserFriends user = userArrayList.get(position);

        holder.username.setText(user.getUsername());
        holder.desc.setText(user.getDesc());
//        holder.profileimg.set(user.getProfile_img());

    }

    // How many items?
    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username;
        public TextView desc;
        public ImageButton iconButton;
        public CircleImageView profileimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            username = itemView.findViewById(R.id.username);
            desc = itemView.findViewById(R.id.desc);
            iconButton = itemView.findViewById(R.id.more_button);
            profileimg = itemView.findViewById(R.id.profile_image);

            iconButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");

        }
    }
}
