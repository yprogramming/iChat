package com.yprogramming.ichat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    //Declare any java ui objects
    private Button btnSaveProfile;
    private RadioButton rdbMale, rdbFemale;
    private EditText txtName, txtStatus;
    private ImageButton imgProfile;
    private Toolbar ichProfileChatToolBar;

    private boolean checkClickProfile = false;
    private String profileUrl = "";

    //Database
    private DatabaseReference userDbRef;
    private DatabaseReference userOnlineDbRef;
    private StorageReference storageRef;

    private static final int GALLERY_REQUEST = 1;
    private Uri profileImageUri = null;

    private userProfile profile;

    private ProgressDialog dialog;

    private String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ichProfileChatToolBar = (Toolbar)findViewById(R.id.ichProfileChatToolBar);
        setSupportActionBar(ichProfileChatToolBar);
        getSupportActionBar().setTitle("Profile");
        if (variable.checkMenu) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_id = getIntent().getStringExtra("user_id");

        dialog = new ProgressDialog(this);
        dialog.setTitle("iChat");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        //Firebase Reference
        userDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users");
        userOnlineDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("online").child(user_id);
        storageRef = FirebaseStorage.getInstance().getReference().child("iChat");

        //Connect java ui Objects to view objects
        imgProfile = (ImageButton) findViewById(R.id.imgPicture);
        txtName = (EditText) findViewById(R.id.txtName);
        txtStatus = (EditText) findViewById(R.id.txtStatus);
        rdbMale = (RadioButton) findViewById(R.id.rdbMale);
        rdbFemale = (RadioButton) findViewById(R.id.rdbFemale);
        btnSaveProfile = (Button) findViewById(R.id.btnSave);

        rdbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdbFemale.setChecked(false);
            }
        });

        rdbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdbMale.setChecked(false);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                profilePictureIntent.setType("image/*");
                startActivityForResult(profilePictureIntent, GALLERY_REQUEST);
            }
        });
        //Add click event to button save profile
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!getInternetConnection()){
                Toast.makeText(ProfileActivity.this, "ຂາດການເຊື່ອມຕໍ່ອິນເຕເນັດ", Toast.LENGTH_LONG).show();
                return;
            }
            dialog.setMessage("ກຳລັງບັນທືກຂໍ້ມູນ...");
            final String name = txtName.getText().toString();
            final String status = txtStatus.getText().toString();
            final boolean validate = !TextUtils.isEmpty(name);
            if (validate){
                dialog.show();
                if (profileImageUri != null && checkClickProfile){
                    checkClickProfile = false;
                    StorageReference userProfileStorageRef = storageRef.child("users").child(user_id).child(variable.randomProfileName());
                    userProfileStorageRef.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            profileImageUri = null;
                            Uri profileUrl = taskSnapshot.getDownloadUrl();
                            profile = new userProfile(name, getCheckGender(), status, profileUrl.toString());
                            userDbRef.child(user_id).setValue(profile);

                            dialog.dismiss();

                            if (!variable.checkMenu){
                                variable.checkMenu = true;
                                Intent intentIChat = new Intent(ProfileActivity.this, IChatActivity.class);
                                intentIChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentIChat);
                                finish();
                            }else {
                                Toast.makeText(ProfileActivity.this, "ບັນທຶກຂໍ້ມູນໃຫມ່ສຳເລັດແລ້ວ", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                else {

                    profile = new userProfile(name, getCheckGender(), status, profileUrl);
                    userDbRef.child(user_id).setValue(profile);

                    dialog.dismiss();

                    if (!variable.checkMenu){
                        variable.checkMenu = true;
                        Intent intentIChat = new Intent(ProfileActivity.this, IChatActivity.class);
                        intentIChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentIChat);
                        finish();
                    }else {
                        Toast.makeText(ProfileActivity.this, "ບັນທຶກຂໍ້ມູນໃຫມ່ສຳເລັດແລ້ວ", Toast.LENGTH_LONG).show();
                    }
                }
            }else {
                Toast.makeText(ProfileActivity.this, "ຊື່ບູກຄົນຫ້າມວ່າງ", Toast.LENGTH_LONG).show();
            }
            }
        });

        String user_check = getIntent().getStringExtra("user_check");
        if(user_check.equals("exist")){
            getSupportActionBar().setTitle("Update profile");
            profileUrl = getIntent().getStringExtra("user_url");
            String uGender = getIntent().getStringExtra("user_gender");
            imgProfile.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (!profileUrl.equals("")){
                Glide.with(ProfileActivity.this)
                        .load(profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .error(R.drawable.ic_user_waiting)
                        .into(imgProfile);
            }else {
                if(uGender.equals("ຊາຍ"))
                    imgProfile.setImageResource(R.drawable.ic_user_man);
                else
                    imgProfile.setImageResource(R.drawable.ic_user_woman);
            }
            txtName.setText(getIntent().getStringExtra("user_name"));
            if (uGender.equals("ຊາຍ")) rdbMale.setChecked(true); else rdbFemale.setChecked(true);
            txtStatus.setText(getIntent().getStringExtra("user_status"));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!getInternetConnection()){
            return;
        }
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    if(!getInternetConnection()){
                        return;
                    }
                    userOnlineDbRef.child("online_now").setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!getInternetConnection()){
            return;
        }
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    if(!getInternetConnection()){
                        return;
                    }
                    userOnlineDbRef.child("online_now").setValue(false);
                    userOnlineDbRef.child("last_seen").setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            checkClickProfile = true;
            imgProfile.setScaleType(ImageView.ScaleType.FIT_CENTER);
            profileImageUri = data.getData();
            imgProfile.setImageURI(profileImageUri);
        }
    }

    private String getCheckGender(){
        if (rdbMale.isChecked()) return "ຊາຍ";
        return "ຍິງ";
    }

    private boolean getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean checkInternetConnection = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return checkInternetConnection;
    }

}
