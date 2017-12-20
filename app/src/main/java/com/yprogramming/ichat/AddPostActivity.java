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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.util.Date;

public class AddPostActivity extends AppCompatActivity {

    private Toolbar ichAddPostToolBar;
    private ImageButton imgPost;
    private EditText txtTitle;
    private EditText txtDetail;
    private Button btnSavePost;

    private static final int GALLERY_REQUEST = 1;
    private Uri postImageUri = null;

    //Firebase object
    private DatabaseReference postDbRef;
    private DatabaseReference userOnlineDbRef;
    private StorageReference storageRef;
    private FirebaseAuth userAuth;


    private ProgressDialog dialog;
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Set new toolbar to current activity
        ichAddPostToolBar = (Toolbar)findViewById(R.id.ichAddPostToolBar);
        setSupportActionBar(ichAddPostToolBar);
        getSupportActionBar().setTitle("New");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set check menu equal to true to don't make reload dialog while back to main activity
        variable.checkMenu = true;

        //set firebase reference
        userAuth = FirebaseAuth.getInstance();
        userKey = userAuth.getCurrentUser().getUid();
        postDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("posts");
        userOnlineDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("online").child(userKey);

        storageRef = FirebaseStorage.getInstance().getReference().child("iChat");

        dialog = new ProgressDialog(this);
        dialog.setTitle("iChat");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        //set java activity object to xml view activity
        imgPost = (ImageButton) findViewById(R.id.imgPost);
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtDetail = (EditText) findViewById(R.id.txtDetail);
        btnSavePost = (Button) findViewById(R.id.btnSavePost);

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                profilePictureIntent.setType("image/*");
                startActivityForResult(profilePictureIntent, GALLERY_REQUEST);
            }
        });
        btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getInternetConnection()){
                    Toast.makeText(AddPostActivity.this, "ຂາດການເຊື່ອມຕໍ່ອິນເຕເນັດ", Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.setMessage("ກຳລັງບັນທືກຂໍ້ມູນ...");
                Date d = new Date();
                final String currentDMY = DateFormat.format("dd/MM/yyyy", d).toString();
                final String currentTime = DateFormat.format("hh:mm:ss", d).toString();
                final String title = txtTitle.getText().toString();
                final String detail = txtDetail.getText().toString();
                final boolean check = !TextUtils.isEmpty(title) && !TextUtils.isEmpty(detail) && (postImageUri != null);

                if (check){
                    dialog.show();
                    StorageReference postImageSave = storageRef.child("posts").child(variable.randomProfileName());
                    postImageSave.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            postImageUri = null;
                            Uri postUrl = taskSnapshot.getDownloadUrl();
                            post newPost = new post(postUrl.toString(), title, detail, currentDMY, currentTime, userKey, ServerValue.TIMESTAMP);
                            postDbRef.push().setValue(newPost);
                            dialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "ເພີ່ມການບັນເທີງສຳເລັດແລ້ວ", Toast.LENGTH_LONG).show();
                            imgPost.setScaleType(ImageView.ScaleType.CENTER);
                            imgPost.setImageResource(R.drawable.ic_camera);
                            txtTitle.setText("");
                            txtTitle.setFocusable(true);
                            txtDetail.setText("");
                        }
                    });
                }else{
                    Toast.makeText(AddPostActivity.this, "ຂໍ້ມູນບໍ່ຄົບຖ້ວນ", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imgPost.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postImageUri = data.getData();
            imgPost.setImageURI(postImageUri);
        }
    }


    private boolean getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean checkInternetConnection = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return checkInternetConnection;
    }
}
