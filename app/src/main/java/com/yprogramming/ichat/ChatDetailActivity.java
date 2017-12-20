package com.yprogramming.ichat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.bassaer.chatmessageview.model.IChatUser;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.util.ITimeFormatter;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailActivity extends AppCompatActivity {


    private DatabaseReference getUserOnline, relationshipDbRef, userOnlineDbRef, getMessages, sendMessage;
    private FirebaseAuth userAuth;

    private Toolbar ichChatDetailToolBar;
    private CircleImageView imgChatProfile, imgOnline;
    private ChatView mChatView;
    private IChatUser me, you;

    private String friend_id = "";
    private String currentUserId = "";
    private boolean relationship = false;

    private Bitmap myIcon, yourIcon;
    private Map<String, vMessage> msgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        ichChatDetailToolBar = (Toolbar)findViewById(R.id.ichChatDetailToolBar);
        setSupportActionBar(ichChatDetailToolBar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("friend_name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friend_id = getIntent().getStringExtra("friend_id");

        imgChatProfile = (CircleImageView)findViewById(R.id.imgChatProfile);
        imgOnline = (CircleImageView)findViewById(R.id.imgOnline);
        mChatView = (ChatView) findViewById(R.id.mChatView);
        mChatView.setAutoScroll(true);
        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray500));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900));
        mChatView.setOptionButtonColor(ContextCompat.getColor(this, R.color.cyan900));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("ພິມຂໍ້ຄວາມ...");
        mChatView.setMessageMarginTop(2);
        mChatView.setMessageMarginBottom(3);


        final String profile_url = getIntent().getStringExtra("friend_url");
        final String gender = getIntent().getStringExtra("friend_gender");

        if(!profile_url.equals("")){
            Glide.with(ChatDetailActivity.this)
                    .load(profile_url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imgChatProfile);
        }else {
            if(gender.equals("ຊາຍ"))
                imgChatProfile.setImageResource(R.drawable.ic_user_man);
            else
                imgChatProfile.setImageResource(R.drawable.ic_user_woman);
        }

        userAuth = FirebaseAuth.getInstance();
        currentUserId = userAuth.getCurrentUser().getUid();
        userOnlineDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("online").child(currentUserId);
        getMessages = FirebaseDatabase.getInstance().getReference().child("iChat").child("messages");
        getMessages.keepSynced(true);
        getUserOnline = FirebaseDatabase.getInstance().getReference().child("iChat").child("online").child(friend_id);
        getUserOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    boolean checkOnline = (boolean) dataSnapshot.child("online_now").getValue();
                    if (checkOnline){
                        imgOnline.setVisibility(View.VISIBLE);
                        getSupportActionBar().setSubtitle("Online");
                    }else {
                        Long last_seen = (Long) dataSnapshot.child("last_seen").getValue();
                        imgOnline.setVisibility(View.INVISIBLE);
                        getSupportActionBar().setSubtitle(TimeAgo.getTimeAgo(last_seen, getApplicationContext()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        relationshipDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("chats");
        relationshipDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUserId)){
                    if (dataSnapshot.child(currentUserId).hasChild(friend_id)){
                        relationship = true;
                        getMessages.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                msgs = new HashMap<String, vMessage>();
                                mChatView.getMessageView().getMessageList().clear();
                                if (dataSnapshot.hasChild(currentUserId + friend_id)){
                                    sendMessage = FirebaseDatabase.getInstance().getReference().child("iChat").child("messages").child(currentUserId + friend_id);
                                    for (DataSnapshot m: dataSnapshot.child(currentUserId + friend_id).getChildren()) {
                                        msgs.put(m.getKey(), m.getValue(vMessage.class));
                                        setMessages(m.getKey(), m.getValue(vMessage.class));
                                    }
                                }else if (dataSnapshot.hasChild(friend_id + currentUserId)){
                                    sendMessage = FirebaseDatabase.getInstance().getReference().child("iChat").child("messages").child(friend_id+currentUserId);
                                    for (DataSnapshot m: dataSnapshot.child(friend_id + currentUserId).getChildren()) {
                                        msgs.put(m.getKey(), m.getValue(vMessage.class));
                                        setMessages(m.getKey(), m.getValue(vMessage.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        relationship = false;
                    }
                }else {
                    relationship = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            if(!profile_url.equals("")){
                URL url = new URL(profile_url);
                yourIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }else {
                if (gender.equals("ຊາຍ")){
                    yourIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_man);
                }else {
                    yourIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_woman);
                }
            }
        }catch (Exception e){
            if (gender.equals("ຊາຍ")){
                yourIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_man);
            }else {
                yourIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_woman);
            }
        }

        try {
            if (!variable.userProfileUrl.equals("")){
                URL url = new URL(variable.userProfileUrl);
                myIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }else {
                if (variable.user_gender.equals("ຊາຍ")){
                    myIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_man);
                }else {
                    myIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_woman);
                }
            }
        }catch (Exception e){
            if (variable.user_gender.equals("ຊາຍ")){
                myIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_man);
            }else {
                myIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_user_woman);
            }
        }

        me = new IChatUser() {
            @NotNull
            @Override
            public String getId() {
                return "me";
            }

            @Nullable
            @Override
            public String getName() {
                return "Me";
            }

            @Nullable
            @Override
            public Bitmap getIcon() {
                return myIcon;
            }

            @Override
            public void setIcon(@NotNull Bitmap bitmap) {

            }
        };

        you = new IChatUser() {
            @NotNull
            @Override
            public String getId() {
                return "You";
            }

            @Nullable
            @Override
            public String getName() {
                return "You";
            }

            @Nullable
            @Override
            public Bitmap getIcon() {
                return yourIcon;
            }

            @Override
            public void setIcon(@NotNull Bitmap bitmap) {

            }
        };

        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputMessage = mChatView.getInputText();
                if (TextUtils.isEmpty(inputMessage)) return;
                String mDate = DateFormat.format("yyyy-MM-dd", new Date()).toString();
                String mTime = DateFormat.format("hh:mm", new Date()).toString();
                message msg = new message(inputMessage, "text", mDate, mTime, ServerValue.TIMESTAMP, false, currentUserId);
                if (relationship){
                    sendMessage.push().setValue(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            relationshipDbRef.child(currentUserId).child(friend_id).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                            relationshipDbRef.child(currentUserId).child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("timeStamp")){
                                        Long timest = (Long) dataSnapshot.child("timeStamp").getValue();
                                        relationshipDbRef.child(currentUserId).child(friend_id).child("timeStamp").setValue(-timest);
                                        relationshipDbRef.child(friend_id).child(currentUserId).child("timeStamp").setValue(-timest);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }else {
                    String reDate = DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date()).toString();
                    sendMessage = FirebaseDatabase.getInstance().getReference().child("iChat").child("messages").child(currentUserId + friend_id);
                    relationshipDbRef.child(currentUserId).child(friend_id).child("relationDate").setValue(reDate);
                    relationshipDbRef.child(friend_id).child(currentUserId).child("relationDate").setValue(reDate);
                    sendMessage.push().setValue(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            relationshipDbRef.child(currentUserId).child(friend_id).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                            relationshipDbRef.child(currentUserId).child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("timeStamp")){
                                        Long timest = (Long) dataSnapshot.child("timeStamp").getValue();
                                        relationshipDbRef.child(currentUserId).child(friend_id).child("timeStamp").setValue(-timest);
                                        relationshipDbRef.child(friend_id).child(currentUserId).child("timeStamp").setValue(-timest);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
                mChatView.setInputText("");
            }
        });

        mChatView.setOnClickOptionButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatDetailActivity.this, "New message option will be added soon", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        variable.checkMenu = true;
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

    private void setMessages(String key, final vMessage msg){
        Message newMessage;
        if (msg.getSender().equals(currentUserId)){
            newMessage = new Message.Builder()
                    .setUser(me)
                    .setSendTimeFormatter(new ITimeFormatter() {
                        @NotNull
                        @Override
                        public String getFormattedTimeText(@NotNull Calendar calendar) {
                            return msg.getmTime();
                        }
                    })
                    .setRightMessage(true)
                    .setMessageText(msg.getmText())
                    .build();
            mChatView.send(newMessage);
        }else {
            newMessage = new Message.Builder()
                    .setUser(you)
                    .setSendTimeFormatter(new ITimeFormatter() {
                        @NotNull
                        @Override
                        public String getFormattedTimeText(@NotNull Calendar calendar) {
                            return msg.getmTime();
                        }
                    })
                    .setRightMessage(false)
                    .setMessageText(msg.getmText())
                    .build();
            mChatView.receive(newMessage);
        }
    }

    private boolean getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean checkInternetConnection = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return checkInternetConnection;
    }

}
