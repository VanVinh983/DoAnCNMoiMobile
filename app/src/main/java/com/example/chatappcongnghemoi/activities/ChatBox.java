package com.example.chatappcongnghemoi.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatBox extends AppCompatActivity {
    private TextView txt_username;
    private CircleImageView avatar_user;
    private DataService dataService;
    private User userCurrent=null;
    private User friendCurrent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        String friendId = intent.getStringExtra("friendId");
        mapping();
        initialize();
        getUserById();
        getFriendById(friendId);
        findViewById(R.id.btn_chatbox_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void mapping(){
        txt_username = findViewById(R.id.txt_chatbox_username);
        avatar_user = findViewById(R.id.image_chatbox_avatart);
    }
    private void initialize(){
        dataService = ApiService.getService();
    }
    private void getUserById(){
        Call<UserDTO> call = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userCurrent = response.body().getUser();
                Glide.with(ChatBox.this).load(userCurrent.getAvatar()).into(avatar_user);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ChatBox.this, "get user by id fail ", Toast.LENGTH_LONG).show();
                System.err.println("get user by id fail"+t.getMessage());
            }
        });
    }
    private void getFriendById(String id){
        Call<UserDTO> call = dataService.getUserById(id);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                 friendCurrent = response.body().getUser();
                 txt_username.setText(friendCurrent.getUserName());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ChatBox.this, "get friend by id fail ", Toast.LENGTH_LONG).show();
                System.err.println("get friend by id fail"+t.getMessage());
            }
        });
    }

}