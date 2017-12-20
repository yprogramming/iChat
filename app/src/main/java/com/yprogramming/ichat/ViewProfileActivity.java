package com.yprogramming.ichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    private Toolbar ichViewProfileChatToolBar;

    private FirebaseAuth userAuth;
    private DatabaseReference viewDbRef;
    private DatabaseReference userOnlineDbRef;

    private CircleImageView imgViewProfile;
    private TextView lblViewName, lblViewStatus;
    private Button btnChatNow;

    private String view_id, view_name, view_url, gender, status, view_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ichViewProfileChatToolBar = (Toolbar)findViewById(R.id.ichViewProfileChatToolBar);
        setSupportActionBar(ichViewProfileChatToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        view_id = getIntent().getStringExtra("view_id");
        view_type = getIntent().getStringExtra("view_type");

        imgViewProfile = (CircleImageView)findViewById(R.id.imgViewProfile);
        lblViewName = (TextView)findViewById(R.id.lblViewName);
        lblViewStatus = (TextView)findViewById(R.id.lblViewStatus);
        btnChatNow = (Button)findViewById(R.id.btnChatNow);

        if (view_type.equals("uprofile")){
            getSupportActionBar().setTitle("Profile");
            btnChatNow.setText("ແກ້ໄຂຂໍ້ມູນສ່ວນຕົວ");
        }else if (view_type.equals("friend")){
            getSupportActionBar().setTitle("Friend");
            btnChatNow.setText("ສົນທະນາຕອນນີ້");
        }

        userAuth = FirebaseAuth.getInstance();
        userOnlineDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("online").child(userAuth.getCurrentUser().getUid());

        viewDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users");
        viewDbRef.child(view_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile view_profile = dataSnapshot.getValue(userProfile.class);
                view_name = view_profile.getFullname();
                view_url = view_profile.getProfileUrl();
                gender = view_profile.getGender();
                status = view_profile.getStatus();
                if(!view_url.equals("")){
                    Glide.with(ViewProfileActivity.this)
                            .load(view_url)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(R.drawable.ic_user)
                            .into(imgViewProfile);
                }else {
                    if(gender.equals("ຊາຍ"))
                        imgViewProfile.setImageResource(R.drawable.ic_user_man);
                    else
                        imgViewProfile.setImageResource(R.drawable.ic_user_woman);
                }
                lblViewName.setText(view_name);
                lblViewStatus.setText(view_profile.getStatus());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnChatNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view_type.equals("uprofile")){

                    Intent profileEditIntent = new Intent(ViewProfileActivity.this, ProfileActivity.class);
                    profileEditIntent.putExtra("user_check", "exist");
                    profileEditIntent.putExtra("user_id", view_id);
                    profileEditIntent.putExtra("user_name", view_name);
                    profileEditIntent.putExtra("user_status", status);
                    profileEditIntent.putExtra("user_url", view_url);
                    profileEditIntent.putExtra("user_gender", gender);
                    startActivity(profileEditIntent);

                }else if (view_type.equals("friend")){

                    Intent chatDetailIntent = new Intent(ViewProfileActivity.this, ChatDetailActivity.class);
                    chatDetailIntent.putExtra("friend_id", view_id);
                    chatDetailIntent.putExtra("friend_name", view_name);
                    chatDetailIntent.putExtra("friend_url", view_url);
                    chatDetailIntent.putExtra("friend_gender", gender);
                    startActivity(chatDetailIntent);

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!getInternetConnection()){
            return;
        }
        userOnlineDbRef.child("online_now").setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!getInternetConnection()){
            return;
        }
        userOnlineDbRef.child("online_now").setValue(false);
        userOnlineDbRef.child("last_seen").setValue(ServerValue.TIMESTAMP);
    }

    private boolean getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean checkInternetConnection = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return checkInternetConnection;
    }
}
