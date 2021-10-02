package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.FirendRequestRecyclerAdapter;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.until.RestfulLink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirendRequestRecyclerAdapter adapter;
    private ImageButton btnBack;

    private ArrayList<User> senderList;
    private ArrayList<Contact> contactList;
    private boolean checkRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mapping();
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(FriendRequestActivity.this, PhoneBookActivity.class));
            }
        });

    }

    public void mapping() {
        recyclerView = findViewById(R.id.actFR_recyclerView);
        btnBack = findViewById(R.id.actFr_btnBack);
    }

    public void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        getSenderIdList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkRequest) {
                    getSenderList();
                    handler.removeCallbacks(this);
                } else
                    handler.postDelayed(this, 500);
            }
        }, 500);
    }

    public void getSenderIdList() {
        contactList = new ArrayList<>();

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
                        String _id = contact.getString("_id");
                        String receiverId = contact.getString("receiverId");
                        String senderId = contact.getString("senderId");
                        boolean status = contact.getBoolean("status");


                        if (receiverId.equals(RestfulLink.userIdLoggedIn) && !status) {
                            contactList.add(new Contact(_id, senderId, receiverId, status));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkRequest = false;
//                Log.d("tag", contactList.size() + "");
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
                    if (!checkRequest) {
                        senderList.sort(new Comparator<User>() {
                            @Override
                            public int compare(User o1, User o2) {
                                return o1.getUserName().compareToIgnoreCase(o2.getUserName());
                            }
                        });
                        adapter = new FirendRequestRecyclerAdapter(FriendRequestActivity.this, senderList, contactList, RestfulLink.BASE_URL_CONTACT);
                        recyclerView.setAdapter(adapter);
                    } else
                        handler.postDelayed(this, 500);
                }
            }, 500);
        }

    }

    public void getUserById(String id) {
        checkRequest = true;
        RequestQueue requestQueue = Volley.newRequestQueue(FriendRequestActivity.this);
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

                    senderList.add(user);

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


}