package com.example.chatappcongnghemoi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.OnlineContactRecyclerAdapter;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class PhoneBookActivity extends AppCompatActivity {

    private TextView mTxtThongBao;
    private RecyclerView mRecyclerContact;
    private RecyclerView mRecyclerOnlineContact;
    private TextView mTabGroup, txt_search_user;
    private ArrayList<String> friendIdList; //Id Friend User
    private ArrayList<User> friendList;
    private ContactRecyclerAdapter contactRecyclerAdapter;
    private OnlineContactRecyclerAdapter onlineContactRecyclerAdapter;
    private LinearLayout lineFriendRequest;
    private LinearLayout lineLoadPhoneBook;
    private DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mapping();
        init();

        lineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneBookActivity.this, FriendRequestActivity.class));
            }
        });

    }

    private void mapping() {
        mTabGroup = findViewById(R.id.actPhoneBook_tabGroup);
        mRecyclerContact = findViewById(R.id.actPhonebook_listContact);
        mRecyclerOnlineContact = findViewById(R.id.actPhonebook_listOnlineContact);
        mTxtThongBao = findViewById(R.id.actPhonebook_txtThongBao);
        lineFriendRequest = findViewById(R.id.actPhoneBook_friendReq);
        lineLoadPhoneBook = findViewById(R.id.actPhonebook_loadPhonebook);
        txt_search_user = findViewById(R.id.txt_phonebook_search_user);
    }

    public void init() {
        dataService = ApiService.getService();
        //initialize
        BottomNavigationView bottomNavigationView = findViewById(R.id.actPhonebook_bottomNavagation);
        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.menuDanhBa);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuCaNhan:
                        startActivity(new Intent(getApplicationContext(), Personal.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        mTabGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GroupActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        txt_search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchUser.class));
            }
        });

        mRecyclerContact.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerOnlineContact.setLayoutManager(new LinearLayoutManager(this));

        getFriendIdList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (friendIdList != null) {
                    getFriendList();
                    handler.removeCallbacks(this);
                } else
                    handler.postDelayed(this, 500);
            }
        }, 500);


    }

    public void getFriendIdList() {
        Call<ContactList> callback = dataService.getContactList();
        callback.enqueue(new Callback<ContactList>() {
            @Override
            public void onResponse(Call<ContactList> call, retrofit2.Response<ContactList> response) {
                ArrayList<Contact> contacts = response.body().getContacts();
                friendIdList = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++){
                Contact contact = contacts.get(i);
                    if (contact.getSenderId().equals(DataLoggedIn.userIdLoggedIn) && contact.getStatus()) {
                        friendIdList.add(contact.getReceiverId());
                    } else if (contact.getReceiverId().equals(DataLoggedIn.userIdLoggedIn) && contact.getStatus()) {
                        friendIdList.add(contact.getSenderId());
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getFriendList() {
        friendList = new ArrayList<>();
        if (friendIdList != null && friendIdList.size() > 0) {
            for (int i = 0; i < friendIdList.size(); i++) {
                getUserById(friendIdList.get(i));
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (friendList.size() == friendIdList.size()) {
                        friendList.sort(new Comparator<User>() {
                            @Override
                            public int compare(User o1, User o2) {
                                return o1.getUserName().compareToIgnoreCase(o2.getUserName());
                            }
                        });

                        contactRecyclerAdapter = new ContactRecyclerAdapter(PhoneBookActivity.this, friendList);
                        mRecyclerContact.setAdapter(contactRecyclerAdapter);

                        getOnlineFriendList();
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
                friendList.add(user);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getOnlineFriendList() {
        if (friendList.size() > 3) {
            onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, new ArrayList<User>(friendList.subList(0, 3)));
            mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
            mTxtThongBao.setText("Xem thêm...");
        } else if (friendList.size() <= 3 && friendList.size() > 0) {
            onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, friendList);
            mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
        } else {
            mTxtThongBao.setText("Không có bạn bè đang online");
        }
    }

}