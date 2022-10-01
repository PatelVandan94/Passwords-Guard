package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class EditNoteActivity extends AppCompatActivity {

    Intent data;
    EditText medittitleofnote, meditcontentofnote;
    FloatingActionButton msaveeditnote;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        medittitleofnote = findViewById(R.id.edittitleofnote);
        meditcontentofnote = findViewById(R.id.editcontentofnote);
        msaveeditnote = findViewById(R.id.saveeditnote);
        data = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        msaveeditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newtitle = medittitleofnote.getText().toString();
                String newcontent = meditcontentofnote.getText().toString();

                if(newtitle.isEmpty() || newcontent.isEmpty()){
                    Toast.makeText(EditNoteActivity.this, "Both field is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    DocumentReference documentReference = firebaseFirestore.collection("Notes").document(firebaseUser.getUid()).collection("mynotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title",newtitle);
                    note.put("content",newcontent);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditNoteActivity.this, "Note updated...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditNoteActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        String notetitle = data.getStringExtra("title");
        String notecontent = data.getStringExtra("content");
        meditcontentofnote.setText(notecontent);
        medittitleofnote.setText(notetitle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}