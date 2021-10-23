package com.example.chatappcongnghemoi.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.gms.common.api.Api;

public class ChatBox extends AppCompatActivity {
    private Socket socket;
    private DataService dataService;
    private User user;
    private TextView textView;

    {
        try {
            socket = IO.socket("http://192.168.1.7:4002");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        textView = findViewById(R.id.txt_chatbox);
        getUserById("6159c1f9bf628567ac523586");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user!=null){
                    System.out.println(user.toString());
                    textView.setText(user.toString());
                }else {
                    handler.postDelayed(this,500);
                }
            }
        },500);
        socket.send(user);
    }
    private void getUserById(String id){
        Call<UserDTO> dtoCall = dataService.getUserById(id);
        dtoCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                user = response.body().getUser();
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ChatBox.this, "Fail get User By Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get User By Id"+t.toString());
            }
        });
    }

}