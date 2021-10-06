package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;

import com.example.chatappcongnghemoi.R;

public class ConfirmPassword extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        getSupportActionBar().hide();
    }

}