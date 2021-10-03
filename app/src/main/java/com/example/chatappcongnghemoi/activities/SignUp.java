package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;

public class SignUp extends AppCompatActivity {

    EditText txtUsername;
    Button btnNext,btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        txtUsername = findViewById(R.id.txtUsernameSignUp);
        btnNext = findViewById(R.id.btnNextSignUp);
        btnLogin = findViewById(R.id.btnLogin_SignUp);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString().trim();
                if(username.matches("[^\\d\\W]{2,40}")) {
                    Intent intent = new Intent(SignUp.this,SignUp_SDT.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(SignUp.this, "Tên người dùng từ 2-40 ký tự, không chứa ký tự số và ký tự đặc biệt", Toast.LENGTH_SHORT).show();
            }
        });
    }
}