package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.FirendRequestRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirendRequestRecyclerAdapter adapter;
    private ImageButton btnBack;

    private ArrayList<User> senderList;
    private ArrayList<Contact> contactList;
    private DataService dataService;
    private static Socket mSocket = MySocket.getInstance().getSocket();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        getSupportActionBar().hide();

        mapping();
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(FriendRequestActivity.this, PhoneBookActivity.class));
            }
        });

        mSocket.on("response-add-new-contact", responeAddNewContact);
        mSocket.on("response-remove-request-contact", responeAddNewContact);
        mSocket.on("response-accept-Friend-Request", responeAddNewContact);

    }

    public void mapping() {
        recyclerView = findViewById(R.id.actFR_recyclerView);
        btnBack = findViewById(R.id.actFr_btnBack);
    }

    public void init() {
        dataService = ApiService.getService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        getSenderIdList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (contactList != null) {
                    getSenderList();
                    handler.removeCallbacks(this);
                } else
                    handler.postDelayed(this, 500);
            }
        }, 500);
    }

    public void getSenderIdList() {
        Call<ContactList> callback = dataService.getContactList();
        callback.enqueue(new Callback<ContactList>() {
            @Override
            public void onResponse(Call<ContactList> call, retrofit2.Response<ContactList> response) {
                ArrayList<Contact> contacts = response.body().getContacts();
                contactList = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++){
                    Contact contact = contacts.get(i);
                    if (contact.getReceiverId().equals(new DataLoggedIn(FriendRequestActivity.this).getUserIdLoggedIn()) && !contact.getStatus()) {
                        contactList.add(contact);
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getSenderList() {
        senderList = new ArrayList<>();
        if (contactList != null && contactList.size() > 0) {
            for (int i = 0; i < contactList.size(); i++) {
                getUserById(contactList.get(i).getSenderId());
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (senderList.size() == contactList.size()) {
                        senderList.sort(new Comparator<User>() {
                            @Override
                            public int compare(User o1, User o2) {
                                return o1.getUserName().compareToIgnoreCase(o2.getUserName());
                            }
                        });
                        adapter = new FirendRequestRecyclerAdapter(FriendRequestActivity.this, senderList, contactList);
                        recyclerView.setAdapter(adapter);
                    } else
                        handler.postDelayed(this, 500);
                }
            }, 500);
        }

    }

    public void getUserById(String id) {
        Call<UserDTO> callback = dataService.getUserById(id);
        callback.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, retrofit2.Response<UserDTO> response) {
                User user = response.body().getUser();
                senderList.add(user);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Emitter.Listener responeAddNewContact = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   Intent intent = getIntent();
                   finish();
                   startActivity(intent);
                }
            });
        }
    };

}