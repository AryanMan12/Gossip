package com.example.gossip.notification;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gossip.databaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseIdService extends FirebaseMessagingService {

    FirebaseFirestore db;

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        db = FirebaseFirestore.getInstance();
        new databaseHandler().getCurrentUsername(new databaseHandler.currentUserCallBack() {
            @Override
            public void onCallback(String currUser) {
                db.collection("NotifyToken").document(currUser).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (task.isSuccessful()){
                                                        String refreshToken = task.getResult();
                                                        Token token1= new Token(refreshToken);
                                                        db.collection("NotifyToken").document(currUser).update("token", token1.getToken());
                                                    }else{
                                                        Log.d("Update Token:", "No Token Found");
                                                    }
                                                }
                                            });
                                }else{
                                    Toast.makeText(FirebaseIdService.this, "Failed to Send Notification", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
