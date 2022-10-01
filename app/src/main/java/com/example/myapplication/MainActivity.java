package com.example.myapplication;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText mEmail, mPassword;
    private TextView forgotPassword, createPage;
    private FirebaseAuth fAuth;
    private ProgressBar loading;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.button);
        createPage = findViewById(R.id.textView3);
        forgotPassword = findViewById(R.id.textView2);
        mEmail = findViewById(R.id.editTextTextEmailAddress);
        mPassword = findViewById(R.id.editTextTextPassword);
        fAuth = FirebaseAuth.getInstance();
        loading = findViewById(R.id.progressBar2);

        createPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Create your Account here!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CreateAccountPage.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ForgotPasswordPage.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Get Password", Toast.LENGTH_SHORT).show();
            }
        });

       login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(isEmpty(email)){
                    mEmail.setError("Enter Email/Username");
                    flag = false;
                }
                if(isEmpty(password)){
                    mPassword.setError("Enter Password");
                    flag = false;
                }
                if (password.length() < 6){
                    mPassword.setError("Enter Password with length >= 6");
                    flag = false;
                }

                if(flag) {
                    loading.setVisibility(View.VISIBLE);
                }

                if(flag) {
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                firebaseFirestore = FirebaseFirestore.getInstance();
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                Query query = firebaseFirestore.collection("Notes").document(firebaseUser.getUid()).collection("mynotes").orderBy("title",Query.Direction.ASCENDING);
                                FirestoreRecyclerOptions<FirebaseModel> allusernotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();
                                checkMailVerification();
                            } else {
                                Toast.makeText(MainActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkMailVerification(){
        FirebaseUser firebaseUser = fAuth.getCurrentUser();

        assert firebaseUser != null;
        if(firebaseUser.isEmailVerified()){
            Toast.makeText(MainActivity.this, "You have successfully Logged In", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            finish();
            startActivity(new Intent(MainActivity.this, MenuPage.class));
            mEmail.setText("");
            mPassword.setText("");
        }
        else{
            Toast.makeText(this, "Verify your email first!", Toast.LENGTH_SHORT).show();
            fAuth.signOut();
        }
    }
}