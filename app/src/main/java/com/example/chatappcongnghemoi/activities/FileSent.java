package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.MessageFileSentRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MessageImageSentRecyclerAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileSent extends AppCompatActivity {
    String groupId;
    DataService dataService;
    RecyclerView recyclerView;
    ImageView btnBack;
    int count = 0;
    MessageFileSentRecyclerAdapter messageFileSentRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sent);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        recyclerView = findViewById(R.id.recyclerview_fileSent);
        btnBack = findViewById(R.id.btnBackFileSent);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(FileSent.this,InfoGroupChat.class);
                intentBack.putExtra("groupId",groupId);
                startActivity(intentBack);
                finish();
            }
        });
        Call<ChatGroup> chatGroupCall = dataService.getGroupById(groupId);
        chatGroupCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                getFileSent(response.body());
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {

            }
        });

    }
    public void getFileSent(ChatGroup chatGroup){
        ArrayList<Map<String,String>> mapMembers = chatGroup.getMembers();
        List<String> listId = new ArrayList<>();
        List<Message> allMessages = new ArrayList<>();
        mapMembers.forEach(map ->{
            listId.add(map.get("userId"));
        });
        count = 0;
        listId.forEach(id -> {
            count++;
            Call<List<Message>> call = dataService.getAllMessages(id,chatGroup.getId());
            call.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    List<Message> messages = response.body();
                    messages.forEach(mess ->{
                        if(mess.getMessageType().equals("file"))
                            allMessages.add(mess);
                    });
                    if(count == listId.size()){
                        Collections.sort(allMessages, new Comparator<Message>() {
                            @Override
                            public int compare(Message o1, Message o2) {
                                return Integer.valueOf(o2.getCreatedAt().compareTo(o1.getCreatedAt()));
                            }
                        });
                        messageFileSentRecyclerAdapter = new MessageFileSentRecyclerAdapter(allMessages,FileSent.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(FileSent.this,RecyclerView.VERTICAL,false));
                        recyclerView.setAdapter(messageFileSentRecyclerAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {

                }
            });
        });
    }
}