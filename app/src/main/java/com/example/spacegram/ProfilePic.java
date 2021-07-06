package com.example.spacegram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spacegram.model.Dp;
import com.example.spacegram.util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class ProfilePic extends AppCompatActivity implements View.OnClickListener{
    private static final int GALLERY_CODE = 1;
    private Button postButton;
    private ProgressBar progressBar;
    private ImageView postImage;
    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Dp");
    private Uri imageUri;

    public ProfilePic() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        postButton = findViewById(R.id.postprofbutton);
        progressBar = findViewById(R.id.progressBar2);
        postImage = findViewById(R.id.postprofImage);

        postImage.setOnClickListener(this);
        postButton.setOnClickListener(this);
        progressBar.setVisibility(View.INVISIBLE);

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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postprofbutton:
                saveProfilePhoto();

                break;
            case R.id.postprofImage:
                CropImage.activity().start(ProfilePic.this);

                break;
        }

    }

    private void saveProfilePhoto() {

        progressBar.setVisibility(View.VISIBLE);
        if(imageUri!=null){
            final StorageReference filepath = storageReference
                    .child("user_dp")
                    .child(currentUserId + Timestamp.now().getSeconds());




            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            collectionReference

                                    .whereEqualTo("userId",currentUserId)
                                    .get()

                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            if(!queryDocumentSnapshots.isEmpty()) {
                                                for(QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                                    collectionReference.document(document.getId()).delete();


                                                    String imageUrl = uri.toString();
                                                    Dp dp = new Dp();
                                                    dp.setImageUrl(imageUrl);
                                                    dp.setUserId(currentUserId);
                                                    dp.setUsername(currentUserName);



                                                    collectionReference.add(dp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {


                                                            Toast.makeText(ProfilePic.this, "Profile Picture Upload Successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent();
                                                            setResult(2,intent);
                                                            finish();

                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });





                                                }



                                            }else {
                                                String imageUrl = uri.toString();
                                                Dp dp = new Dp();
                                                dp.setImageUrl(imageUrl);
                                                dp.setUserId(currentUserId);
                                                dp.setUsername(currentUserName);



                                                collectionReference.add(dp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {


                                                        Toast.makeText(ProfilePic.this, "Profile Picture Upload Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent();
                                                        setResult(2,intent);
                                                        finish();

                                                    }
                                                })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfilePic.this, "Error Upload", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilePic.this, "Error Upload", Toast.LENGTH_SHORT).show();


                }
            });


        }else{
            Toast.makeText(this, "Select Photo", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}