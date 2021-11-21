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
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.UserHomeAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactDTO;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    private ImageView btnLogout;
    private DataService dataService;
    private RecyclerView recyclerView;
    private List<User> listConversation = new ArrayList<User>();
    private List<String> listIdFiend = new ArrayList<>();
    private UserHomeAdapter userHomeAdapter;
    private String userCurrentId;
    private ImageView btnAddGroup;
    public static  final String SHARED_PREFERENCES= "saveID";
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
        userCurrentId = new DataLoggedIn(this).getUserIdLoggedIn();
        addUserByIdIntoUsers();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listIdFiend.size()!=0){
                    System.out.println("list id:"+listIdFiend);
                    getMessageBySenderIdAndRevicerId();
                    handler.removeCallbacks(this);
                }
            }
        },500);
//        SharedPreferences sharedPreferences = getSharedPreferences("saveID", MODE_PRIVATE);
//        String id = sharedPreferences.getString("userId","");
//        if (!id.equals("")){
//            Log.d("tag","Preferences is: "+ id);
//            Log.d("tag", "ID: "+ new DataLoggedIn(this).getUserIdLoggedIn());
//        }

//         ContactService contactService = new ContactService(this, "614ddf15fe79c83cac2a7423");
//        UserService userService = new UserService(this, "614ddf15fe79c83cac2a7423");
//        Handler handler = new Handler();
//        contactService.VolleyGetContactsByUserId();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(contactService.isFlag())
//                    handler.postDelayed(this, 500);
//                else{
//                    contactIdList = contactService.getContactIdList();
//                    userService.getContactUserList(contactIdList);
//                    handler.removeCallbacks(this);
//                }
//            }
//        }, 500);
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
    }
    public void saveIDLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId","");
        editor.apply();
    }

    private void addUserByIdIntoUsers(){
        Call<List<Contact>> dtoCall = dataService.searchContactsByUserId(userCurrentId);
        dtoCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public synchronized void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                List<Contact> contactList = response.body();
                for (int i = 0; i < contactList.size(); i++) {
                    if ( contactList.get(i).getStatus()==true){
                        if (!contactList.get(i).getSenderId().equals(userCurrentId)){
                            listIdFiend.add(contactList.get(i).getSenderId());
                        }else
                        if (!contactList.get(i).getReceiverId().equals(userCurrentId)){
                            listIdFiend.add(contactList.get(i).getReceiverId());
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(Home.this, "Fail get contact list By user Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get contact list By user Id" + t.toString());
            }
        });
    };
    private void getMessageBySenderIdAndRevicerId(){
        List<String> stringList = new ArrayList<>();
        for (String id: listIdFiend) {
            List<Message> messages = new ArrayList<>();
            Call<List<Message>> call = dataService.getMessageBySIdAndRId(userCurrentId, id);
            call.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    for (Message m: response.body()) {
                        messages.add(m);
                    }
                }
                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Toast.makeText(Home.this, "Fail get message list By user Id", Toast.LENGTH_SHORT).show();
                    System.err.println("Fail get message list By user Id" + t.toString());
                }
            });
            Call<List<Message>> call2 = dataService.getMessageBySIdAndRId(id, userCurrentId);
            call2.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    for (Message m: response.body()) {
                        messages.add(m);
                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Toast.makeText(Home.this, "Fail get message at call 2 list By user Id", Toast.LENGTH_SHORT).show();
                    System.err.println("Fail get message at call2 list By user Id" + t.toString());
                }
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(messages.size()!=0){
                        System.out.println("message: "+messages.toString());
                        stringList.add(id);
                        handler.removeCallbacks(this);
                    }
                }
            },500);
        }
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> stringListwithout = stringList.stream().distinct().collect(Collectors.toList());
                System.out.println("string list: "+stringListwithout.toString());
                getConversationByUserId(stringList);
                handler1.removeCallbacks(this);
            }
        },500);
    }

    private void getConversationByUserId(List<String> stringList){
        for (String id: stringList) {
            Call<UserDTO> call = dataService.getUserById(id);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    User user = response.body().getUser();
                    listConversation.add(user);
                }
                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Toast.makeText(Home.this, "Fail get user By user Id", Toast.LENGTH_SHORT).show();
                    System.err.println("Fail get user By user Id" + t.toString());
                }
            });
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listConversation.size()==stringList.size()){
                    Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrentId);
                    listCall.enqueue(new Callback<List<ChatGroup>>() {
                        @Override
                        public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                            List<ChatGroup> chatGroups = response.body();
                            for (ChatGroup chatGroup: response.body()) {
                                User user = new User();
                                user.setId(chatGroup.getId());
                                user.setUserName(chatGroup.getName());
                                String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
                                user.setAvatar(url_s3+chatGroup.getAvatar());
                                listConversation.add(user);
                            }
                            userHomeAdapter = new UserHomeAdapter(listConversation, Home.this, userCurrentId, chatGroups);
                            recyclerView.setAdapter(userHomeAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                        }
                        @Override
                        public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                            Toast.makeText(Home.this, "Fail get chat group By user Id", Toast.LENGTH_SHORT).show();
                            System.err.println("Fail get chat group By user Id" + t.toString());
                        }
                    });
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this,500);
                }
            }
        },500);
    }
}