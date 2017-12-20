package com.yprogramming.ichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpreshScreenActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth userAuth;
    private DatabaseReference userDbRef;

    private CircleImageView imgIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spresh_screen);


        userAuth = FirebaseAuth.getInstance();
        userDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users");
        userDbRef.keepSynced(true);

        Thread splashThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    //If current user equal null redirect to start activity the exit
                    if(userAuth.getCurrentUser() == null){
                        Intent start = new Intent(SpreshScreenActivity.this, StartActity.class);
                        startActivity(start);
                        finish();
                        return;
                    }

                    //Otherwise check user data on database
                    final String userKey = userAuth.getCurrentUser().getUid();
                    userDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userKey = userAuth.getCurrentUser().getUid();
                            //If user data is exist in database then go to main activity otherwise back to save profile on profile activity
                            if(!dataSnapshot.hasChild(userKey)){
                                Intent intentProfile = new Intent(SpreshScreenActivity.this, ProfileActivity.class);
                                intentProfile.putExtra("user_id", "");
                                startActivity(intentProfile);
                                finish();
                            }else {
                                variable.userProfileUrl = dataSnapshot.child(userKey).child("profileUrl").getValue().toString();
                                variable.user_gender = dataSnapshot.child(userKey).child("gender").getValue().toString();
                                variable.checkMenu = true;
                                Intent intentIChat = new Intent(SpreshScreenActivity.this, IChatActivity.class);
                                intentIChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentIChat);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(SpreshScreenActivity.this, "ເກີດຂໍ້ຜິດພາດລະຫວ່າງດຳເນີນການ...", Toast.LENGTH_LONG).show();
                            finish();
                            System.exit(0);
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(SpreshScreenActivity.this, "ເກີດຂໍ້ຜິດພາດລະຫວ່າງດຳເນີນການ...", Toast.LENGTH_LONG).show();
                    finish();
                    System.exit(0);
                }
            }
        };
        splashThread.start();

    }
}
