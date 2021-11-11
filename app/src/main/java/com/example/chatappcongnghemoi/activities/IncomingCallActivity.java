package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.CallingDTO;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.ListenSocket;
import com.example.chatappcongnghemoi.socket.VideoCallSocket;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingCallActivity extends AppCompatActivity {
    private CircleImageView avatar;
    private TextView name;
    private ImageView btnCancel, btnAccept;
    private DataService dataService;
    private User caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        getSupportActionBar().hide();

        avatar = findViewById(R.id.incoming_avatar);
        name = findViewById(R.id.incoming_name);
        btnCancel = findViewById(R.id.incoming_btn_Cancel);
        btnAccept = findViewById(R.id.incoming_btnAccept);

        dataService = ApiService.getService();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent().hasExtra("callingDTO")) {
            CallingDTO callingDTO = (CallingDTO) getIntent().getSerializableExtra("callingDTO");
            setUI(callingDTO.getCallerId());

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callingDTO.setStatus("accept");
                    new VideoCallSocket().sendStatusCallingSocket(new Gson().toJson(callingDTO));
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callingDTO.setStatus("cancel");
                    new VideoCallSocket().sendStatusCallingSocket(new Gson().toJson(callingDTO));
                }
            });

        }
    }

    private void setUI(String id) {
        User user;
        Call<UserDTO> callback = dataService.getUserById(id);
        callback.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                caller = response.body().getUser();
                Picasso.get().load(caller.getAvatar()).into(avatar);
                name.setText(caller.getUserName());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });

        finishRunForeground();
    }

    /**
     * Không cho chạy nền
     */
    private void finishRunForeground() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Get Current Activity is visible
                ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                if (cn.getClassName().equals(IncomingCallActivity.class.getName())) {
                    finish();
                }

                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }
}