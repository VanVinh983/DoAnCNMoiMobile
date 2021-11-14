package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.AddMembersInfoGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.ContactRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MembersOfInfoGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoGroupChat extends AppCompatActivity {
    CircleImageView imgAvatarGroup;
    TextView tvGroupName,tvQuantityMember;
    ChatGroup chatGroup;
    String groupId;
    DataService dataService;
    ImageView btnBack;
    ImageButton btnRename,btnAddMember;
    LinearLayout btnWatchMembers,btnChangeBackground;
    User user = null;
    List<User> members ;
    DatabaseReference database;
    String background = "";
    List<String> friendIdList;
    ArrayList<User> friendList;
    public static ArrayList<User> listAddMembers = new ArrayList<>();
    AddMembersInfoGroupRecyclerAdapter adapterAddMembers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_group_chat);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        database = FirebaseDatabase.getInstance().getReference("background");
        mapping();
        init();
        btnBack.setOnClickListener((view) ->{
            Intent intent = new Intent(InfoGroupChat.this,ChatBoxGroup.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
            finish();
        });
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog =new Dialog(InfoGroupChat.this);
                dialog.setContentView(R.layout.dialog_rename_group);
                Window window = dialog.getWindow();
                if(window == null)
                    return;
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.setCancelable(true);
                TextView tvCancel = dialog.findViewById(R.id.tvCancelRename);
                TextView tvConfirm = dialog.findViewById(R.id.tvRename_Dialog);
                EditText txtRename = dialog.findViewById(R.id.txtRename);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = txtRename.getText().toString().trim();
                        if(!newName.matches("^[0-9\\u0041-\\u00ff\\u0100-\\u013c\\u01cd-\\u01ce\\s]{2,40}$")){
                            Toast.makeText(InfoGroupChat.this, "Tên nhóm không được chứa kí tự đặc biệt, có từ 2-40 kí tự", Toast.LENGTH_SHORT).show();
                        }else{
                            chatGroup.setName(newName);
                            Call<ChatGroup> chatGroupCall = dataService.updateGroup(groupId,chatGroup);
                            chatGroupCall.enqueue(new Callback<ChatGroup>() {
                                @Override
                                public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                                }

                                @Override
                                public void onFailure(Call<ChatGroup> call, Throwable t) {

                                }
                            });
                            dialog.dismiss();
                            Toast.makeText(InfoGroupChat.this, "Đổi tên nhóm thành công", Toast.LENGTH_SHORT).show();
                            tvGroupName.setText(newName);
                        }
                    }
                });
                dialog.show();
            }
        });
        btnWatchMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchMembers();
            }
        });
        btnChangeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackground();
            }
        });
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();
            }
        });
    }
    public  void mapping(){
        tvGroupName = findViewById(R.id.tvGroupNameInfoGroup);
        imgAvatarGroup = findViewById(R.id.imgAvatarInfoGroup);
        Picasso.get().load("https://www.iucn.org/sites/dev/files/content/images/2020/shutterstock_1458128810.jpg").into(imgAvatarGroup);
        tvQuantityMember = findViewById(R.id.tvQuantityMemberInfoGroup);
        btnBack = findViewById(R.id.imgBackInfoGroupChat);
        btnRename = findViewById(R.id.btnRename);
        btnWatchMembers = findViewById(R.id.btnWatchMembers);
        btnChangeBackground = findViewById(R.id.btnChangeBackgroundInfoGroup);
        btnAddMember = findViewById(R.id.imgAddMemberInfoGroup);
    }
    public void init(){
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        Call<ChatGroup> chatGroupCall = dataService.getGroupById(groupId);
        chatGroupCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                tvGroupName.setText(chatGroup.getName());
                tvQuantityMember.setText("Xem thành viên ("+chatGroup.getMembers().size()+")");
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {

            }
        });
    }
    public void watchMembers(){
//        MembersOfInfoGroupRecyclerAdapter adapter;
        final Dialog dialog =new Dialog(InfoGroupChat.this);
        dialog.setContentView(R.layout.dialog_members_info_group);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        ImageView tvClose = dialog.findViewById(R.id.btnCloseDialogMembersInfoGroup);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerviewMembersInfoGroup);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        members = new ArrayList<>();
        List<Map<String,String>> listMap = chatGroup.getMembers();
        List<String> listId = new ArrayList<>();
        listMap.forEach((map) -> {
            listId.add(map.get("userId"));
        });
        listId.forEach((id) ->{
            Call<UserDTO> userCall = dataService.getUserById(id);
            userCall.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    User user = response.body().getUser();
                    members.add(user);
                    if(members.size() == listId.size()){
                        MembersOfInfoGroupRecyclerAdapter adapter = new MembersOfInfoGroupRecyclerAdapter(members,InfoGroupChat.this,chatGroup);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InfoGroupChat.this));
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {

                }
            });
        });
        dialog.show();
    }
    public void setBackGround(String newBackground){
        background = newBackground;
    }
    public void changeBackground(){
        final Dialog dialog =new Dialog(InfoGroupChat.this);
        dialog.setContentView(R.layout.dialog_change_background_chat);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        Button btnCancel = dialog.findViewById(R.id.btnCancelChangeBackgroundInfoGroup);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmChangeBackgroundInfoGroup);
        ImageButton imgDefault = dialog.findViewById(R.id.imgBackGroundDefaultInfoGroup);
        ImageButton imgYellow = dialog.findViewById(R.id.imgBackGroundYellowInfoGroup);
        ImageButton imgBlue = dialog.findViewById(R.id.imgBackGroundBlueInfoGroup);
        ImageButton imgGreen = dialog.findViewById(R.id.imgBackGroundGreenInfoGroup);
        ImageButton imgRed = dialog.findViewById(R.id.imgBackGroundRedInfoGroup);
        ImageButton imgPink = dialog.findViewById(R.id.imgBackGroundPinkInfoGroup);
        imgGreen.setBackgroundColor(Color.WHITE);
        imgDefault.setBackgroundColor(Color.WHITE);
        imgYellow.setBackgroundColor(Color.WHITE);
        imgRed.setBackgroundColor(Color.WHITE);
        imgBlue.setBackgroundColor(Color.WHITE);
        imgPink.setBackgroundColor(Color.WHITE);
        imgDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("default");
                imgDefault.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("yellow");
                imgYellow.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("blue");
                imgBlue.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("green");
                imgGreen.setBackgroundColor(Color.BLUE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("pink");
                imgPink.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
            }
        });
        imgRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("red");
                imgRed.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(background == ""){
                    Toast.makeText(InfoGroupChat.this, "Vui lòng chọn hình nền", Toast.LENGTH_SHORT).show();
                }else{
                    Message message = new Message();
                    message.setCreatedAt(new Date().getTime());
                    message.setMessageType("note");
                    message.setReceiverId(groupId);
                    message.setSenderId(new DataLoggedIn(InfoGroupChat.this).getUserIdLoggedIn());
                    message.setText("Đã đổi hình nền nhóm chat");
                    message.setChatType("group");
                    Call<Message> messageCall = dataService.postMessage(message);
                    messageCall.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            database.child(groupId).child("background").setValue(background);
                            dialog.dismiss();
                            Toast.makeText(InfoGroupChat.this, "Đổi hình nền chat thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {

                        }
                    });
                }
            }
        });
        dialog.show();
    }
    public void addMember(){
        final Dialog dialog =new Dialog(InfoGroupChat.this);
        dialog.setContentView(R.layout.dialog_addmember_info_group);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        Button btnCancel = dialog.findViewById(R.id.btnCancelAddMembersInfoGroup);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmAddMembersInfoGroup);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerviewAddMembersInfoGroup);
        Call<ContactList> callback = dataService.getContactList();
        callback.enqueue(new Callback<ContactList>() {
            @Override
            public void onResponse(Call<ContactList> call, retrofit2.Response<ContactList> response) {
                ArrayList<Contact> contacts = response.body().getContacts();
                friendIdList = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++) {
                    Contact contact = contacts.get(i);
                    if (contact.getSenderId().equals(new DataLoggedIn(InfoGroupChat.this).getUserIdLoggedIn()) && contact.getStatus()) {
                        friendIdList.add(contact.getReceiverId());
                    } else if (contact.getReceiverId().equals(new DataLoggedIn(InfoGroupChat.this).getUserIdLoggedIn()) && contact.getStatus()) {
                        friendIdList.add(contact.getSenderId());
                    }
                }
                members = new ArrayList<>();
                List<Map<String,String>> listMap = chatGroup.getMembers();
                List<String> listId = new ArrayList<>();
                listMap.forEach((map) -> {
                    listId.add(map.get("userId"));
                });
                listId.forEach((id) ->{
                    Call<UserDTO> userCall = dataService.getUserById(id);
                    userCall.enqueue(new Callback<UserDTO>() {
                        @Override
                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                            User user = response.body().getUser();
                            members.add(user);
                            listAddMembers.add(user);
                            if(members.size() == listId.size()){
                                getFriendList(recyclerView,members);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDTO> call, Throwable t) {

                        }
                    });
                });

            }

            @Override
            public void onFailure(Call<ContactList> call, Throwable t) {
                t.printStackTrace();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAddMembers.removeAll(listAddMembers);
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (members.size() == listAddMembers.size()) {
                    Toast.makeText(InfoGroupChat.this, "Vui lòng chọn bạn bè muốn thêm", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, String> mapUserId0 = new HashMap<>();
                    Map<String, String> mapUserId1 = new HashMap<>();
                    Map<String, String> mapUserId2 = new HashMap<>();
                    Map<String, String> mapUserId3 = new HashMap<>();
                    Map<String, String> mapUserId4 = new HashMap<>();
                    Map<String, String> mapUserId5 = new HashMap<>();
                    Map<String, String> mapUserId6 = new HashMap<>();
                    Map<String, String> mapUserId7 = new HashMap<>();
                    Map<String, String> mapUserId8 = new HashMap<>();
                    Map<String, String> mapUserId9 = new HashMap<>();
                    Map<String, String> mapUserId10 = new HashMap<>();
                    Map<String, String> mapUserId11 = new HashMap<>();
                    Map<String, String> mapUserId12 = new HashMap<>();
                    Map<String, String> mapUserId13 = new HashMap<>();
                    Map<String, String> mapUserId14 = new HashMap<>();
                    Map<String, String> mapUserId15 = new HashMap<>();
                    Map<String, String> mapUserId16 = new HashMap<>();
                    Map<String, String> mapUserId17 = new HashMap<>();
                    Map<String, String> mapUserId18 = new HashMap<>();
                    Map<String, String> mapUserId19 = new HashMap<>();
                    Map<String, String> mapUserId20 = new HashMap<>();
                    Map<String, String> mapUserId21 = new HashMap<>();
                    Map<String, String> mapUserId22 = new HashMap<>();
                    Map<String, String> mapUserId23 = new HashMap<>();
                    Map<String, String> mapUserId24 = new HashMap<>();
                    Map<String, String> mapUserId25 = new HashMap<>();
                    Map<String, String> mapUserId26 = new HashMap<>();
                    Map<String, String> mapUserId27 = new HashMap<>();
                    Map<String, String> mapUserId28 = new HashMap<>();
                    Map<String, String> mapUserId29 = new HashMap<>();
                    Map<String, String> mapUserId30 = new HashMap<>();
                    Map<String, String> mapUserId31 = new HashMap<>();
                    Map<String, String> mapUserId32 = new HashMap<>();
                    Map<String, String> mapUserId33 = new HashMap<>();
                    Map<String, String> mapUserId34 = new HashMap<>();
                    Map<String, String> mapUserId35 = new HashMap<>();
                    Map<String, String> mapUserId36 = new HashMap<>();
                    Map<String, String> mapUserId37 = new HashMap<>();
                    Map<String, String> mapUserId38 = new HashMap<>();
                    Map<String, String> mapUserId39 = new HashMap<>();

                    ArrayList<Map<String, String>> newMembers = new ArrayList<>();
                    for (int i = 0; i < listAddMembers.size(); i++) {
                        if (i == 0) {
                            mapUserId0.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId0);
                        } else if (i == 1) {
                            mapUserId1.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId1);
                        } else if (i == 2) {
                            mapUserId2.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId2);
                        } else if (i == 3) {
                            mapUserId3.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId3);
                        } else if (i == 4) {
                            mapUserId4.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId4);
                        } else if (i == 5) {
                            mapUserId5.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId5);
                        } else if (i == 6) {
                            mapUserId6.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId6);
                        } else if (i == 7) {
                            mapUserId7.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId7);
                        } else if (i == 8) {
                            mapUserId8.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId8);
                        } else if (i == 9) {
                            mapUserId9.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId9);
                        } else if (i == 10) {
                            mapUserId10.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId10);
                        } else if (i == 11) {
                            mapUserId11.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId11);
                        } else if (i == 12) {
                            mapUserId12.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId12);
                        } else if (i == 13) {
                            mapUserId13.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId13);
                        } else if (i == 14) {
                            mapUserId14.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId14);
                        } else if (i == 15) {
                            mapUserId15.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId15);
                        } else if (i == 16) {
                            mapUserId16.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId16);
                        } else if (i == 17) {
                            mapUserId17.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId17);
                        } else if (i == 18) {
                            mapUserId18.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId18);
                        } else if (i == 19) {
                            mapUserId19.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId19);
                        } else if (i == 20) {
                            mapUserId20.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId20);
                        } else if (i == 21) {
                            mapUserId21.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId21);
                        } else if (i == 22) {
                            mapUserId22.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId22);
                        } else if (i == 23) {
                            mapUserId23.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId23);
                        } else if (i == 24) {
                            mapUserId24.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId24);
                        } else if (i == 25) {
                            mapUserId25.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId25);
                        } else if (i == 26) {
                            mapUserId26.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId26);
                        } else if (i == 27) {
                            mapUserId27.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId27);
                        } else if (i == 28) {
                            mapUserId28.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId28);
                        } else if (i == 29) {
                            mapUserId29.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId29);
                        } else if (i == 30) {
                            mapUserId30.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId30);
                        } else if (i == 31) {
                            mapUserId31.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId31);
                        } else if (i == 32) {
                            mapUserId32.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId32);
                        } else if (i == 33) {
                            mapUserId33.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId33);
                        } else if (i == 34) {
                            mapUserId34.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId34);
                        } else if (i == 35) {
                            mapUserId35.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId35);
                        } else if (i == 36) {
                            mapUserId36.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId36);
                        } else if (i == 37) {
                            mapUserId37.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId37);
                        } else if (i == 38) {
                            mapUserId38.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId38);
                        } else if (i == 39) {
                            mapUserId39.put("userId", listAddMembers.get(i).getId());
                            newMembers.add(mapUserId39);
                        }
                    }
                    chatGroup.setMembers(newMembers);
                    Call<ChatGroup> chatGroupCall = dataService.updateGroup(groupId, chatGroup);
                    chatGroupCall.enqueue(new Callback<ChatGroup>() {
                        @Override
                        public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {

                        }

                        @Override
                        public void onFailure(Call<ChatGroup> call, Throwable t) {

                        }
                    });
                    String username = "";
                    for (int i = members.size(); i < listAddMembers.size(); i++) {
                        if (i == listAddMembers.size()) {
                            username = username + listAddMembers.get(i).getUserName() + ".";
                        } else {
                            username = username + listAddMembers.get(i).getUserName() + ",";
                        }
                    }
                    Message message = new Message();
                    message.setCreatedAt(new Date().getTime());
                    message.setMessageType("note");
                    message.setReceiverId(groupId);
                    message.setSenderId(new DataLoggedIn(InfoGroupChat.this).getUserIdLoggedIn());
                    message.setText("Đã thêm thành viên " + username);
                    message.setChatType("group");
                    Call<Message> messageCall = dataService.postMessage(message);
                    messageCall.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            listAddMembers.removeAll(listAddMembers);
                            dialog.dismiss();
                            Toast.makeText(InfoGroupChat.this, "Thêm thành viên thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {

                        }
                    });
                }
            }
        });
        dialog.show();
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
    public void getFriendList(RecyclerView recyclerView,List<User> members) {
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
                        adapterAddMembers = new AddMembersInfoGroupRecyclerAdapter(InfoGroupChat.this, friendList,members);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InfoGroupChat.this));
                        recyclerView.setAdapter(adapterAddMembers);
                    } else
                        handler.postDelayed(this, 500);
                }
            }, 500);
        }

    }
}