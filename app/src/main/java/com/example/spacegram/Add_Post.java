package com.example.spacegram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spacegram.model.Posts;
import com.example.spacegram.util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class Add_Post extends Fragment {
    private static final int GALLERY_CODE = 1;
    private Button postButton;
    private EditText caption;
    private ProgressBar progressBar;
    private String currentDate = DateFormat.getDateTimeInstance().format(new Date());
    private ImageView postImage;
    private static String uniqueID = null;

    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private Toolbar toolbars;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Posts");
    private Uri imageUri;

    public Add_Post(){
        //Required Empty constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__post, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        toolbars = view.findViewById(R.id.toolbaradd);

        postButton = view.findViewById(R.id.postbutton);
        caption = view.findViewById(R.id.caption);
        progressBar = view.findViewById(R.id.progressBar);
        postImage = view.findViewById(R.id.postImage);

        postImage.setOnClickListener(this::onClick);
        postButton.setOnClickListener(this::onClick);
        progressBar.setVisibility(View.INVISIBLE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbars);


        if(UserApi.getInstance()!=null){
            currentUserId = UserApi.getInstance().getUserid();
            currentUserName = UserApi.getInstance().getUsername();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!=null){

                }else{

                }
            }
        };

        // initialise your views

    }


    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postbutton:
                savePost();

                break;
            case R.id.postImage:
                CropImage.activity().start(getContext(),this);

                break;
        }

    }
    private void savePost() {
        uniqueID = UUID.randomUUID().toString();

        final String captions = caption.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(captions) && imageUri!=null){
            final StorageReference filepath = storageReference
                    .child("user_posts")
                    .child("post_image" + Timestamp.now().getSeconds());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);


                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Posts posts = new Posts();
                            posts.setCaption(captions);
                            posts.setImageUrl(imageUrl);
                            posts.setTime(currentDate);
                            posts.setUid(uniqueID);
                            posts.setUserId(currentUserId);
                            posts.setUsername(currentUserName);

                            collectionReference.add(posts).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext().getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext().getApplicationContext(), "Upload Error", Toast.LENGTH_SHORT).show();


                                }
                            });

                        }
                    });






                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);


                }
            });

        }else{
            Toast.makeText(getContext().getApplicationContext(), "Please Select Image and Caption", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                assert result != null;
                imageUri = result.getUri();
                postImage.setImageURI(imageUri);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

}