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
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.example.chatappcongnghemoi.socket.MySocket;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatBox extends AppCompatActivity {
    private TextView txt_username;
    private DataService dataService;
    private User userCurrent=null;
    private User friendCurrent=null;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerViewMessage;
    private EditText input_message_text;
    private MessageSocket socket;
    private static Socket mSocket = MySocket.getInstance().getSocket();
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
        findViewById(R.id.btn_chatbox_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.setSenderId(userCurrent.getId());
                message.setReceiverId(friendId);
                message.setChatType("personal");
                message.setMessageType("text");
                message.setText(input_message_text.getText().toString());
                Call<Message> messageCall = dataService.postMessage(message);
                input_message_text.setText("");
                socket.sendMessage(message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messages.add(message1);
                        messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                        recyclerViewMessage.setAdapter(messageAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerViewMessage.setLayoutManager(linearLayoutManager);
                        if (messageAdapter.getItemCount()>0){
                            recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                        }
                    }
                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(ChatBox.this, "fail post message", Toast.LENGTH_LONG).show();
                        System.err.println("fail post message"+t.getMessage());
                    }
                });
            }
        });
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userCurrent.getId()!=null){
                    Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrent.getId());
                    listCall.enqueue(new Callback<List<ChatGroup>>() {
                        @Override
                        public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                            socket = new MessageSocket(response.body(), userCurrent);
                        }
                        @Override
                        public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                            System.err.println("fail get list group by user"+t.getMessage());
                        }
                    });
                    handler1.removeCallbacks(this);
                }else {
                    handler1.postDelayed(this, 500);
                }
            }
        },500);
    }
    private void mapping(){
        txt_username = findViewById(R.id.txt_chatbox_username);
        recyclerViewMessage = findViewById(R.id.recylerview_message);
        input_message_text = findViewById(R.id.input_chatbox_message);
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
                messages = new ArrayList<>();
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

    @Override
    protected void onPause() {
        super.onPause();
        mSocket.disconnect();
    }
}