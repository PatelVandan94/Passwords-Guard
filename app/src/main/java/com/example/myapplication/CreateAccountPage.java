package com.example.myapplication;

import static android.service.controls.ControlsProviderService.TAG;
import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CreateAccountPage extends AppCompatActivity {

    private Button signup;
    private EditText mEmail, mUsername, mPassword, mSensitiveInformation, mRePassword;
    private FirebaseAuth fAuth;
    private ProgressBar loading;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_page);
        signup = findViewById(R.id.button2);
        mEmail = findViewById(R.id.editTextTextEmailAddress2);
        mUsername = findViewById(R.id.editTextTextPersonName);
        mPassword = findViewById(R.id.editTextTextPassword2);
        mRePassword = findViewById(R.id.editTextTextPassword3);
        mSensitiveInformation = findViewById(R.id.editTextTextPersonName2);
        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loading = findViewById(R.id.progressBar);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                String userName = mUsername.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String rePassword = mRePassword.getText().toString().trim();
                String sensitiveInformation = mSensitiveInformation.getText().toString().trim();

                if (isEmpty(email)) {
                    mEmail.setError("Enter Email");
                    flag = false;
                }
                if (isEmpty(userName)) {
                    mUsername.setError("Enter UserName");
                    flag = false;
                }
                if (isEmpty(password)) {
                    mPassword.setError("Enter Password");
                    flag = false;
                }
                if (isEmpty(rePassword)) {
                    mRePassword.setError("Enter Password in rePassword field");
                    flag = false;
                }
                if (isEmpty(sensitiveInformation)) {
                    mSensitiveInformation.setError("Enter Sensitive Information");
                    flag = false;
                }

                if (password.length() < 6) {
                    mPassword.setError("Enter Password with length >= 6");
                    flag = false;
                }
                if (rePassword.length() < 6) {
                    mRePassword.setError("Enter Password with length >= 6");
                    flag = false;
                }

                if (!password.equals(rePassword)) {
                    mRePassword.setError("Passwords doesn't match!");
                    flag = false;
                }

                if(flag) {
                    loading.setVisibility(View.VISIBLE);
                }

                if (flag) {
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String user_ID;
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateAccountPage.this, "Account Created", Toast.LENGTH_SHORT).show();
                                user_ID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
                                Map<String, Object> user = new HashMap<>();
                                user.put("UserID", user_ID);
                                user.put("Username", userName);
                                user.put("Email", email);
                                user.put("Password", password);
                                user.put("Sensitive Information", sensitiveInformation);
                                sendEmailVerification();
//                                user.put(email, sensitiveInformation);
//                                Map<Object,Object> verify_sinfo = new HashMap<>();
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @RequiresApi(api = Build.VERSION_CODES.R)
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "User Created Successfully" + user_ID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.R)
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Error" + e);
                                    }
                                });
                                startActivity(new Intent(CreateAccountPage.this, MainActivity.class));
                            } else {
                                Toast.makeText(CreateAccountPage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            loading.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = fAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(CreateAccountPage.this, "Verification Email is sent, verify it and login Again", Toast.LENGTH_SHORT).show();
                    fAuth.signOut();
                    finish();
                    startActivity(new Intent(CreateAccountPage.this, MainActivity.class));
                }
            });
        }
        else{
            Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
        }
    }
}