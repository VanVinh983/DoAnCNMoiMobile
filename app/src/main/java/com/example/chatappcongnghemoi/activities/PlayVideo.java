package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Message;

public class PlayVideo extends AppCompatActivity {
    String groupId,video;
    ImageView btnBack;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        getSupportActionBar().hide();
        btnBack = findViewById(R.id.imgBackPlayVideo);
        videoView = findViewById(R.id.videoMessage);
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        video = intent.getStringExtra("video");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(PlayVideo.this,ChatBoxGroup.class);
                intentBack.putExtra("groupId",groupId);
                startActivity(intentBack);
                finish();
            }
        });
        videoView.setVideoPath(video);
        MediaController mediaController = new MediaController(this);
        videoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaController.show( 0 );
            }
        });
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}