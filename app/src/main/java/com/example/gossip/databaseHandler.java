package com.example.gossip;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void getCurrentUsername(currentUserCallBack currentusercallback){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null){
            String phone = fUser.getPhoneNumber().substring(3);
            db.collection("Users").whereEqualTo("phone", phone).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                String username = (task.getResult().getDocuments().get(0).get("username")).toString();
                                currentusercallback.onCallback(username);
                            }else{
                                Toast.makeText(null, "Cannot get users data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void getChatId(currentUserCallBack currentusercallback, String id1, String id2){
        db.collection("Chats").document(id1).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            currentusercallback.onCallback(id1);
                        }else{
                            currentusercallback.onCallback(id2);
                        }
                    }
                });
    }


    public interface userCallback {
        void onCallback(Map<String, Object> userData);
    }

    public interface currentUserCallBack{
        void onCallback(String currUser);
    }


}


