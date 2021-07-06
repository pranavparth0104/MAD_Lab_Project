package com.example.spacegram.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacegram.R;
import com.example.spacegram.model.Dp;
import com.example.spacegram.model.Posts;
import com.example.spacegram.util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private List<Posts> postsList;
    private Boolean likedchecker = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private CollectionReference dps =  db.collection("Dp");

    FirebaseDatabase databases = FirebaseDatabase.getInstance();
    DatabaseReference likesreference = databases.getReference("likes");
    private String usersname = UserApi.getInstance().getUsername();
    private String userId = UserApi.getInstance().getUserid();

    public ProfileAdapter(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.post_prof_view, parent,false);
        return new ProfileAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Posts posts = postsList.get(position);
        String url;
        final String postkey;

        postkey = posts.getUid();
        String uid = posts.getUserId();


        holder.username.setText(posts.getUsername());
        holder.caption.setText(posts.getUsername()+' '+':'+' '+posts.getCaption());
        url = posts.getImageUrl();
        holder.date.setText(posts.getTime());

        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_loading)
                .into(holder.postImage);

        //String timeAgo = String.valueOf(posts.getTimeadded());

        // holder.date.setText(timeAgo);
        holder.dp(uid);

        holder.setLikes(postkey);

        holder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likedchecker = true;
                holder.setLikes(postkey);

                likesreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(likedchecker.equals(true)){
                            if(dataSnapshot.child(String.valueOf(postkey)).hasChild(userId)){
                                likesreference.child(String.valueOf(postkey)).child(userId).removeValue();
                                likedchecker = false;
                            }else
                            {
                                likesreference.child(String.valueOf(postkey)).child(userId).setValue(true);
                                likedchecker = false;

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username,  caption, date;
        public ImageView postImage;
        String userId;
        String usersname;
        ImageButton likebutton;
        TextView liketext;
        int likescount;
        public AvatarView avatarView;
        public String urls;
        public List<Dp> dpList;




        public DatabaseReference likesref;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            username = itemView.findViewById(R.id.profName);
            date = itemView.findViewById(R.id.profTime);
            caption = itemView.findViewById(R.id.profCaption);
            avatarView = itemView.findViewById(R.id.imagePro);


            postImage = itemView.findViewById(R.id.profImage);
            usersname = UserApi.getInstance().getUsername();
            userId = UserApi.getInstance().getUserid();
            likebutton = itemView.findViewById(R.id.like_Buttonp);
            liketext = itemView.findViewById(R.id.likes_textviewp);
            dpList = new ArrayList<>();



        }

        public void dp(String uid){
            dps
                    .whereEqualTo("userId",uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()){
                                for(QueryDocumentSnapshot dpss : queryDocumentSnapshots){
                                    Dp dp = dpss.toObject(Dp.class);
                                    dpList.add(dp);
                                    urls = dp.getImageUrl();
                                    Picasso.get()
                                            .load(urls)
                                            .placeholder(R.drawable.ic_loading)
                                            .into(avatarView);

                                    //  Toast.makeText(context, urls, Toast.LENGTH_SHORT).show();


                                }





                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Display Error", Toast.LENGTH_SHORT).show();

                        }
                    });

        }


        public void setLikes(final String postkey) {
            likesref = FirebaseDatabase.getInstance().getReference("likes");
            final String likes = "likes";

            likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(postkey).hasChild(userId)){
                        likescount=(int)dataSnapshot.child(postkey).getChildrenCount();
                        likebutton.setImageResource(R.drawable.ic_baseline_favorite_24);
                        liketext.setText( " " +Integer.toString(likescount)+ " " + likes);

                    }else{
                        likescount=(int)dataSnapshot.child(postkey).getChildrenCount();
                        likebutton.setImageResource(R.drawable.ic_disliked);
                        liketext.setText(" " +Integer.toString(likescount)+ " " + likes);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
