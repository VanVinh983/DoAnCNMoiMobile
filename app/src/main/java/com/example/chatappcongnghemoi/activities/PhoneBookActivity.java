package com.example.chatappcongnghemoi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.example.chatappcongnghemoi.socket.MySocket;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;

public class PhoneBookActivity extends AppCompatActivity {

    private TextView mTxtThongBao;
    private RecyclerView mRecyclerContact;
    private RecyclerView mRecyclerOnlineContact;
    private TextView mTabGroup, txt_search_user;
    private ArrayList<String> friendIdList; //Id Friend User
    private ArrayList<User> friendList;
    private ArrayList<User> onlineFriendList;
    private ContactRecyclerAdapter contactRecyclerAdapter;
    private OnlineContactRecyclerAdapter onlineContactRecyclerAdapter;
    private LinearLayout lineFriendRequest;
    private LinearLayout lineLoadPhoneBook;
    private DataService dataService;
    private ImageView btnAddGroup;
    private static Socket mSocket = MySocket.getInstance().getSocket();


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

        lineLoadPhoneBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneBookActivity.this, UserOfPhonebookActivity.class));
            }
        });

        mTxtThongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTxtThongBao.getText().toString().equals("Xem th??m...")) {
                    onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, onlineFriendList);
                    mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
                    mTxtThongBao.setText("Thu g???n");
                } else if (mTxtThongBao.getText().toString().equals("Thu g???n")) {
                    onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, new ArrayList<>(onlineFriendList.subList(0, 3)));
                    mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
                    mTxtThongBao.setText("Xem th??m...");
                }
            }
        });
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneBookActivity.this,AddGroupChat.class));
            }
        });

        /**
         * M???i 5 gi??y c???p nh???t danh s??ch b???n b?? online 1 l???n
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getOnlineFriendList();
                handler.postDelayed(this, 5000);
            }
        }, 2000);

        mSocket.on("response-delete-friend", responseDeleteFriend);
    }

    private void mapping() {
        mTabGroup = findViewById(R.id.actPhoneBook_tabGroup);
        mRecyclerContact = findViewById(R.id.actPhonebook_listContact);
        mRecyclerOnlineContact = findViewById(R.id.actPhonebook_listOnlineContact);
        mTxtThongBao = findViewById(R.id.actPhonebook_txtThongBao);
        lineFriendRequest = findViewById(R.id.actPhoneBook_friendReq);
        lineLoadPhoneBook = findViewById(R.id.actPhonebook_loadPhonebook);
        txt_search_user = findViewById(R.id.txt_phonebook_search_user);
        btnAddGroup = findViewById(R.id.input_personal_creategroundfriends);
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
                for (int i = 0; i < contacts.size(); i++) {
                    Contact contact = contacts.get(i);
                    if (contact.getSenderId().equals(new DataLoggedIn(PhoneBookActivity.this).getUserIdLoggedIn()) && contact.getStatus()) {
                        friendIdList.add(contact.getReceiverId());
                    } else if (contact.getReceiverId().equals(new DataLoggedIn(PhoneBookActivity.this).getUserIdLoggedIn()) && contact.getStatus()) {
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
                                try {
                                    return o1.getUserName().compareToIgnoreCase(o2.getUserName());
                                } catch (NullPointerException e) {
                                    return 0;
                                }
                            }
                        });

                        contactRecyclerAdapter = new ContactRecyclerAdapter(PhoneBookActivity.this, friendList);
                        mRecyclerContact.setAdapter(contactRecyclerAdapter);
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
        onlineFriendList = new ArrayList<>();
        if (friendList != null && friendList.size() > 0) {
            friendList.forEach(user -> {
                try {
                    if (user.isOnline())
                        onlineFriendList.add(user);
                } catch (NullPointerException e) {
                }
            });

            if (onlineFriendList.size() > 3) {
                onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, new ArrayList<>(onlineFriendList.subList(0, 3)));
                mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
                mTxtThongBao.setText("Xem th??m...");
            } else if (onlineFriendList.size() <= 3 && onlineFriendList.size() > 0) {
                onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, onlineFriendList);
                mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
            } else {
                mTxtThongBao.setText("Kh??ng c?? b???n b?? ??ang online");
            }
        }

    }

    private Emitter.Listener responseDeleteFriend = new Emitter.Listener() {
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