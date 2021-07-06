package com.example.spacegram;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spacegram.model.Posts;
import com.example.spacegram.ui.RecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Home_Fragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private static String uniqueID = null;

    private List<Posts> postsList;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Toolbar toolbars;
    private DatabaseReference likesreference;
    private FirebaseDatabase database;
    private Boolean likedchecker = false;


    private CollectionReference collectionReference = (CollectionReference) db.collection("Posts");
    private TextView noPost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        toolbars =  view.findViewById(R.id.toolbar);


        noPost = view.findViewById(R.id.noPost);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));

        postsList = new ArrayList<>();


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbars);












    }



    @Override
    public void onStart() {
        super.onStart();
        //ek particular user ka lene ke liye hai collectionReference.whereEqualTo("userId", UserApi.getInstance().getUserid());

        collectionReference.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(QueryDocumentSnapshot posts : queryDocumentSnapshots){

                        Posts post = posts.toObject(Posts.class);
                        postsList.add(post);



                    }
                    recyclerAdapter = new RecyclerAdapter(getContext().getApplicationContext(),postsList);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();

                }else{
                    noPost.setVisibility(View.VISIBLE);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}