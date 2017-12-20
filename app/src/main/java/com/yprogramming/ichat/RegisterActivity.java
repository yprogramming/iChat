package com.yprogramming.ichat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class RegisterActivity extends AppCompatActivity {

    private Toolbar ichRegisterChatToolBar;
    private Button btnRegister, btnVerify, btnNoCode;
    private EditText txtRegister, txtCode;
    private TextView lblTitle;
    private ProgressDialog uprogressDialog;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks registerVerify;
    private PhoneAuthProvider.ForceResendingToken userRegisterToken;

    private String phoneVerifyID;
    private String phoneNumber;


    //user result variable
    private String user_id, user_name, user_url, user_gender, user_status, view_type;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ichRegisterChatToolBar = (Toolbar)findViewById(R.id.ichRegisterChatToolBar);
        setSupportActionBar(ichRegisterChatToolBar);
        getSupportActionBar().setTitle("Register");

        variable.checkProfile = false;
        uprogressDialog = new ProgressDialog(this);
        uprogressDialog.setTitle("iChat");
        uprogressDialog.setCancelable(false);
        uprogressDialog.setInverseBackgroundForced(false);

        //Set button and view to know each other
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        txtRegister = (EditText) findViewById(R.id.txtPhoneNumber);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtCode = (EditText) findViewById(R.id.txtCode);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnNoCode = (Button) findViewById(R.id.btnNoCode);

        //Set up firebase auth root reference
        firebaseAuth = FirebaseAuth.getInstance();

        //Set up verify result
        registerVerify = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                txtCode.setText(phoneAuthCredential.getSmsCode());
                uprogressDialog.dismiss();

                //if (!TextUtils.isEmpty(phoneAuthCredential.getSmsCode()))
                signInWithPhoneNumber(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                uprogressDialog.dismiss();
                lblTitle.setText("ຢຶນຢັນການສະຫມັກໃຊ້ງານ");
                txtCode.setVisibility(View.INVISIBLE);
                btnVerify.setVisibility(View.INVISIBLE);
                btnNoCode.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.VISIBLE);
                txtRegister.setVisibility(View.VISIBLE);
                Toast.makeText(RegisterActivity.this, "ສະຫມັກລົ້ມເຫລວ ກະລຸນາກວດສອບໃຫມ່", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                userRegisterToken = forceResendingToken;
                phoneVerifyID = s;

                uprogressDialog.dismiss();
            }
        };

        //Set on click listener to btnRegister and make register logic
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uprogressDialog.setMessage("ກຳລັງສະຫມັກໃຊ້ງານ...");

                phoneNumber = txtRegister.getText().toString();
                if(validatorMobileNumber(phoneNumber)){
                    uprogressDialog.show();
                    if(getInternetConnection()){
                        btnRegister.setVisibility(View.INVISIBLE);
                        txtRegister.setVisibility(View.INVISIBLE);
                        lblTitle.setText("ຢຶນຢັນການສະຫມັກໃຊ້ແອັບ");
                        txtCode.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        btnNoCode.setVisibility(View.VISIBLE);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,
                                60,
                                TimeUnit.SECONDS,
                                RegisterActivity.this,
                                registerVerify
                        );
                    }else {
                        uprogressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "ຂາດການເຊື່ອມຕໍ່ອິນເຕີເນັດ", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = txtCode.getText().toString();
                if(!TextUtils.isEmpty(code) && !TextUtils.isEmpty(phoneVerifyID)){
                    PhoneAuthCredential verified = new PhoneAuthCredential(phoneVerifyID, code);
                    signInWithPhoneNumber(verified);
                }else {
                    Toast.makeText(RegisterActivity.this, "ຢືນຢັນລົ້ມເຫລວ", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnNoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lblTitle.setText("ສະຫມັກໃຊ້ແອັບດ້ວຍເບີໂທລະສັບ");
                txtCode.setVisibility(View.INVISIBLE);
                btnVerify.setVisibility(View.INVISIBLE);
                btnNoCode.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.VISIBLE);
                txtRegister.setVisibility(View.VISIBLE);
            }
        });
    }

    //Sign in logic
    private void signInWithPhoneNumber(PhoneAuthCredential phoneAuthCredential){
        if(getInternetConnection()){
            uprogressDialog.setMessage("ກຳລັງຢືນຢັນການໃຊ້ງານ...");
            uprogressDialog.show();
        }
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            uprogressDialog.dismiss();
            if(task.isSuccessful()){
                user_id = task.getResult().getUser().getUid();
                getUserResult(user_id);
            }else{
                Toast.makeText(RegisterActivity.this, "ຢືນຢັນການເຂົ້າສູ່ລະບົບລົ້ມເຫລວ", Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    private void getUserResult(final String userid){
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("iChat").child("users");
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userid)){
                    userProfile userData = dataSnapshot.child(userid).getValue(userProfile.class);
                    view_type = "exist";
                    user_name = userData.getFullname();
                    user_status = userData.getStatus();
                    user_url = userData.getProfileUrl();
                    user_gender = userData.getGender();
                }else {
                    view_type = "not_exist";
                    user_name = "";
                    user_status = "";
                    user_url = "";
                    user_gender = "";
                }

                Intent intentProfile = new Intent(RegisterActivity.this, ProfileActivity.class);
                intentProfile.putExtra("user_check", view_type);
                intentProfile.putExtra("user_id", user_id);
                intentProfile.putExtra("user_name", user_name);
                intentProfile.putExtra("user_status", user_status);
                intentProfile.putExtra("user_url", user_url);
                intentProfile.putExtra("user_gender", user_gender);
                startActivity(intentProfile);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                user_name = "";
                user_status = "";
                user_url = "";
                user_gender = "";
                Toast.makeText(RegisterActivity.this, "ເກີດຂໍ້ຜິດພາດໃນລະຫວ່າງກວດສອບຂໍ້ມູນ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean checkInternetConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

        if (checkInternetConnection) return true;
        return false;
    }

    private boolean validatorMobileNumber(String mNumber){
        boolean validate = !TextUtils.isEmpty(mNumber) && TextUtils.isDigitsOnly(mNumber) && mNumber.length() > 7;
        return validate;
    }
}
