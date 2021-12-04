package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ChatBoxOptionFileAdapter;
import com.example.chatappcongnghemoi.adapters.ChatboxOptionImageAdapter;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxOption extends AppCompatActivity {
    private User friend, user;
    private DataService dataService;
    private CircleImageView avatar;
    private ChatboxOptionImageAdapter chatboxOptionImageAdapter;
    private ChatBoxOptionFileAdapter chatBoxOptionFileAdapter;
    private List<String> listImage = new ArrayList<>();
    private List<String> listFile = new ArrayList<>();
    private TextView txt_name, txt_introduce;
    private ImageButton btn_search_message, btn_personal_page;
    private RecyclerView recyclerViewImage, recyclerViewFile;
    private Button btn_delete_conversation;
    private List<Message> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_box_option);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String friendId = intent.getStringExtra("friendId");
        dataService = ApiService.getService();
        mapping();
        //get user
        Call<UserDTO> call = dataService.getUserById(userId);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                user = response.body().getUser();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        Call<UserDTO> userDTOCall = dataService.getUserById(friendId);
        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                friend = response.body().getUser();
                Glide.with(ChatBoxOption.this).load(friend.getAvatar()).into(avatar);
                txt_name.setText(friend.getUserName());
                txt_introduce.setText(friend.getDescription());
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        findViewById(R.id.btn_chatboxoption_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getImageList(friendId, userId);
        btn_delete_conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConversation();
            }
        });
    }
    private void mapping(){
        avatar = findViewById(R.id.image_avatar_option_chatbox);
        txt_introduce = findViewById(R.id.txt_chatbox_option_introduce);
        txt_name = findViewById(R.id.txt_chatbox_option_name);
        btn_personal_page = findViewById(R.id.btn_chatbox_option_personal_page);
        btn_search_message = findViewById(R.id.btn_chatbox_option_search_message);
        btn_delete_conversation  =findViewById(R.id.btn_chatbox_option_delete_conversation);
        recyclerViewImage = findViewById(R.id.recyclerview_chatbox_option_image);
        recyclerViewFile = findViewById(R.id.recyclerview_chatboxoption_file);
    }
    private void getImageList(String friendId, String userId){
        Call<List<Message>> getAllCall = dataService.getAllMessage();
        getAllCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                List<Message> listmessage = response.body();
                List<Message> messageList  = new ArrayList<>();
                for (Message message: listmessage) {
                    if (message.getReceiverId().equals(userId)&&message.getSenderId().equals(friendId)){
                        messageList.add(message);
                        messages.add(message);
                    }
                    if (message.getReceiverId().equals(friendId) && message.getSenderId().equals(userId)){
                        messageList.add(message);
                        messages.add(message);
                    }
                }
                for (Message message: messageList) {
                    System.out.println("message: "+message.toString());
                    if (message.getMessageType().equals("image")){
                        listImage.add(message.getFileName());
                    }
                    if (message.getMessageType().equals("file")){
                        listFile.add(message.getFileName());
                    }
                }
                chatboxOptionImageAdapter = new ChatboxOptionImageAdapter(listImage, ChatBoxOption.this);
                recyclerViewImage.setAdapter(chatboxOptionImageAdapter);
                recyclerViewImage.setLayoutManager(new LinearLayoutManager(ChatBoxOption.this, LinearLayoutManager.HORIZONTAL, false));

                chatBoxOptionFileAdapter  = new ChatBoxOptionFileAdapter(listFile, ChatBoxOption.this);
                recyclerViewFile.setAdapter(chatBoxOptionFileAdapter);
                recyclerViewFile.setLayoutManager(new LinearLayoutManager(ChatBoxOption.this));
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }
    private void deleteConversation(){
        for (Message message: messages) {
           Call<Message> messageCall = dataService.deleteMessage(message.getId());
           messageCall.enqueue(new Callback<Message>() {
               @Override
               public void onResponse(Call<Message> call, Response<Message> response) {
                   messages.remove(message);
               }

               @Override
               public void onFailure(Call<Message> call, Throwable t) {
                   Toast.makeText(ChatBoxOption.this, "Xóa không thành công", Toast.LENGTH_SHORT).show();
               }
           });
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (messages.size()==0){
                    Toast.makeText(ChatBoxOption.this, "Đã xóa Cuộc hội thoại", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChatBoxOption.this, Home.class));
                    finish();
                }else {
                    Toast.makeText(ChatBoxOption.this, "Đang xóa...", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(this,500);
                }
            }
        }, 500);
    }

}