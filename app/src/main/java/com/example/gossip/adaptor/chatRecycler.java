package com.example.gossip.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gossip.R;

import com.example.gossip.databaseHandler;

import java.util.ArrayList;

public class chatRecycler extends RecyclerView.Adapter<chatRecycler.ViewHolder> {

    Context context;
    ArrayList<String> chats;
    ArrayList<String> users;
    String currUser;

    int SENDER_VIEW = 1;
    int RECEIVER_VIEW = 2;
    int posi;

    public chatRecycler(ArrayList<String> chats, ArrayList<String> users,String currUser, Context context){
        this.chats = chats;
        this.context = context;
        this.users = users;
        this.currUser = currUser;
    }

    @Override
    public int getItemViewType(int position) {
        posi = position;
        if (users.get(position).equals(currUser)){
            return SENDER_VIEW;
        }else{
            return RECEIVER_VIEW;
        }
    }

    @NonNull
    @Override
    public chatRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_VIEW){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver, parent, false);
        }
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = chats.get(position);
        holder.msg.setText(message);

    }

    @Override
    public int getItemCount() {
        if (users == null){
            return 0;
        }else{
            return users.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (users.get(posi).equals(currUser)){
                msg = itemView.findViewById(R.id.senderText);
            }else{
                msg = itemView.findViewById(R.id.receiverText);
            }

        }

        @Override
        public void onClick(View v) {
            Log.d("ChatRecycler", "Clicked");
        }
    }
}
