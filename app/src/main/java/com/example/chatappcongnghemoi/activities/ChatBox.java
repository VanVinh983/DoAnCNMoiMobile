package com.example.chatappcongnghemoi.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.List;

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
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerViewMessage;
    private EditText input_message_text;
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
        // get message
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userCurrent!=null&&friendCurrent!=null){
                    getMessage();
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this,500);
                }
            }
        },500);
    }
    private void mapping(){
        txt_username = findViewById(R.id.txt_chatbox_username);
        avatar_user = findViewById(R.id.image_chatbox_avatart);
        recyclerViewMessage = findViewById(R.id.recylerview_message);
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
    private void getMessage(){
        Call<List<Message>> listCall = dataService.getMessageBySIdAndRId(userCurrent.getId(), friendCurrent.getId());
        listCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                List<Message> messages = new ArrayList<>();
                for (Message message:
                     response.body()) {
                    messages.add(message);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (messages.size()==response.body().size()){
                            messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                            recyclerViewMessage.setAdapter(messageAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                            linearLayoutManager.setStackFromEnd(true);
                            recyclerViewMessage.setLayoutManager(linearLayoutManager);
                            if (messageAdapter.getItemCount()>0){
                                recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                            }
                            handler.removeCallbacks(this);
                        }else {
                            handler.postDelayed(this,500);
                        }
                    }
                },500);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ChatBox.this, "get list message fail ", Toast.LENGTH_LONG).show();
                System.err.println("get list message fail"+t.getMessage());
            }
        });
    }
}