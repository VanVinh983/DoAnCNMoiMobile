package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ChatBoxGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.facebook.common.file.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxGroup extends AppCompatActivity {
    private EditText txtMessage;
    private TextView tvGroupName,tvQuantityMember;
    private ImageView btnBack,btnMenu,btnOption,btnReaction,btnSend;
    public static ChatBoxGroupRecyclerAdapter adapter;
    public static RecyclerView recyclerView;
    private String groupId;
    private DataService dataService;
    public ChatGroup chatGroup = null;
    List<Message> messages;
    List<Message> messagesGroup;
    List<String> listId;
    List<User> members;
    User userCurrent = null;
    DatabaseReference database;
    boolean flag = true;
    private MessageSocket socket;
    private static Socket mSocket = MySocket.getInstance().getSocket();
    public static final int PICKFILE_RESULT_CODE = 1;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_group);
        getSupportActionBar().hide();
        new AmplifyInitialize(ChatBoxGroup.this).amplifyInitialize();
        mSocket.on("response-delete-group",responeDeleteGroup);
        dataService = ApiService.getService();
        database = FirebaseDatabase.getInstance().getReference("background");
        mapping();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        database.child(groupId).child("background").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getValue() == null) {
                        recyclerView.setBackgroundResource(R.drawable.background_chat_default);
                    }else{
                        if(task.getResult().getValue().equals("blue")){
                            recyclerView.setBackgroundResource(R.drawable.background_chat_blue);
                        }
                        else if (task.getResult().getValue().equals("yellow")){
                            recyclerView.setBackgroundResource(R.drawable.background_chat_yellow);
                        }
                        else if (task.getResult().getValue().equals("pink")){
                            recyclerView.setBackgroundResource(R.drawable.background_chat_pink);
                        }
                        else if (task.getResult().getValue().equals("green")){
                            recyclerView.setBackgroundResource(R.drawable.background_chat_green);
                        }
                        else if (task.getResult().getValue().equals("red")){
                            recyclerView.setBackgroundResource(R.drawable.background_chat_red);
                        }
                        else{
                            recyclerView.setBackgroundResource(R.drawable.background_chat_default);
                        }
                    }
                }
            }
        });
        mSocket.on("response-add-new-text", responeMessage);
        mSocket.on("response-add-new-file", responeAddFile);
        Call<ChatGroup> groupDTOCall = dataService.getGroupById(groupId);
        groupDTOCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                tvGroupName.setText(chatGroup.getName());
                listId = new ArrayList<>();
                List<Map<String,String>> listMembers = chatGroup.getMembers();
                listMembers.forEach((map) ->{
                    listId.add(map.get("userId"));
                });
                tvQuantityMember.setText(listId.size()+" thành viên");
                getMessages(listId);
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {
                Toast.makeText(ChatBoxGroup.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatBoxGroup.this,Home.class));
            }
        });
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtMessage.getText().toString().equals(""))
                    btnSend.setVisibility(View.INVISIBLE);
                else{
                    btnSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userCurrent.getId()!=null){
                    Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrent.getId());
                    listCall.enqueue(new Callback<List<ChatGroup>>() {
                        @Override
                        public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                            socket = new MessageSocket(response.body(), userCurrent);
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
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                String userId = new DataLoggedIn(ChatBoxGroup.this).getUserIdLoggedIn();
                message.setSenderId(userId);
                message.setReceiverId(groupId);
                message.setChatType("group");
                message.setMessageType("text");
                message.setCreatedAt(new Date().getTime());
                message.setText(txtMessage.getText().toString());
                Call<Message> messageCall = dataService.postMessage(message);
                txtMessage.setText("");
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        socket.sendMessage(message1,"true");
                        messages.add(message1);
                        adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        if (adapter.getItemCount()>0){
                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMenu = new Intent(ChatBoxGroup.this,InfoGroupChat.class);
                intentMenu.putExtra("groupId",groupId);
                startActivity(intentMenu);
            }
        });
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(-1)&& dy<0){
                    count =count +10;
                    Call<List<Message>> messageCall = dataService.getMessagePaging(groupId,count);
                    messageCall.enqueue(new Callback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            if(response.body().size() == 0){

                            }else{
                                List<Message> list = new ArrayList<>();
                                for(int i = response.body().size()-1;i>=0;i--){
                                    list.add(response.body().get(i));
                                }
                                messages.addAll(0,list);
                                list.clear();
                                adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this, userCurrent, members);
                                recyclerView.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                                linearLayoutManager.setStackFromEnd(true);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.scrollToPosition(messages.size()-count);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Message>> call, Throwable t) {

                        }
                    });
                }

            }
        });
    }
    public void showFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent,PICKFILE_RESULT_CODE );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    Uri fileUri = data.getData();
                    File file = new File(getPath(fileUri));
                    UUID uuid = UUID.randomUUID();
                    String extension = file.getName().substring(file.getName().lastIndexOf(".")+1);
                    try {
                        InputStream exampleInputStream = getContentResolver().openInputStream(fileUri);
                        com.amplifyframework.core.Amplify.Storage.uploadInputStream(
                                uuid+"."+file.getName(),
                                exampleInputStream,
                                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                        );
                        Message message = new Message();
                        String userId = new DataLoggedIn(ChatBoxGroup.this).getUserIdLoggedIn();
                        message.setSenderId(userId);
                        message.setReceiverId(groupId);
                        message.setChatType("group");
                        if(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("svg") || extension.equals("gif"))
                            message.setMessageType("image");
                        else
                            message.setMessageType("file");
                        message.setCreatedAt(new Date().getTime());
                        message.setFileName(uuid+"."+file.getName());
                        Toast.makeText(ChatBoxGroup.this, ""+message.getMessageType(), Toast.LENGTH_SHORT).show();
                        Call<Message> messageCall = dataService.postMessage(message);
                        messageCall.enqueue(new Callback<Message>() {
                            @Override
                            public void onResponse(Call<Message> call, Response<Message> response) {
                                Message message1 = response.body();
                                List<Message> list = new ArrayList<>();
                                list.add(message1);
                                socket.sendFile(list,"true");
                                messages.add(message1);
                                adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
                                recyclerView.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                                linearLayoutManager.setStackFromEnd(true);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                if (adapter.getItemCount()>0){
                                    recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                                }
                            }

                            @Override
                            public void onFailure(Call<Message> call, Throwable t) {

                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    private String getPath(Uri uri)
    {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }
    public static void sendEventDeleteMessage(List<Message> messages,User userCurrent,List<User> members,Context context){
        adapter = new ChatBoxGroupRecyclerAdapter(messages, context,userCurrent, members);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (adapter.getItemCount()>0){
            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        }
    }
    private void mapping(){
        txtMessage = findViewById(R.id.txtMessageText);
        tvGroupName = findViewById(R.id.tvGroupNameChatBoxGroup);
        tvQuantityMember = findViewById(R.id.tvQuantityMember);
        btnBack = findViewById(R.id.btnBackChatBoxGroup);
        recyclerView = findViewById(R.id.recyclerviewChatBoxGroup);
        btnSend = findViewById(R.id.imgSendMessageChatBoxGroup);
        btnSend.setVisibility(View.INVISIBLE);
        btnMenu = findViewById(R.id.imgMenuChatBoxGroup);
        btnOption = findViewById(R.id.btnOptionsChatBoxGroup);
//        database.child(groupId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(ChatBoxGroup.this, ""+task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
//                    if(task.getResult().getValue().toString().equals("blue"))
//                        recyclerView.setBackgroundResource(R.drawable.background_chat_blue);
//                    else if (task.getResult().getValue().toString().equals("yellow"))
//                        recyclerView.setBackgroundResource(R.drawable.background_chat_yellow);
//                    else if (task.getResult().getValue().toString().equals("green"))
//                        recyclerView.setBackgroundResource(R.drawable.background_chat_green);
//                    else
//                        recyclerView.setBackgroundColor(Color.parseColor("#e9eff0"));
//                }
//            }
//        });
    }
    public void getMessages(List<String> listId){
        members = new ArrayList<>();
        String userId = new DataLoggedIn(ChatBoxGroup.this).getUserIdLoggedIn();
        messages = new ArrayList<>();
        listId.forEach((id) -> {
            Call<UserDTO> call = dataService.getUserById(id);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    members.add(response.body().getUser());
                    if(members.size() == listId.size()){
                        Call<List<Message>> messageCall = dataService.getMessagePaging(groupId,0);
                        messageCall.enqueue(new Callback<List<Message>>() {
                            @Override
                            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                                for (Message message:
                                        response.body()) {
                                    messages.add(message);
                                }
                                Call<UserDTO> userCall = dataService.getUserById(userId);
                                userCall.enqueue(new Callback<UserDTO>() {
                                    @Override
                                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                        userCurrent = response.body().getUser();
                                        adapter = new ChatBoxGroupRecyclerAdapter(messages,ChatBoxGroup.this,userCurrent,members);
                                        recyclerView.setAdapter(adapter);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                                        linearLayoutManager.setStackFromEnd(true);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        if (adapter.getItemCount()>0){
                                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserDTO> call, Throwable t) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<List<Message>> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {

                }
            });
        });
    }
    public void getMessagesGroup(String groupId){
        messagesGroup = new ArrayList<>();
        Call<List<Message>> call = dataService.getMessagesGroupByGroupId(groupId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                for (Message message:
                        response.body()) {
                    messagesGroup.add(message);
                }
                Toast.makeText(ChatBoxGroup.this, ""+messagesGroup, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }
    private Emitter.Listener responeMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String mess = null;
                    try {
                        mess = data.getString("message");
                        Gson gson = new Gson();
                        Message message = gson.fromJson(mess, Message.class);
                        messages.add(message);
                        adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        if (adapter.getItemCount()>0){
                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener responeAddFile = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray mess = null;
                    Message messObject = null;
                    try {
                        mess = data.getJSONArray("messages");
                        messObject = new Gson().fromJson(mess.get(0).toString(), Message.class);
                        messages.add(messObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
                    recyclerView.setAdapter(adapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    if (adapter.getItemCount()>0){
                        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                    }
                }
            });
        }
    };
    private Emitter.Listener responeDeleteGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ChatBoxGroup.this,Home.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    };
}