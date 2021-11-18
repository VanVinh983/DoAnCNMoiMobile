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
import com.example.chatappcongnghemoi.adapters.ChatBoxGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.FriendsRecyclerAdapterAddGroup;
import com.example.chatappcongnghemoi.adapters.ImageFriendsWhenClickAddGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.PhoneBookRecyclerAdapterAddGroup;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.GroupSocket;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
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
    private GroupSocket groupSocket;
    private MessageSocket messageSocket;
    private User userCurrent = null;
    private List<ChatGroup> chatGroupList;
    private static Socket mSocket = MySocket.getInstance().getSocket();
    private FriendsRecyclerAdapterAddGroup friendsRecyclerAdapterAddGroup;
    private PhoneBookRecyclerAdapterAddGroup phoneBookRecyclerAdapterAddGroup;
    public static ImageFriendsWhenClickAddGroupRecyclerAdapter imageFriendsWhenClickAddGroupRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_chat);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        mSocket.on("response-add-new-text", responeMessage);
        mSocket.on("response-create-group", responeCreateGroup);
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
        Call<UserDTO> userDTOCall = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userCurrent = response.body().getUser();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        getUserList();
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userCurrent.getId()!=null){
                    Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrent.getId());
                    listCall.enqueue(new Callback<List<ChatGroup>>() {
                        @Override
                        public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                            groupSocket = new GroupSocket(response.body(),userCurrent);
                            messageSocket = new MessageSocket(response.body(),userCurrent);
                        }
                        @Override
                        public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                            System.err.println("fail get list group by user"+t.getMessage());
                        }
                    });
                    handler1.removeCallbacks(this);
                }else {
                    handler1.postDelayed(this, 500);
                }
            }
        },500);
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
                if(!groupName.matches("^[0-9\\u0041-\\u00ff\\u0100-\\u013c\\u01cd-\\u01ce\\s]{2,40}$")){
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
        Call<UserDTO> callUser = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        callUser.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if(response.isSuccessful()){
                    User user = response.body().getUser();
                    list.add(user);
                    Map<String,String> mapUserId0 = new HashMap<>();
                    Map<String,String> mapUserId1 = new HashMap<>();
                    Map<String,String> mapUserId2 = new HashMap<>();
                    Map<String,String> mapUserId3 = new HashMap<>();
                    Map<String,String> mapUserId4 = new HashMap<>();
                    Map<String,String> mapUserId5 = new HashMap<>();
                    Map<String,String> mapUserId6 = new HashMap<>();
                    Map<String,String> mapUserId7 = new HashMap<>();
                    Map<String,String> mapUserId8 = new HashMap<>();
                    Map<String,String> mapUserId9 = new HashMap<>();
                    Map<String,String> mapUserId10 = new HashMap<>();
                    Map<String,String> mapUserId11 = new HashMap<>();
                    Map<String,String> mapUserId12 = new HashMap<>();
                    Map<String,String> mapUserId13 = new HashMap<>();
                    Map<String,String> mapUserId14 = new HashMap<>();
                    Map<String,String> mapUserId15 = new HashMap<>();
                    Map<String,String> mapUserId16 = new HashMap<>();
                    Map<String,String> mapUserId17 = new HashMap<>();
                    Map<String,String> mapUserId18 = new HashMap<>();
                    Map<String,String> mapUserId19 = new HashMap<>();
                    Map<String,String> mapUserId20 = new HashMap<>();
                    Map<String,String> mapUserId21 = new HashMap<>();
                    Map<String,String> mapUserId22 = new HashMap<>();
                    Map<String,String> mapUserId23 = new HashMap<>();
                    Map<String,String> mapUserId24 = new HashMap<>();
                    Map<String,String> mapUserId25 = new HashMap<>();
                    Map<String,String> mapUserId26 = new HashMap<>();
                    Map<String,String> mapUserId27 = new HashMap<>();
                    Map<String,String> mapUserId28 = new HashMap<>();
                    Map<String,String> mapUserId29 = new HashMap<>();
                    Map<String,String> mapUserId30 = new HashMap<>();
                    Map<String,String> mapUserId31 = new HashMap<>();
                    Map<String,String> mapUserId32 = new HashMap<>();
                    Map<String,String> mapUserId33 = new HashMap<>();
                    Map<String,String> mapUserId34 = new HashMap<>();
                    Map<String,String> mapUserId35 = new HashMap<>();
                    Map<String,String> mapUserId36 = new HashMap<>();
                    Map<String,String> mapUserId37 = new HashMap<>();
                    Map<String,String> mapUserId38 = new HashMap<>();
                    Map<String,String> mapUserId39 = new HashMap<>();

                    ArrayList<Map<String,String>> members = new ArrayList<>();
                    for(int i = 0 ; i < list.size() ;i++){
                        if( i== 0){
                            mapUserId0.put("userId",list.get(i).getId());
                            members.add(mapUserId0);
                        }else if(i == 1){
                            mapUserId1.put("userId",list.get(i).getId());
                            members.add(mapUserId1);
                        }else if(i == 2){
                            mapUserId2.put("userId",list.get(i).getId());
                            members.add(mapUserId2);
                        }else if(i == 3){
                            mapUserId3.put("userId",list.get(i).getId());
                            members.add(mapUserId3);
                        }else if(i == 4){
                            mapUserId4.put("userId",list.get(i).getId());
                            members.add(mapUserId4);
                        }else if(i == 5){
                            mapUserId5.put("userId",list.get(i).getId());
                            members.add(mapUserId5);
                        }else if(i == 6){
                            mapUserId6.put("userId",list.get(i).getId());
                            members.add(mapUserId6);
                        }else if(i == 7){
                            mapUserId7.put("userId",list.get(i).getId());
                            members.add(mapUserId7);
                        }else if(i == 8){
                            mapUserId8.put("userId",list.get(i).getId());
                            members.add(mapUserId8);
                        }else if(i == 9){
                            mapUserId9.put("userId",list.get(i).getId());
                            members.add(mapUserId9);
                        }else if(i == 10){
                            mapUserId10.put("userId",list.get(i).getId());
                            members.add(mapUserId10);
                        }else if(i == 11){
                            mapUserId11.put("userId",list.get(i).getId());
                            members.add(mapUserId11);
                        }else if(i == 12){
                            mapUserId12.put("userId",list.get(i).getId());
                            members.add(mapUserId12);
                        }else if(i == 13){
                            mapUserId13.put("userId",list.get(i).getId());
                            members.add(mapUserId13);
                        }else if(i == 14){
                            mapUserId14.put("userId",list.get(i).getId());
                            members.add(mapUserId14);
                        }else if(i == 15){
                            mapUserId15.put("userId",list.get(i).getId());
                            members.add(mapUserId15);
                        }else if(i == 16){
                            mapUserId16.put("userId",list.get(i).getId());
                            members.add(mapUserId16);
                        }else if(i == 17){
                            mapUserId17.put("userId",list.get(i).getId());
                            members.add(mapUserId17);
                        }else if(i == 18){
                            mapUserId18.put("userId",list.get(i).getId());
                            members.add(mapUserId18);
                        }else if(i == 19){
                            mapUserId19.put("userId",list.get(i).getId());
                            members.add(mapUserId19);
                        }else if(i == 20){
                            mapUserId20.put("userId",list.get(i).getId());
                            members.add(mapUserId20);
                        }else if(i == 21){
                            mapUserId21.put("userId",list.get(i).getId());
                            members.add(mapUserId21);
                        }else if(i == 22){
                            mapUserId22.put("userId",list.get(i).getId());
                            members.add(mapUserId22);
                        }
                        else if(i == 23){
                            mapUserId23.put("userId",list.get(i).getId());
                            members.add(mapUserId23);
                        }else if(i == 24){
                            mapUserId24.put("userId",list.get(i).getId());
                            members.add(mapUserId24);
                        }else if(i == 25){
                            mapUserId25.put("userId",list.get(i).getId());
                            members.add(mapUserId25);
                        }else if(i == 26){
                            mapUserId26.put("userId",list.get(i).getId());
                            members.add(mapUserId26);
                        }else if(i == 27){
                            mapUserId27.put("userId",list.get(i).getId());
                            members.add(mapUserId27);
                        }else if(i == 28){
                            mapUserId28.put("userId",list.get(i).getId());
                            members.add(mapUserId28);
                        }else if(i == 29){
                            mapUserId29.put("userId",list.get(i).getId());
                            members.add(mapUserId29);
                        }else if(i == 30){
                            mapUserId30.put("userId",list.get(i).getId());
                            members.add(mapUserId30);
                        }else if(i == 31){
                            mapUserId31.put("userId",list.get(i).getId());
                            members.add(mapUserId31);
                        }else if(i == 32){
                            mapUserId32.put("userId",list.get(i).getId());
                            members.add(mapUserId32);
                        }else if(i == 33){
                            mapUserId33.put("userId",list.get(i).getId());
                            members.add(mapUserId33);
                        }else if(i == 34){
                            mapUserId34.put("userId",list.get(i).getId());
                            members.add(mapUserId34);
                        }else if(i == 35){
                            mapUserId35.put("userId",list.get(i).getId());
                            members.add(mapUserId35);
                        }else if(i == 36){
                            mapUserId36.put("userId",list.get(i).getId());
                            members.add(mapUserId36);
                        }else if(i == 37){
                            mapUserId37.put("userId",list.get(i).getId());
                            members.add(mapUserId37);
                        }else if(i == 38){
                            mapUserId38.put("userId",list.get(i).getId());
                            members.add(mapUserId38);
                        }else if(i == 39){
                            mapUserId39.put("userId",list.get(i).getId());
                            members.add(mapUserId39);
                        }
                    };
                    ChatGroup chatGroup = new ChatGroup(groupName,user.getId(),Long.parseLong(list.size()+""),members);
                    Call<ChatGroupDTO> callGroup = dataService.createChatGroup(chatGroup);
                    callGroup.enqueue(new Callback<ChatGroupDTO>() {
                        @Override
                        public void onResponse(Call<ChatGroupDTO> call, Response<ChatGroupDTO> response) {
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (userCurrent.getId()!=null){
                                        Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrent.getId());
                                        listCall.enqueue(new Callback<List<ChatGroup>>() {
                                            @Override
                                            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                                                chatGroupList = response.body();
                                                chatGroupList.forEach(group -> {
                                                if(group.getUserId().equals(userCurrent.getId())&& group.getName().equals(groupName)){
                                                    Message message = new Message();
                                                    message.setCreatedAt(new Date().getTime());
                                                    message.setMessageType("note");
                                                    message.setReceiverId(group.getId());
                                                    message.setSenderId(new DataLoggedIn(AddGroupChat.this).getUserIdLoggedIn());
                                                    message.setText(" đã tạo nhóm.");
                                                    message.setChatType("group");
                                                    Call<Message> messageCall = dataService.postMessage(message);
                                                    messageCall.enqueue(new Callback<Message>() {
                                                        @Override
                                                        public void onResponse(Call<Message> call, Response<Message> response) {
                                                            Message message1 = response.body();
                                                            groupSocket.createGroup(group);
                                                            messageSocket.sendMessage(message1,"true");
                                                            Toast.makeText(AddGroupChat.this, "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(AddGroupChat.this,Home.class));
                                                            listFriendsClickAdd.removeAll(listFriendsClickAdd);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Message> call, Throwable t) {

                                                        }
                                                    });
                                                }
                                            });
                                            }
                                            @Override
                                            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                                                System.err.println("fail get list group by user"+t.getMessage());
                                            }
                                        });
                                        handler1.removeCallbacks(this);
                                    }else {
                                        handler1.postDelayed(this, 500);
                                    }
                                }
                            },500);

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
    private Emitter.Listener responeMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };
    private Emitter.Listener responeCreateGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];

                }
            });
        }
    };
}