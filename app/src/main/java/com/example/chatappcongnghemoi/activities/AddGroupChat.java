package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.FriendsRecyclerAdapterAddGroup;
import com.example.chatappcongnghemoi.adapters.ImageFriendsWhenClickAddGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AddGroupChat extends AppCompatActivity {
    TextView tvFriends,tvPhoneBook;
    RecyclerView recyclerView;
    public static RecyclerView recyclerViewImage;
    DataService dataService;
    ImageView btnBack,btnAddGroup;
    public static ArrayList<User> listFriendsClickAdd = new ArrayList<>();
    private ArrayList<User> friendList;
    private ArrayList<String> friendIdList;
    private FriendsRecyclerAdapterAddGroup friendsRecyclerAdapterAddGroup;
    public static ImageFriendsWhenClickAddGroupRecyclerAdapter imageFriendsWhenClickAddGroupRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_chat);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        recyclerView = findViewById(R.id.recyclerview_friends_add_group);
        btnBack = findViewById(R.id.btnBackAddGroupChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnAddGroup = findViewById(R.id.btnAddGroup);
        getFriendIdList();
        recyclerViewImage = findViewById(R.id.recyclerview_image_friends_click_add_group);
        imageFriendsWhenClickAddGroupRecyclerAdapter = new ImageFriendsWhenClickAddGroupRecyclerAdapter(AddGroupChat.this,listFriendsClickAdd);
        recyclerViewImage.setAdapter(imageFriendsWhenClickAddGroupRecyclerAdapter);
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(AddGroupChat.this,RecyclerView.HORIZONTAL,false));
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
                    if (contact.getSenderId().equals(new DataLoggedIn(AddGroupChat.this).getUserIdLoggedIn()) && contact.getStatus()) {
                        friendIdList.add(contact.getReceiverId());
                    } else if (contact.getReceiverId().equals(new DataLoggedIn(AddGroupChat.this).getUserIdLoggedIn()) && contact.getStatus()) {
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
                        friendsRecyclerAdapterAddGroup = new FriendsRecyclerAdapterAddGroup(AddGroupChat.this, friendList);
                        recyclerView.setAdapter(friendsRecyclerAdapterAddGroup);
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
}