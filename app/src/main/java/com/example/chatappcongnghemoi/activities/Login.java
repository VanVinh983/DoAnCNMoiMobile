package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;

public class Login extends AppCompatActivity {
    ImageView imgBack;
    Button btnSignUp,btnLogin;
    TextView tvForgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        imgBack = findViewById(R.id.imgBackLogin);
        btnSignUp = findViewById(R.id.btnSignUpLogin);
        btnLogin  = findViewById(R.id.btnLogin_Login);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,StartApp.class);
                startActivity(intent);
                finish();
            }
        });
    }
}