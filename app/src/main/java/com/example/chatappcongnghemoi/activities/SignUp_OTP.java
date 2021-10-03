package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;

public class SignUp_OTP extends AppCompatActivity {
    ImageView imgBack;
    Button btnConfirm;
    EditText txtOTP;
    TextView tvResendOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_otp);
        getSupportActionBar().hide();
        imgBack = findViewById(R.id.imgBackSignUpOTP);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtOTP = findViewById(R.id.txtOTP_SignUp);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp_OTP.this,SignUp_SDT.class);
                startActivity(intent);
                finish();
            }
        });
    }
}