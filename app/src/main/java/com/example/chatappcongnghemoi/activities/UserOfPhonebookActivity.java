package com.example.chatappcongnghemoi.activities;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.UserPhonebookAdapter;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.models.UserPhonebook;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOfPhonebookActivity extends AppCompatActivity {
    public static final int REQUEST_READ_CONTACTS = 79;
    private DataService dataService;

    private RecyclerView listViewAccount, listViewNotAccount;
    private ImageButton btnBack;

    private ArrayList<UserPhonebook> userPhonebooks;
    private ArrayList<User> userList;
    private UserPhonebookAdapter userPhonebookAdapter;
    private ContactRecyclerAdapter adapter;


    private boolean flag = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_of_phonebook);
        getSupportActionBar().hide();

        btnBack = findViewById(R.id.actUserPhoneBook_btnBack);
        listViewAccount = findViewById(R.id.actUserPhoneBook_recyclerView);
        listViewNotAccount = findViewById(R.id.actUserPhoneBook_listNotAccount);
        dataService = ApiService.getService();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            getAllContacts();
        } else {
            requestPermission();
        }

        getUserList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    adapter = new ContactRecyclerAdapter(UserOfPhonebookActivity.this, userList);
                    listViewAccount.setLayoutManager(new LinearLayoutManager(UserOfPhonebookActivity.this));
                    listViewAccount.setAdapter(adapter);

                    userPhonebookAdapter = new UserPhonebookAdapter(UserOfPhonebookActivity.this, userPhonebooks);
                    listViewNotAccount.setLayoutManager(new LinearLayoutManager(UserOfPhonebookActivity.this));
                    listViewNotAccount.setAdapter(userPhonebookAdapter);

                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getUserList() {
        if (userPhonebooks != null) {
            userList = new ArrayList<>();

            for (int i = 0; i < userPhonebooks.size(); i++) {
                int position = i;
                Call<UserDTO> callback = dataService.getUserByPhone(userPhonebooks.get(i).getPhone());
                callback.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        User user = response.body().getUser();
                        if (user != null)
                            userList.add(user);
                        else {
                            userPhonebooks.add(new UserPhonebook(userPhonebooks.get(position).getName(), userPhonebooks.get(position).getPhone()));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

                if (i == userPhonebooks.size() - 1) {
                    flag = true;
                }
            }
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void getAllContacts() {
        userPhonebooks = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
//                System.out.println("===> NAME: " + name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo = getSpaceOnPhoneNumber(phoneNo);
//                        System.out.println("===> PHONE NUMBER: " + phoneNo);
                        userPhonebooks.add(new UserPhonebook(name, phoneNo));
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    public String getSpaceOnPhoneNumber(String phoneNumber) {
        String result = "";
        String[] phoneNumberPath = phoneNumber.split(" ");
        if (phoneNumberPath.length > 0) {
            for (int i = 0; i < phoneNumberPath.length; i++) {
                result += phoneNumberPath[i];
            }
            return result;
        } else {
            return phoneNumber;
        }
    }
}