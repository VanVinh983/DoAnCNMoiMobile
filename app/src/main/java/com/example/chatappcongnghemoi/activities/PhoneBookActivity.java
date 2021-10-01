package com.example.chatappcongnghemoi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class PhoneBookActivity extends AppCompatActivity {
    private String userIdLoggedIn = "614ddf15fe79c83cac2a7423";
    private final String BASE_URL_USER = "http://192.168.56.1:4000/users/";
    private final String BASE_URL_CONTACT = "http://192.168.56.1:4000/contacts/";

    private TextView mTxtThongBao;
    private RecyclerView mRecyclerContact;
    private RecyclerView mRecyclerOnlineContact;
    private TextView mTabGroup;
    private ArrayList<String> contactIdList; //Id Friend User
    private ArrayList<User> contactUserList;
    private ContactRecyclerAdapter contactRecyclerAdapter;
    private OnlineContactRecyclerAdapter onlineContactRecyclerAdapter;

    boolean checkRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mapping();
        init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkRequest) {
                    getContactUserList();
                    handler.removeCallbacks(this);
                } else
                    handler.postDelayed(this, 500);
            }
        }, 500);


    }

    private void mapping() {
        mTabGroup = findViewById(R.id.actPhoneBook_tabGroup);
        mRecyclerContact = findViewById(R.id.actPhonebook_listContact);
        mRecyclerOnlineContact = findViewById(R.id.actPhonebook_listOnlineContact);
        mTxtThongBao = findViewById(R.id.actPhonebook_txtThongBao);
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

        getContactUserIdList();
    }

    public void getContactUserIdList() {
        contactIdList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        checkRequest = true;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL_CONTACT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray contacts = jsonObject.getJSONArray("contacts");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject contact = (JSONObject) contacts.get(i);
                        String userId = contact.getString("userId");
                        String contactId = contact.getString("contactId");
                        Boolean status = contact.getBoolean("status");

                        if (userId.equals(userIdLoggedIn) && status) {
                            contactIdList.add(contactId);
                        } else if (contactId.equals(userIdLoggedIn) && status) {
                            contactIdList.add(userId);
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

    public void getContactUserList() {
        contactUserList = new ArrayList<>();
        if (contactIdList != null && contactIdList.size() > 0) {
            for (int i = 0; i < contactIdList.size(); i++) {
                getUserById(contactIdList.get(i));
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!checkRequest) {
//                        contactUserList.forEach(u->{
//                            Log.d("tag", u.toString());
//                        });
                        for(int i=0; i<4; i++){
                            contactUserList.add(contactUserList.get(i));
                        }
                        contactUserList.sort(new Comparator<User>() {
                            @Override
                            public int compare(User o1, User o2) {
                                return o1.getUserName().compareToIgnoreCase(o2.getUserName());
                            }
                        });
                        contactRecyclerAdapter = new ContactRecyclerAdapter(PhoneBookActivity.this, contactUserList);
                        mRecyclerContact.setAdapter(contactRecyclerAdapter);

                        getOnlineContactList();
                    } else
                        handler.postDelayed(this, 500);
                }
            }, 500);
        }

    }

    public void getUserById(String id) {
        checkRequest = true;
        RequestQueue requestQueue = Volley.newRequestQueue(PhoneBookActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL_USER + "/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    User user = new User();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonUser = jsonObject.getJSONObject("user");
                    user.setUserId(jsonUser.getString("_id"));
                    user.setUserName(jsonUser.getString("userName"));
                    user.setAvatar(jsonUser.getString("avatar"));

                    contactUserList.add(user);

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

    public void getOnlineContactList() {
        onlineContactRecyclerAdapter = new OnlineContactRecyclerAdapter(PhoneBookActivity.this, contactUserList);
        mRecyclerOnlineContact.setAdapter(onlineContactRecyclerAdapter);
        if(contactUserList.size()>3){
            mTxtThongBao.setText("Xem thêm...");
        }else if(contactIdList.size() <= 0){
            mTxtThongBao.setText("Không có bạn bè đang online");
        }
    }

}