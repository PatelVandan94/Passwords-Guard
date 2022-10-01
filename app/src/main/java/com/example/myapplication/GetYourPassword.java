package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class GetYourPassword extends AppCompatActivity {

    private EditText length1,showPass;
    private Button click,copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_your_password);
        length1 = findViewById(R.id.editTextNumberSigned);
        showPass = findViewById(R.id.editTextTextPersonName6);
        click = findViewById(R.id.button4);
        copy = findViewById(R.id.button5);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String len = length1.getText().toString();
                int length = Integer.parseInt(len);
                String randomPassword = generateRandomPassword(length);
                showPass.setText(randomPassword);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("EditText",showPass.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(GetYourPassword.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";

        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}