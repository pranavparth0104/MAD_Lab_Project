package com.example.spacegram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    private TextView clicktoSignup;
    private EditText email;
    private EditText password;
    private String uid = "a";
    private String uname = "a";
    private Button login;
    private ProgressBar progressBar;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_password);
        progressBar = findViewById(R.id.plog);
        login = findViewById(R.id.button_login);
        clicktoSignup = findViewById(R.id.clickToRegister);
        clicktoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });
        SharedPreferences preferences = getSharedPreferences("checkuser",MODE_PRIVATE);
        String check = preferences.getString("remember","");
        uid = preferences.getString("uid","");
        uname = preferences.getString("uname","");
        if(check.equals("true")){

            UserApi userApi = UserApi.getInstance();
            userApi.setUserid(uid);
            userApi.setUsername(uname);

            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
            finish();

        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailid = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
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


                if(pass.length()<6){
                    password.setError("Password must be of more than 6 characters");
                    return;
                }

                mAuth.signInWithEmailAndPassword(emailid,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);

                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            final String currentUserId = user.getUid();
                            final String username = user.getDisplayName();
                            collectionReference
                                    .whereEqualTo("userId", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {


                                    }
                                    assert value != null;
                                    if (!value.isEmpty()) {
                                        for (QueryDocumentSnapshot snapshot : value) {
//                                            if((uid != null && uname != null) || (uid == snapshot.getString("userId") && uname == snapshot.getString("username"))){
//
//                                                startActivity(new Intent(Login.this,
//                                                        Home.class));
//                                                finish();
//                                                Toast.makeText(Login.this, "SP wala", Toast.LENGTH_SHORT).show();
//
//                                            }else{


                                            UserApi userApi = UserApi.getInstance();
                                            userApi.setUserid(snapshot.getString("userId"));
                                            userApi.setUsername(snapshot.getString("username"));
                                            SharedPreferences preferences = getSharedPreferences("checkuser",MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("remember","true");
                                            editor.putString("uid",snapshot.getString("userId"));
                                            editor.putString("uname",snapshot.getString("username"));


                                            editor.commit();

                                            startActivity(new Intent(Login.this,
                                                    Home.class));
                                            finish();
                                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        }

//                                        }
                                    }
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(Login.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();

                    }
                });






            }
        });
    }
}