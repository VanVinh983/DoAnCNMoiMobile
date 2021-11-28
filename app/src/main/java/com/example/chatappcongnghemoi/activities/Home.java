package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.HomeConversationAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.Conversation;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    private ImageView btnLogout;
    private DataService dataService;
    private RecyclerView recyclerView;
    private HomeConversationAdapter userHomeAdapter;
    private List<String> stringList;
    private User userCurrent;
    private List<Conversation> conversations = new ArrayList<>();
    private ImageView btnAddGroup;
    public static  final String SHARED_PREFERENCES= "saveID";
    private static Socket mSocket = MySocket.getInstance().getSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        btnLogout = findViewById(R.id.btnLogout);
        btnAddGroup = findViewById(R.id.input_personal_creategroundfriends);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,AddGroupChat.class));
            }
        });
        //initialize
        dataService = ApiService.getService();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_home);
        recyclerView = findViewById(R.id.recyclerview_home);
        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuCaNhan:
                        startActivity(new Intent(getApplicationContext(), Personal.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuDanhBa:
                        startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        System.out.println("id la: "+new DataLoggedIn(this).getUserIdLoggedIn());
        
        Call<UserDTO> userDTOCall = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userCurrent = response.body().getUser();
                checkConversation(userCurrent);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog =new Dialog(Home.this);
                dialog.setContentView(R.layout.dialog_logout);
                Window window = dialog.getWindow();
                if(window == null)
                    return;
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.setCancelable(true);
                TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                TextView tvLogout = dialog.findViewById(R.id.tvLogout_Dialog);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                tvLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call<UserDTO> callback = dataService.getUserById(new DataLoggedIn(Home.this).getUserIdLoggedIn());
                        callback.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                if(response.isSuccessful()){
                                    dialog.dismiss();
                                    User user = response.body().getUser();
                                    user.setOnline(false);
                                    saveIDLogout();
                                    Intent intent = new Intent(Home.this,StartApp.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {

                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        socketOn();
    }
    private void socketOn(){
//        mSocket.on("response-add-new-text", responeMessage);
//        mSocket.on("response-add-new-file", responeAddFile);
    }
    public void saveIDLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId","");
        editor.apply();
    }

    private void checkConversation(User user){
        Call<List<Contact>> call = dataService.searchContactsByUserId(user.getId());
        List<String> strings = new ArrayList<>();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                List<Contact> contacts = response.body();
                for (Contact c:
                     contacts) {
                    if (!c.getSenderId().equals(user.getId())){
                        strings.add(c.getSenderId());
                    }
                    if (!c.getReceiverId().equals(user.getId())){
                        strings.add(c.getReceiverId());
                    }
                }
                for (String s:
                     strings) {
                    Call<List<Message>> listCall = dataService.getMessageBySIdAndRId(user.getId(), s);
                    listCall.enqueue(new Callback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            if (response.body().size()>0){
                                Conversation conversation = new Conversation();
                                conversation.setNewMessage(response.body().get(response.body().size()-1));
                                Call<UserDTO> userDTOCall = dataService.getUserById(s);
                                userDTOCall.enqueue(new Callback<UserDTO>() {
                                    @Override
                                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                        conversation.setFriend(response.body().getUser());
                                        conversations.add(conversation);
                                        System.out.println("cuoc hoi thoai la: "+conversations);
                                        userHomeAdapter = new HomeConversationAdapter(conversations, Home.this);
                                        recyclerView.setAdapter(userHomeAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                                    }

                                    @Override
                                    public void onFailure(Call<UserDTO> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Message>> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {

            }
        });
        Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(user.getId());
        listCall.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                List<ChatGroup> chatGroups = response.body();

                for (ChatGroup chatGroup: chatGroups) {
                    Conversation conversation = new Conversation();
                    conversation.setChatGroup(chatGroup);
                    Call<List<Message>> listCall1 = dataService.getMessageBySIdAndRId(user.getId(), chatGroup.getId());
                    listCall1.enqueue(new Callback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            conversation.setNewMessage(response.body().get(response.body().size()-1));
                            conversations.add(conversation);
                            userHomeAdapter = new HomeConversationAdapter(conversations, Home.this);
                            recyclerView.setAdapter(userHomeAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                        }

                        @Override
                        public void onFailure(Call<List<Message>> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {

            }
        });
    }


}