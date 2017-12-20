package com.yprogramming.ichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class IChatActivity extends AppCompatActivity {

    private Toolbar ichMainChatToolBar;
    private ViewPager vpMainIChat;
    private TabPagerAdapter tabPagerAdapter;
    private TabLayout tabMainIChat;

    //User authentication
    private FirebaseAuth userAuth;
    private DatabaseReference userOnlineDbRef;

    //Post fragment view
    private RecyclerView rcvPosts;
    private FloatingActionButton btnfNew;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ichat);

        ichMainChatToolBar = (Toolbar)findViewById(R.id.ichMainChatToolBar);
        setSupportActionBar(ichMainChatToolBar);
        getSupportActionBar().setTitle("iChat");

        //Set tab view on top
        vpMainIChat = (ViewPager)findViewById(R.id.vpMainIChat) ;
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        vpMainIChat.setAdapter(tabPagerAdapter);

        tabMainIChat = (TabLayout) findViewById(R.id.tabMainIChat);
        tabMainIChat.setupWithViewPager(vpMainIChat);

        userAuth = FirebaseAuth.getInstance();
        if(userAuth.getCurrentUser() != null){

            userOnlineDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("online").child(userAuth.getCurrentUser().getUid());

        }
        if(!variable.checkMenu){
            Intent spreshIntent = new Intent(IChatActivity.this, SpreshScreenActivity.class);
            startActivity(spreshIntent);
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!getInternetConnection()){
            return;
        }
        if(userAuth.getCurrentUser() == null){
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
        if(userAuth.getCurrentUser() == null){
            return;
        }
        userOnlineDbRef.child("online_now").setValue(false);
        userOnlineDbRef.child("last_seen").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mnItemProfile){
            variable.checkProfile = false;
            variable.checkMenu = true;
            final String userKey = userAuth.getCurrentUser().getUid();
            Intent intentViewProfile = new Intent(IChatActivity.this, ViewProfileActivity.class);
            intentViewProfile.putExtra("view_id", userKey);
            intentViewProfile.putExtra("view_type", "uprofile");
            startActivity(intentViewProfile);
        }

        return true;
    }

    private boolean getInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean checkInternetConnection = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return checkInternetConnection;
    }

}
