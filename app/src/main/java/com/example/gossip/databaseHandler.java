package com.example.gossip;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class databaseHandler {
    FirebaseFirestore db;

    public databaseHandler(){
        db = FirebaseFirestore.getInstance();
    }

    public void getdata(userCallback usercallback, String username){
        db.collection("Users")
                .document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            usercallback.onCallback(document.getData());
                        } else {
                            Toast.makeText(null, "Cannot get User's Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getChats(userCallback usercallback, String chatId){
        db.collection("Chats")
                .document(chatId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            usercallback.onCallback(document.getData());
                        }else{
                            Toast.makeText(null, "Cannot get User's Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public interface userCallback {
        void onCallback(Map userData);
    }

}


