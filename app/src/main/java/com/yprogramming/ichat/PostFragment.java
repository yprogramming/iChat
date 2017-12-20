package com.yprogramming.ichat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private View mView;
    private RecyclerView rcvPosts;
    private FloatingActionButton btnfNew;

    DatabaseReference postDbRef;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_post, container, false);

        postDbRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("posts");
        postDbRef.keepSynced(true);

        rcvPosts = (RecyclerView)mView.findViewById(R.id.rcvPosts);
        rcvPosts.setHasFixedSize(true);
        rcvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        btnfNew = (FloatingActionButton)mView.findViewById(R.id.btnfNew);
        btnfNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(addIntent);
            }
        });

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<vPost, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<vPost, PostViewHolder>(
                vPost.class,
                R.layout.post_row,
                PostViewHolder.class,
                postDbRef
        ){
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final vPost model, int position) {

                viewHolder.setImgUrl(getContext(),model.getImgUrl());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDetail(model.getDetail());
                viewHolder.setCreate_date(model.getCreate_date() + " " + model.getCreate_time());

                String author_id = model.getAuthor_id();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("iChat").child("users").child(author_id);
                userRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userProfile userData = dataSnapshot.getValue(userProfile.class);
                        viewHolder.setuProfile(getContext(),userData.getProfileUrl());
                        viewHolder.setAuthor(userData.getFullname());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        rcvPosts.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private View cyclerView;
        public PostViewHolder(View itemView) {
            super(itemView);
            cyclerView = itemView;

        }

        public void setuProfile(Context ctx, String uProfile) {
            CircleImageView imgUserProfile = (CircleImageView)cyclerView.findViewById(R.id.imgpUserProfile);
            if(!uProfile.equals("")) {
                Glide.with(ctx)
                        .load(uProfile)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .error(R.drawable.ic_user_waiting)
                        .into(imgUserProfile);
            }else {
                imgUserProfile.setImageResource(R.drawable.ic_user);
            }
        }

        public void setAuthor(String author) {
            TextView lblAuthor = (TextView) cyclerView.findViewById(R.id.lblpAuthor);
            lblAuthor.setText(author);
        }

        public void setImgUrl(Context ctx, String imgUrl) {
            ImageView imgPostPicture = (ImageView)cyclerView.findViewById(R.id.imgPostPicture);
            Glide.with(ctx)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imgPostPicture);
        }

        public void setTitle(String title) {
            TextView lblTitle = (TextView) cyclerView.findViewById(R.id.lblpTitle);
            lblTitle.setText(title);
        }

        public void setDetail(String detail) {
            TextView lblDetail = (TextView) cyclerView.findViewById(R.id.lblpDetail);
            lblDetail.setText(detail);
        }

        public void setCreate_date(String create_date) {
            TextView lblDate = (TextView) cyclerView.findViewById(R.id.lblpDate);
            lblDate.setText(create_date);
        }

    }
}
