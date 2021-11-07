package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.GroupRecyclerAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {

    private TextView mTabPhonebook, txt_search;
    private RecyclerView recyclerView;
    private ImageView btnAddGroup;
    private GroupRecyclerAdapter adapter;
    private DataService dataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().hide();

        mapping();
        init();
        getGroupChatsByUserId();
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupActivity.this,AddGroupChat.class));
            }
        });
    }

    private void mapping() {
        mTabPhonebook = findViewById(R.id.actPhoneBook_tabPhoneBook);
        txt_search = findViewById(R.id.txt_listgroup_search);
        recyclerView = findViewById(R.id.recyclerViewGroup);
        btnAddGroup = findViewById(R.id.input_personal_creategroundfriends);
    }
    public void getGroupChatsByUserId(){
        dataService = ApiService.getService();
        Call<List<ChatGroup>> call = dataService.getChatGroupByUserId(new DataLoggedIn(GroupActivity.this).getUserIdLoggedIn());
        call.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                List<ChatGroup> list = response.body();
                adapter = new GroupRecyclerAdapter(GroupActivity.this,list);
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupActivity.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {

            }
        });
    }
    public void init(){
        //initialize
        BottomNavigationView bottomNavigationView = findViewById(R.id.actGroup_bottomNavagation);
        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.menuDanhBa);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuCaNhan:
                        startActivity(new Intent(getApplicationContext(), Personal.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        mTabPhonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                overridePendingTransition(0,0);
            }
        });
        txt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchUser.class));
            }
        });

    }
}