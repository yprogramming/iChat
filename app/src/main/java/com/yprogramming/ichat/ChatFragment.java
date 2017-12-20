package com.yprogramming.ichat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private View mView;
    private RecyclerView rcvChats;

    Query chatDbRef;
    private FirebaseAuth userAuth;;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);

        userAuth = FirebaseAuth.getInstance();
        String curUser = userAuth.getCurrentUser().getUid();
        chatDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("chats").child(curUser);

        rcvChats = (RecyclerView) mView.findViewById(R.id.rcvfChats);
        rcvChats.setHasFixedSize(true);
        rcvChats.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter <chat, ChatViewHolder> chatFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chat, ChatViewHolder>(
                chat.class,
                R.layout.chat_row,
                ChatViewHolder.class,
                chatDbRef.orderByChild("timeStamp")
        ) {
            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder, chat model, int position) {

                final String key = getRef(position).getKey();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users").child(key);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userProfile profile = dataSnapshot.getValue(userProfile.class);
                        viewHolder.setProfileUrl(getContext(), profile.getProfileUrl(), profile.getGender());
                        viewHolder.setFullname(profile.getFullname());
                        viewHolder.setStatus(profile.getStatus());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.cyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users").child(key);
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userProfile profile = dataSnapshot.getValue(userProfile.class);
                                Intent chatDetailIntent = new Intent(getActivity(), ChatDetailActivity.class);
                                chatDetailIntent.putExtra("friend_id", key);
                                chatDetailIntent.putExtra("friend_name", profile.getFullname());
                                chatDetailIntent.putExtra("friend_url", profile.getProfileUrl());
                                chatDetailIntent.putExtra("friend_gender", profile.getGender());
                                startActivity(chatDetailIntent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        rcvChats.setAdapter(chatFirebaseRecyclerAdapter);

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        private View cyclerView;
        public ChatViewHolder(View itemView) {
            super(itemView);
            cyclerView = itemView;
        }

        public void setProfileUrl(Context ctx, String uProfile, String gender) {
            CircleImageView imgUserProfile = (CircleImageView)cyclerView.findViewById(R.id.imgChProfile);
            if(!uProfile.equals("")){
                Glide.with(ctx)
                        .load(uProfile)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .error(R.drawable.ic_user_waiting)
                        .into(imgUserProfile);
            }else {
                if(gender.equals("ຊາຍ"))
                    imgUserProfile.setImageResource(R.drawable.ic_user_man);
                else
                    imgUserProfile.setImageResource(R.drawable.ic_user_woman);
            }
        }

        public void setFullname(String fullname) {
            TextView lblAuthor = (TextView) cyclerView.findViewById(R.id.lblChName);
            lblAuthor.setText(fullname);
        }

        public void setStatus(String status) {
            TextView lblStatus = (TextView) cyclerView.findViewById(R.id.lblChStatus);
            lblStatus.setText(status);
        }

    }

}
