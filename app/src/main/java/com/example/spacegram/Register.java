package com.example.spacegram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spacegram.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    private TextView clicktologin;
    private EditText username;
    private EditText email;
    private ProgressBar progressBar;
    private EditText password;
    private EditText repass;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Button register;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.preg);
        username = findViewById(R.id.edit_register_name);
        email = findViewById(R.id.edit_register_email);
        password = findViewById(R.id.edit_register_password);
        repass = findViewById(R.id.edit_register_repassword);
        register = findViewById(R.id.button_register);
        clicktologin = findViewById(R.id.clickToLogin);
        clicktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already loggedin..
                }else {
                    //no user yet...
                }

            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String user = username.getText().toString().trim();
                String emailid = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String repassword = repass.getText().toString().trim();

                if(TextUtils.isEmpty(user)){
                    username.setError("Name not Entered");
                    return;
                }


                if(TextUtils.isEmpty(emailid)){
                    email.setError("Email not Entered");
                    return;
                }
                if(!emailid.matches(emailPattern)){
                    email.setError("Invaild Email Address");
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    password.setError("Password not Entered");
                    return;
                }
                if(TextUtils.isEmpty(repassword)){
                    repass.setError("Please Re Enter Password");
                    return;
                }

                if(pass.length()<6){
                    password.setError("Password must be of more than 6 characters");
                    return;
                }
                if(!pass.equals(repassword)){
                    repass.setError("Enter Password Correctly");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailid,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);
                            currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();
                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("username", user);

                            collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (Objects.requireNonNull(task.getResult().exists())) {
                                                String name = task.getResult()
                                                        .getString("username");

                                                UserApi userApi = UserApi.getInstance();
                                                userApi.setUserid(currentUserId);
                                                userApi.setUsername(name);
                                                SharedPreferences preferences = getSharedPreferences("checkuser",MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("remember","true");
                                                editor.putString("uid",currentUserId);
                                                editor.putString("uname",user);
                                                editor.apply();

                                                Intent intent = new Intent(Register.this,Home.class);

                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(Register.this, "Register Successful", Toast.LENGTH_SHORT).show();

                                            }else{
                                                Toast.makeText(Register.this, "Register Error", Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, "Register Error", Toast.LENGTH_SHORT).show();

                                            //
                                        }
                                    });


                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //
                    }
                });






            }
        });

    }
}