package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateNote extends AppCompatActivity {


    EditText mcreatetitleofnote, mcreatecontentofnote;
    FloatingActionButton msavenote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);


        msavenote = findViewById(R.id.savenote);
        mcreatecontentofnote = findViewById(R.id.createcontentofnote);
        mcreatetitleofnote = findViewById(R.id.createtitleofnote);

        Toolbar toolbar = findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        msavenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mcreatetitleofnote.getText().toString();
                String content = mcreatecontentofnote.getText().toString();
                if(title.isEmpty() || content.isEmpty()){
                    Toast.makeText(CreateNote.this, "Both field are Required", Toast.LENGTH_SHORT).show();
                }
                else{
                    DocumentReference documentReference = firebaseFirestore.collection("Notes").document(firebaseUser.getUid()).collection("mynotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("content", content);
                    note.put("title", title);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateNote.this, "Note created successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateNote.this, PasswordManagerPage.class);
                            FirebaseModel firebaseModel = new FirebaseModel(title,content);
                            firebaseModel.setTitle(title);
                            firebaseModel.setContent(content);
//                            intent.putExtra("title", title);
//                            intent.putExtra("content", content);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateNote.this, "Failed to Create Note!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//    }

}