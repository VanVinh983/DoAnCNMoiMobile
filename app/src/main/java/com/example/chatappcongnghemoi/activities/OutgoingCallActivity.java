package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.socket.VideoCallSocket;
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
            String type = bundle.getString("type", "video");

            Picasso.get().load(receiver.getAvatar()).into(avatar);
            name.setText(receiver.getUserName());
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(new Intent(OutgoingCallActivity.this, PhoneBookActivity.class));
                }
            });

            String message = getUserId() + "/" + receiver.getId() + "/" + type;
//            System.out.println("===>Type Call: " + type);
            new VideoCallSocket().sendStatusCallingSocket(message);
        }
    }

    private String getUserId() {
        final String SHARED_PREFERENCES = "saveID";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

}