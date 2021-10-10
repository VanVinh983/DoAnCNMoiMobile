package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.SearchUserAdapter;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUser extends AppCompatActivity {

    private EditText input_search;
    private DataService dataService;
    private List<User> list = new ArrayList<User>();
    private SearchUserAdapter searchUserAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.search_recyclervier);

        findViewById(R.id.btn_search_user_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mapping();

        findViewById(R.id.btn_search_user_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserByPhone();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (list.size()==0){
                            handler.postDelayed(this, 500);
                        }else {
                            searchUserAdapter = new SearchUserAdapter(list, SearchUser.this);
                            recyclerView.setAdapter(searchUserAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(SearchUser.this));
                            handler.removeCallbacks(this);
                        }
                    }
                },500);
            }
        });
    }
    private void mapping(){
        input_search = findViewById(R.id.input_search_user);
        dataService = ApiService.getService();

    }
    private void getUserByPhone(){
        list = new ArrayList<User>();
        Call<UserDTO> dtoCall = dataService.getUserByPhone(input_search.getText().toString());
        dtoCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                list.add(response.body().getUser());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                System.err.println("Fail get User By Phone Number!"+t.getMessage());
            }
        });
    }
}