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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.OnlineContactRecyclerAdapter;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.utils.RestfulLink;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class PhoneBookActivity extends AppCompatActivity {

    private TextView mTxtThongBao;
    private RecyclerView mRecyclerContact;
    private RecyclerView mRecyclerOnlineContact;
    private TextView mTabGroup;
    private ArrayList<String> friendIdList; //Id Friend User
    private ArrayList<User> friendList;
    private ContactRecyclerAdapter contactRecyclerAdapter;
    private OnlineContactRecyclerAdapter onlineContactRecyclerAdapter;
    private LinearLayout lineFriendRequest;
    private LinearLayout lineLoadPhoneBook;

    boolean checkRequest = false;

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
    }

    public void init() {
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

        mRecyclerContact.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerOnlineContact.setLayoutManager(new LinearLayoutManager(this));

        getFriendIdList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkRequest) {
                    getFriendList();
                    handler.removeCallbacks(this);
                } else
                    handler.postDelayed(this, 500);
            }
        }, 500);


    }

    public void getFriendIdList() {
        friendIdList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        checkRequest = true;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, RestfulLink.BASE_URL_CONTACT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray contacts = jsonObject.getJSONArray("contacts");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject contact = (JSONObject) contacts.get(i);
                        String senderId = contact.getString("senderId");
                        String receiverId = contact.getString("receiverId");
                        Boolean status = contact.getBoolean("status");

                        if (senderId.equals(RestfulLink.userIdLoggedIn) && status) {
                            friendIdList.add(receiverId);
                        } else if (receiverId.equals(RestfulLink.userIdLoggedIn) && status) {
                            friendIdList.add(senderId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkRequest = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                checkRequest = false;
            }
        });
        requestQueue.add(stringRequest);
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
                    if (!checkRequest) {
//                        contactUserList.forEach(u->{
//                            Log.d("tag", u.toString());
//                        });
//                        for(int i=0; i<4; i++){
//                            contactUserList.add(contactUserList.get(i));
//                        }
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
        checkRequest = true;
        RequestQueue requestQueue = Volley.newRequestQueue(PhoneBookActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, RestfulLink.BASE_URL_USER + "/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    User user = new User();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonUser = jsonObject.getJSONObject("user");
                    user.setUserId(jsonUser.getString("_id"));
                    user.setUserName(jsonUser.getString("userName"));
                    user.setAvatar(jsonUser.getString("avatar"));

                    friendList.add(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkRequest = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getOnlineFriendList() {
        if (friendList.size() > 3) {
            onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this,  new ArrayList<User>(friendList.subList(0, 3)));
            mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
            mTxtThongBao.setText("Xem thêm...");
        } else if (friendList.size() <= 3 && friendList.size() > 0) {
            onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, friendList);
            mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
        }else {
            mTxtThongBao.setText("Không có bạn bè đang online");
        }
    }

}