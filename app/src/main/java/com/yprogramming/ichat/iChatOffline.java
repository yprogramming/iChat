package com.yprogramming.ichat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by yourthor on 15/12/2560.
 */

public class iChatOffline extends Application {
    DatabaseReference userDbRef;
    FirebaseAuth userAuth;

    String current_user_key;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        userAuth = FirebaseAuth.getInstance();
        if(userAuth.getCurrentUser() == null) return;
        current_user_key = userAuth.getCurrentUser().getUid();
        userDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users").child(current_user_key);
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    DatabaseReference userOnline = FirebaseDatabase.getInstance().getReference().child("iChat").child("online");
                    userOnline.child(current_user_key).child("online_now").onDisconnect().setValue(false);
                    userOnline.child(current_user_key).child("last_seen").onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
