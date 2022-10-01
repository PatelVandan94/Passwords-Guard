package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MenuPage extends AppCompatActivity {
    TextView getRandomPassword, managePassword;
    ProgressBar progressBar;
//    private ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        getRandomPassword = findViewById(R.id.textView);
        managePassword = findViewById(R.id.textView6);
        progressBar = findViewById(R.id.progressBar5);
//        logout = findViewById(R.id.imageView5);

        getRandomPassword.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MenuPage.this,GetYourPassword.class);
            startActivity(intent);
            Toast.makeText(MenuPage.this,"Get your Password",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
        managePassword.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(MenuPage.this, "Store Your Passwords Here", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MenuPage.this,PasswordManagerPage.class));
            progressBar.setVisibility(View.GONE);
        });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MenuPage.this, MainActivity.class));
        finish();
        Toast.makeText(MenuPage.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
    }
}