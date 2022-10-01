package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;





public class PasswordManagerPage extends AppCompatActivity {

    FloatingActionButton mcreatenotesfab;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirebaseModel firebasemodel;


    FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder> noteAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager_page);


        firebasemodel = new FirebaseModel();
//        getSupportActionBar().setTitle("All Notes");

        mcreatenotesfab = findViewById(R.id.createnotesfab);

//        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordManagerPage.this, CreateNote.class));
            }
        });

        Query query = firebaseFirestore.collection("Notes").document(firebaseUser.getUid()).collection("mynotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirebaseModel> allusernotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();


        noteAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusernotes) {

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull FirebaseModel model) {

                ImageView popupbutton = holder.itemView.findViewById(R.id.menupopbutton);

                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(view.getContext(),NoteDetails.class);
                        intent.putExtra("title",firebasemodel.getTitle());
                        intent.putExtra("content",firebasemodel.getContent());
                        intent.putExtra("noteId",docId);

                        view.getContext().startActivity(intent);
//                        Toast.makeText(PasswordManagerPage.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                Intent intent = new Intent(view.getContext(),EditNoteActivity.class);
                                intent.putExtra("title",firebasemodel.getTitle());
                                intent.putExtra("content",firebasemodel.getContent());
                                intent.putExtra("noteId",docId);
                                view.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
//                                Toast.makeText(PasswordManagerPage.this, "Clicked", Toast.LENGTH_SHORT).show();
                                DocumentReference documentReference = firebaseFirestore.collection("Notes").document(firebaseUser.getUid()).collection("mynotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(PasswordManagerPage.this, "Note Deleted...", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PasswordManagerPage.this, "Failed!", Toast.LENGTH_SHORT).show();

                                    }
                                });
//                                Intent intent = new Intent(view.getContext(),EditNoteActivity.class);
//                                view.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        popupMenu.show();

                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recycler_view);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);
    }


    public static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView notetitle;
        TextView notecontent;
        LinearLayout mnotes;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnotes = itemView.findViewById(R.id.note);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(PasswordManagerPage.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }
}

