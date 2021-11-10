package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.VideoCallSocket;
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

        if (getIntent().hasExtra("callerId")) {
            String callerId = getIntent().getStringExtra("callerId");
//            System.out.println("====> Caller: " + callerId);
            setUI(callerId);
        }

        if(getIntent().hasExtra("message")){
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = getIntent().getStringExtra("message") + "/" + "accept";
                    System.out.println("===> Message Status: " + message);
                    new VideoCallSocket().sendStatusCallingSocket(message);                }
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
    }
}