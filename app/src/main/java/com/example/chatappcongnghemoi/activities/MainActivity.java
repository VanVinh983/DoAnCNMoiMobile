package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.chatappcongnghemoi.R;

public class MainActivity extends AppCompatActivity {
    Animation topAnim,bottomAnim;
    ImageView logo,slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        logo = findViewById(R.id.imgLogo);
        slogan = findViewById(R.id.slogan);
        logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}