package com.example.gossip.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.R;
import com.example.gossip.UserFriends;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder>{
    Context context;
    ArrayList<UserFriends> userArrayList;

    public RecyclerViewAdaptor(Context context, ArrayList<UserFriends> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    public RecyclerViewAdaptor(ArrayList<UserFriends> filterList) {
        this.userArrayList = filterList;
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
        holder.status.setText(user.getStatus());
//        holder.profileimg.set(user.getProfile_img());

    }

    // How many items?
    @Override
    public int getItemCount() {
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
            iconButton = itemView.findViewById(R.id.remove_friends);
            profileimg = itemView.findViewById(R.id.profile_image);

            iconButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Log.d("ClickFromViewHolder", "Clicked");

        }
    }
}
