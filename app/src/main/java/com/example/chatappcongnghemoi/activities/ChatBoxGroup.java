package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ChatBoxGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxGroup extends AppCompatActivity {
    private EditText txtMessage;
    private TextView tvGroupName,tvQuantityMember;
    private ImageView btnBack,btnMenu,btnOption,btnReaction,btnSend;
    private ChatBoxGroupRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String groupId;
    private DataService dataService;
    public ChatGroup chatGroup = null;
    List<Message> messages;
    List<Message> messagesGroup;
    List<String> listId;
    List<User> members;
    User userCurrent = null;
    DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_group);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        database = FirebaseDatabase.getInstance().getReference("background");
        mapping();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        database.child(groupId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getValue().toString().equals("blue")){
                        recyclerView.setBackgroundResource(R.drawable.background_chat_blue);
                        Toast.makeText(ChatBoxGroup.this, ""+task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
                    }
                    else if (task.getResult().getValue().toString().equals("yellow")){
                        recyclerView.setBackgroundResource(R.drawable.background_chat_yellow);
                        Toast.makeText(ChatBoxGroup.this, ""+task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
                    }
                    else if (task.getResult().getValue().toString().equals("green")){
                        recyclerView.setBackgroundResource(R.drawable.background_chat_green);
                        Toast.makeText(ChatBoxGroup.this, ""+task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        recyclerView.setBackgroundColor(Color.parseColor("#e9eff0"));
                        Toast.makeText(ChatBoxGroup.this, ""+task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Call<ChatGroup> groupDTOCall = dataService.getGroupById(groupId);
        groupDTOCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                tvGroupName.setText(chatGroup.getName());
                listId = new ArrayList<>();
                List<Map<String,String>> listMembers = chatGroup.getMembers();
                listMembers.forEach((map) ->{
                    listId.add(map.get("userId"));
                });
                tvQuantityMember.setText(listId.size()+" thành viên");
                getMessages(listId);
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {
                Toast.makeText(ChatBoxGroup.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatBoxGroup.this,Home.class));
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                String userId = new DataLoggedIn(ChatBoxGroup.this).getUserIdLoggedIn();
                message.setSenderId(userId);
                message.setReceiverId(groupId);
                message.setChatType("group");
                message.setMessageType("text");
                message.setCreatedAt(new Date().getTime());
                message.setText(txtMessage.getText().toString());
                Call<Message> messageCall = dataService.postMessage(message);
                txtMessage.setText("");
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        messages.add(message);
                        adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        if (adapter.getItemCount()>0){
                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMenu = new Intent(ChatBoxGroup.this,InfoGroupChat.class);
                intentMenu.putExtra("groupId",groupId);
                startActivity(intentMenu);
            }
        });
    }
    private void mapping(){
        txtMessage = findViewById(R.id.txtMessageText);
        tvGroupName = findViewById(R.id.tvGroupNameChatBoxGroup);
        tvQuantityMember = findViewById(R.id.tvQuantityMember);
        btnBack = findViewById(R.id.btnBackChatBoxGroup);
        recyclerView = findViewById(R.id.recyclerviewChatBoxGroup);
        btnSend = findViewById(R.id.imgSendMessageChatBoxGroup);
        btnMenu = findViewById(R.id.imgMenuChatBoxGroup);
//        database.child(groupId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(ChatBoxGroup.this, ""+task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
//                    if(task.getResult().getValue().toString().equals("blue"))
//                        recyclerView.setBackgroundResource(R.drawable.background_chat_blue);
//                    else if (task.getResult().getValue().toString().equals("yellow"))
//                        recyclerView.setBackgroundResource(R.drawable.background_chat_yellow);
//                    else if (task.getResult().getValue().toString().equals("green"))
//                        recyclerView.setBackgroundResource(R.drawable.background_chat_green);
//                    else
//                        recyclerView.setBackgroundColor(Color.parseColor("#e9eff0"));
//                }
//            }
//        });
    }
    public void getMessages(List<String> listId){
        members = new ArrayList<>();
        String userId = new DataLoggedIn(ChatBoxGroup.this).getUserIdLoggedIn();
        messages = new ArrayList<>();
        listId.forEach((id) -> {
            Call<UserDTO> call = dataService.getUserById(id);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    members.add(response.body().getUser());
                    if(members.size() == listId.size()){
                        Call<List<Message>> messageCall = dataService.getMessagesGroupByGroupId(groupId);
                        messageCall.enqueue(new Callback<List<Message>>() {
                            @Override
                            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                                for (Message message:
                                        response.body()) {
                                    messages.add(message);
                                }
                                Call<UserDTO> userCall = dataService.getUserById(userId);
                                userCall.enqueue(new Callback<UserDTO>() {
                                    @Override
                                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                        userCurrent = response.body().getUser();
                                        adapter = new ChatBoxGroupRecyclerAdapter(messages,ChatBoxGroup.this,userCurrent,members);
                                        recyclerView.setAdapter(adapter);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                                        linearLayoutManager.setStackFromEnd(true);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        if (adapter.getItemCount()>0){
                                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserDTO> call, Throwable t) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<List<Message>> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {

                }
            });
        });
    }
    public void getMessagesGroup(String groupId){
        messagesGroup = new ArrayList<>();
        Call<List<Message>> call = dataService.getMessagesGroupByGroupId(groupId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                for (Message message:
                        response.body()) {
                    messagesGroup.add(message);
                }
                Toast.makeText(ChatBoxGroup.this, ""+messagesGroup, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }
    public void getChatBoxById(String groupId){
        Call<ChatGroup> groupDTOCall = dataService.getGroupById(groupId);
        groupDTOCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                tvGroupName.setText(chatGroup.getName());
                List<Map<String,String>> listMembers = chatGroup.getMembers();
                listMembers.forEach((map) ->{
                    listId.add(map.get("userId"));
                });
                tvQuantityMember.setText(listId.size()+" thành viên");
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {
                Toast.makeText(ChatBoxGroup.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}