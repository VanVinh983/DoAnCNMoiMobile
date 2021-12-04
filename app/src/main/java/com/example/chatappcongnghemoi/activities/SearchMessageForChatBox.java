package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMessageForChatBox extends AppCompatActivity {
    private EditText input_search;
    private DataService dataService;
    private User user, friend;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message_for_chat_box);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String friendId = intent.getStringExtra("friendId");
        dataService = ApiService.getService();

        Call<UserDTO> userDTOCall = dataService.getUserById(userId);
        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                user = response.body().getUser();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        Call<UserDTO> friendCall = dataService.getUserById(friendId);
        friendCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                friend = response.body().getUser();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        Call<List<Message>> listCall = dataService.getAllMessage();
        listCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                for (Message message: response.body()) {
                    if (message.getSenderId().equals(userId)&&message.getReceiverId().equals(friendId)){
                        messages.add(message);
                    }
                    if (message.getSenderId().equals(friendId)&&message.getReceiverId().equals(userId)){
                        messages.add(message);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });

        mapping();
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = input_search.getText().toString().trim();
                if (!input.equals("")){
                    List<Message> messageSearch = new ArrayList<>();
                    for (Message message: messages) {
                        if (message.getText().contains(input)){
                            messageSearch.add(message);
                        }
                    }
                    messageAdapter = new MessageAdapter(messageSearch, SearchMessageForChatBox.this, user, friend);
                    recyclerView.setAdapter(messageAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchMessageForChatBox.this, LinearLayoutManager.VERTICAL, false);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    if (messageAdapter.getItemCount()>0){
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void mapping(){
        input_search = findViewById(R.id.input_search_message_chatbox);
        recyclerView = findViewById(R.id.recyclerview_searchmessage_chatbox);
    }
}