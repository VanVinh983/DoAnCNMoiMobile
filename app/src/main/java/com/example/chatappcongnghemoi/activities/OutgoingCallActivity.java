package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.CallingDTO;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.socket.VideoCallSocket;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OutgoingCallActivity extends AppCompatActivity {

    private CircleImageView avatar;
    private TextView name;
    private ImageView btnCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);
        getSupportActionBar().hide();

        avatar = findViewById(R.id.outgoing_avatar);
        name = findViewById(R.id.outgoing_name);
        btnCancel = findViewById(R.id.outgoing_btn_Cancel);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            User receiver = bundle.getParcelable("Receiver");

            Picasso.get().load(receiver.getAvatar()).into(avatar);
            name.setText(receiver.getUserName());

            CallingDTO callingDTO = (CallingDTO) bundle.getSerializable("callingDTO");
            callingDTO.setCallerId(getUserId());
            callingDTO.setReceiverId(receiver.getId());
            callingDTO.setStatus("none");
            new VideoCallSocket().sendStatusCallingSocket(new Gson().toJson(callingDTO));

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callingDTO.setStatus("cancel");
                    new VideoCallSocket().sendStatusCallingSocket(new Gson().toJson(callingDTO));
                }
            });

            finishRunForeground();
        }
    }

    private String getUserId() {
        final String SHARED_PREFERENCES = "saveID";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    /**
     * Không cho chạy nền
     */
    private void finishRunForeground(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Get Current Activity is visible
                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                if(cn.getClassName().equals(OutgoingCallActivity.class.getName())){
                    finish();
                }

                handler.postDelayed(this, 60000);
            }
        }, 10000);
    }

}