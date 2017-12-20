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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {


    private View mView;
    private RecyclerView rcvFriends;

    private DatabaseReference friendDbRef;
    private FirebaseAuth userAuth;

    private String currentUserUrl = "";

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_friend, container, false);

        userAuth = FirebaseAuth.getInstance();
        friendDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users");
        friendDbRef.keepSynced(true);

        rcvFriends = (RecyclerView)mView.findViewById(R.id.rcvFriends);
        rcvFriends.setHasFixedSize(true);
        rcvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();


        String curUserKey = userAuth.getCurrentUser().getUid();
        friendDbRef.child(curUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile up = dataSnapshot.getValue(userProfile.class);
                currentUserUrl = up.getProfileUrl();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<userProfile, FriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<userProfile, FriendViewHolder>(
                userProfile.class,
                R.layout.friend_row,
                FriendViewHolder.class,
                friendDbRef
        ) {
            @Override
            protected void populateViewHolder(FriendViewHolder viewHolder, userProfile model, int position) {
                viewHolder.setProfileUrl(getContext(), model.getProfileUrl(), model.getGender(), currentUserUrl);
                viewHolder.setFullname(model.getFullname());

                final String key = getRef(position).getKey();
                viewHolder.cyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewProfileIntent = new Intent(getActivity(), ViewProfileActivity.class);
                        viewProfileIntent.putExtra("view_id", key);
                        viewProfileIntent.putExtra("view_type", "friend");
                        startActivity(viewProfileIntent);
                    }
                });
            }
        };
        rcvFriends.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{

        private View cyclerView;
        public FriendViewHolder(View itemView) {
            super(itemView);
            cyclerView = itemView;
        }

        public void setProfileUrl(Context ctx, String userProfile, String gender, String compareUrl) {
            CircleImageView imgUserProfile = (CircleImageView)cyclerView.findViewById(R.id.imgFrPro);
            if (userProfile.equals(compareUrl)){
                TextView lblAuthor = (TextView) cyclerView.findViewById(R.id.lblFrName);
                LinearLayout linlFriend = (LinearLayout)cyclerView.findViewById(R.id.linlFriend);
                LinearLayout.LayoutParams newMargin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newMargin.setMargins(0,0,0,0);
                imgUserProfile.setVisibility(View.GONE);
                lblAuthor.setVisibility(View.GONE);
                linlFriend.setLayoutParams(newMargin);
                cyclerView.setVisibility(View.GONE);
            }else {
                if(!userProfile.equals("")){
                    Glide.with(ctx)
                            .load(userProfile)
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
        }

        public void setFullname(String fullname) {
            TextView lblAuthor = (TextView) cyclerView.findViewById(R.id.lblFrName);
            lblAuthor.setText(fullname);

        }

    }

}
