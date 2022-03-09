package com.example.gossip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.RemoteInput;

import com.example.gossip.notification.ApiService;
import com.example.gossip.notification.Client;
import com.example.gossip.notification.Data;
import com.example.gossip.notification.FireBaseMessagingService;
import com.example.gossip.notification.MyResponse;
import com.example.gossip.notification.NotificationSender;
import com.example.gossip.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class receiveNotification extends BroadcastReceiver {
    ApiService apiService;
    FirebaseFirestore db;
    String fr_username;
    Context context;
    String notfy_chanel_id;

    @Override
    public void onReceive(Context context, Intent intent) {
        apiService =  Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
        fr_username = intent.getStringExtra("username");
        notfy_chanel_id = intent.getStringExtra("ch_id");
        db = FirebaseFirestore.getInstance();
        this.context = context;
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null){
            String message = remoteInput.getString("message_key");
            System.out.println(message);

            db.collection("NotifyToken").document(fr_username).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                String userToken = (task.getResult().get("token")).toString();
                                sendNotifications(userToken, fr_username, message);
                            }else{
                                Log.d("Send Notification", "Error");
                            }
                        }
                    });

            new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
                @Override
                public void onCallback(String currUser) {
                    String chatId1 = currUser + "" + fr_username;
                    String chatId2 = fr_username + "" + currUser;
                    new databaseHandler().getChatId(new databaseHandler.currentUserCallBack() {
                        @Override
                        public void onCallback(String chatId) {
                            db.collection("Chats").document(chatId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                ArrayList<String> users = (ArrayList<String>) task.getResult().get("users");
                                                ArrayList<String> chats = (ArrayList<String>) task.getResult().get("chats");
                                                if (users == null || chats == null) {
                                                    Map<String, Object> chatData = new HashMap<>();
                                                    users = new ArrayList<String>();
                                                    chats = new ArrayList<String>();
                                                    chatData.put("chats", chats);
                                                    chatData.put("users", users);
                                                    db.collection("Chats").document(chatId)
                                                            .set(chatData);
                                                }
                                                users.add(currUser);
                                                chats.add(message);
                                                db.collection("Chats").document(chatId)
                                                        .update("users", users);
                                                db.collection("Chats").document(chatId)
                                                        .update("chats", chats);
                                            } else {
                                                Log.d("Firebase Error", "Adding User in chat");
                                            }
                                        }
                                    });
                        }
                    }, chatId1, chatId2);
                }
            });
        
        }

    };

    public void sendNotifications(String usertoken, String title, String message){
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200){
                    if (response.body().success != 1){
                        Toast.makeText(context, "Failed to Notify", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}
