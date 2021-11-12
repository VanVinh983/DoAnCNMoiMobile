package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;

public class Full_Image_Avatar extends AppCompatActivity {
    private ImageView image_avatar;
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_avatar);
        getSupportActionBar().hide();

        image_avatar = findViewById(R.id.avatar_fullscreen);
        btn_back = findViewById(R.id.btn_fullscreen_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        System.out.println("tenn anh: "+ url);
        Glide.with(this).load(url).into(image_avatar);
    }
}