package com.example.myapplication;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class ForgotPasswordPage extends AppCompatActivity {
    private Button getYourPassword;
    private TextView showPassword, mEmail, mSensitiveInfo;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        getYourPassword = findViewById(R.id.button3);
        showPassword = findViewById(R.id.textView5);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        mEmail = findViewById(R.id.editTextTextPersonName3);
        mSensitiveInfo = findViewById(R.id.editTextTextPersonName4);

//        boolean ans = fStore.getApp().get()

        getYourPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String SensitiveInformation = mSensitiveInfo.getText().toString();
//                fAuth.getCurrentUser().getUid().co
                if(email.isEmpty()){
                    Toast.makeText(ForgotPasswordPage.this, "Enter MailID", Toast.LENGTH_SHORT).show();
                }
                else {
                    DocumentReference docRef = fStore.collection("Users").document(email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    showPassword.setText((CharSequence) document.getData());
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }





//                else{
//                    fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(ForgotPasswordPage.this, "Password sent to entered Email.", Toast.LENGTH_SHORT).show();
//                                finish();
//                                startActivity(new Intent(ForgotPasswordPage.this, MainActivity.class));
//                            }
//                            else{
//                                Toast.makeText(ForgotPasswordPage.this, "Account No Exist!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }

            }
        });
    }
}