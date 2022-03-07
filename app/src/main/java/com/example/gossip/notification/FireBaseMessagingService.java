package com.example.gossip.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.gossip.Friends_Page;
import com.example.gossip.MainActivity;
import com.example.gossip.chatting_page;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.gossip.R;

import java.util.ArrayList;

public class FireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ArrayList<String> messageID = new ArrayList<>();
        String CHANNEL_ID="MESSAGE";
        String CHANNEL_NAME="MESSAGE";
        NotificationManagerCompat manager=NotificationManagerCompat.from(getApplicationContext());
        title=remoteMessage.getData().get("Title");
        message=remoteMessage.getData().get("Message");
        Intent intent;
        PendingIntent pendingIntent;

        if (title.equals("New Request") || title.equals("Request Accepted")){
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }else{
            intent = new Intent(getApplicationContext(), chatting_page.class);
            intent.putExtra("username", title);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), messageID.indexOf(title), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        androidx.core.app.RemoteInput remoteInput = new RemoteInput.Builder("message_key").setLabel("Reply...").build();

        Intent replyIntent = new Intent(getApplicationContext(), chatting_page.class);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), messageID.indexOf(title), replyIntent, 0);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_baseline_send_24,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .addAction(replyAction)
                .setContentIntent(pendingIntent)
                .build();

        if (!(messageID.contains(title))){
            messageID.add(title);
        }

        manager.notify(messageID.indexOf(title), notification);
    }

}
