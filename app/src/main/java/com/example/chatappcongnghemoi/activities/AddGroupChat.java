package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.FriendsRecyclerAdapterAddGroup;
import com.example.chatappcongnghemoi.adapters.ImageFriendsWhenClickAddGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.PhoneBookRecyclerAdapterAddGroup;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupChat extends AppCompatActivity {
    TextView txtGroupName;
    public static final int REQUEST_READ_CONTACTS = 79;
    RecyclerView recyclerViewFriends,recyclerViewPhoneBook;
    public static RecyclerView recyclerViewImage;
    DataService dataService;
    ImageView btnBack,btnAddGroup;
    public static ArrayList<User> listFriendsClickAdd = new ArrayList<>();
    private ArrayList<User> friendList;
    private ArrayList<String> friendIdList;
    private ArrayList phoneNumberList;
    private ArrayList<User> userList;
    private boolean flag = false;
    private FriendsRecyclerAdapterAddGroup friendsRecyclerAdapterAddGroup;
    private PhoneBookRecyclerAdapterAddGroup phoneBookRecyclerAdapterAddGroup;
    public static ImageFriendsWhenClickAddGroupRecyclerAdapter imageFriendsWhenClickAddGroupRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_chat);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        recyclerViewFriends = findViewById(R.id.recyclerview_friends_add_group);
        recyclerViewPhoneBook = findViewById(R.id.recyclerview_phonebook_add_group);
        btnBack = findViewById(R.id.btnBackAddGroupChat);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddGroupChat.this,Home.class));
                listFriendsClickAdd.removeAll(listFriendsClickAdd);
            }
        });
        txtGroupName = findViewById(R.id.txtGroupName);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            phoneNumberList = getAllContacts();
        } else {
            requestPermission();
        }

        getUserList();

//        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    phoneBookRecyclerAdapterAddGroup = new PhoneBookRecyclerAdapterAddGroup(AddGroupChat.this,userList);
                    recyclerViewPhoneBook.setAdapter(phoneBookRecyclerAdapterAddGroup);
                    recyclerViewPhoneBook.setLayoutManager(new LinearLayoutManager(AddGroupChat.this));
                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = txtGroupName.getText().toString();
                if(!groupName.matches("^[a-zA-Z0-9][a-zA-Z0-9\\s]{2,40}$")){
                    Toast.makeText(AddGroupChat.this, "Tên nhóm không được chứa kí tự đặc biệt, có từ 2-40 kí tự", Toast.LENGTH_SHORT).show();
                }else{
                    if(listFriendsClickAdd.size() < 2){
                        Toast.makeText(AddGroupChat.this, "Nhóm phải có ít nhất 3 thành viên", Toast.LENGTH_SHORT).show();
                    }else{
                        addGroup(listFriendsClickAdd,groupName);
                    }
                }
            }
        });
    }
    public void addGroup(ArrayList<User> list,String groupName){
//        list  = new ArrayList<>();
        Call<UserDTO> callUser = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        callUser.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if(response.isSuccessful()){
                    User user = response.body().getUser();
                    list.add(user);
                    ArrayList<String> listUserID = new ArrayList<>();
                    list.forEach((u -> {
                        listUserID.add(u.getId());
                    }));
//                    ChatGroup chatGroup = new ChatGroup(groupName,user.getId(),Long.parseLong(list.size()+""),listUserID);
                    ChatGroup chatGroup = new ChatGroup(groupName,user.getId(),Long.parseLong(list.size()+""),list);
                    Call<ChatGroupDTO> callGroup = dataService.createChatGroup(chatGroup);
                    callGroup.enqueue(new Callback<ChatGroupDTO>() {
                        @Override
                        public void onResponse(Call<ChatGroupDTO> call, Response<ChatGroupDTO> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(AddGroupChat.this, "OK", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddGroupChat.this, ""+response.errorBody(), Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<ChatGroupDTO> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
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
                        recyclerViewFriends.setAdapter(friendsRecyclerAdapterAddGroup);
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
    private void getUserList() {
        if (phoneNumberList != null) {
            userList = new ArrayList<>();
            for (int i = 0; i < phoneNumberList.size(); i++) {
                Call<UserDTO> callback = dataService.getUserByPhone(phoneNumberList.get(i).toString());
                callback.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        User user = response.body().getUser();
                        if (user != null)
                            userList.add(user);
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                if (i == phoneNumberList.size() - 1) {
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
                    phoneNumberList = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private ArrayList getAllContacts() {
        ArrayList<String> phoneNumberList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumberList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneNumberList;
    }
}