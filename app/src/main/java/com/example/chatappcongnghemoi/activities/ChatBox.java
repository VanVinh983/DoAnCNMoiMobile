package com.example.chatappcongnghemoi.activities;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ChatBoxGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatBox extends AppCompatActivity {
    private TextView txt_username;
    private DataService dataService;
    private User userCurrent=null;
    private User friendCurrent=null;
    public static List<Message> messages;
    public static MessageAdapter messageAdapter;
    public static RecyclerView recyclerViewMessage;
    private EditText input_message_text;
    private ImageView btnGoToBottom;
    private MessageSocket socket;
    private ImageButton btn_chatbox_file, btn_chatbox_gif;
    private static final int PICKFILE_RESULT_CODE = 1;
    private int  count = 0;
    private static Socket mSocket = MySocket.getInstance().getSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        String friendId = intent.getStringExtra("friendId");
        mapping();
        initialize();
        getUserById();
        getFriendById(friendId);
        new AmplifyInitialize(ChatBox.this).amplifyInitialize();
        mSocket.on("response-add-new-text", responeMessage);
        mSocket.on("response-add-new-files", responeAddFile);
        mSocket.on("response-reaction", responseReaction);
        findViewById(R.id.btn_chatbox_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // get message
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userCurrent!=null&&friendCurrent!=null){
                    getMessage();
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this,500);
                }
            }
        },500);
        findViewById(R.id.btn_chatbox_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.setSenderId(userCurrent.getId());
                message.setReceiverId(friendId);
                message.setChatType("personal");
                message.setMessageType("text");
                message.setText(input_message_text.getText().toString());
                Call<Message> messageCall = dataService.postMessage(message);
                input_message_text.setText("");
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        socket.sendMessage(message1,"false");
                        messages.add(message1);
                        messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                        recyclerViewMessage.setAdapter(messageAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerViewMessage.setLayoutManager(linearLayoutManager);
                        if (messageAdapter.getItemCount()>0){
                            recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                        }
                    }
                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(ChatBox.this, "fail post message", Toast.LENGTH_LONG).show();
                        System.err.println("fail post message"+t.getMessage());
                    }
                });
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
//                            socket = new MessageSocket(response.body(), userCurrent);
                            socket = new MessageSocket();
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
        btn_chatbox_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        recyclerViewMessage.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)){
                    btnGoToBottom.setVisibility(View.INVISIBLE);
                }else{
                    btnGoToBottom.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(-1)&& dy<0){
                    count =count +10;
                    Call<List<Message>> messageCall = dataService.getMessageBySIdAndRIdPaging(userCurrent.getId() ,friendCurrent.getId(),count);
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
                                messageAdapter = new MessageAdapter(messages, ChatBox.this, userCurrent, friendCurrent);
                                recyclerViewMessage.setAdapter(messageAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                                linearLayoutManager.setStackFromEnd(true);
                                recyclerViewMessage.setLayoutManager(linearLayoutManager);
                                recyclerViewMessage.scrollToPosition(messages.size()-count);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Message>> call, Throwable t) {

                        }
                    });
                }
            }
        });
        btnGoToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewMessage.scrollToPosition(messages.size()-1);
                btnGoToBottom.setVisibility(View.INVISIBLE);
            }
        });

    }
    private void mapping(){
        txt_username = findViewById(R.id.txt_chatbox_username);
        recyclerViewMessage = findViewById(R.id.recylerview_message);
        input_message_text = findViewById(R.id.input_chatbox_message);
        btn_chatbox_file = findViewById(R.id.btn_chatbox_file);
        btn_chatbox_gif = findViewById(R.id.btn_chatbox_gif);
        btnGoToBottom = findViewById(R.id.image_btn_gobottom);
        btnGoToBottom.setVisibility(View.INVISIBLE);
    }
    private void initialize(){
        dataService = ApiService.getService();
    }
    private void getUserById(){
        Call<UserDTO> call = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userCurrent = response.body().getUser();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ChatBox.this, "get user by id fail ", Toast.LENGTH_LONG).show();
                System.err.println("get user by id fail"+t.getMessage());
            }
        });
    }
    private void getFriendById(String id){
        Call<UserDTO> call = dataService.getUserById(id);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                 friendCurrent = response.body().getUser();
                 txt_username.setText(friendCurrent.getUserName());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ChatBox.this, "get friend by id fail ", Toast.LENGTH_LONG).show();
                System.err.println("get friend by id fail"+t.getMessage());
            }
        });
    }
    private void getMessage(){
        Call<List<Message>> listCall = dataService.getMessageBySIdAndRId(userCurrent.getId(), friendCurrent.getId());
        listCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
               System.out.println("so luong tin nhan la: "+ response.body().size());
                messages = new ArrayList<>();
                for (Message message:
                     response.body()) {
                    messages.add(message);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (messages.size()==response.body().size()){
                            messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                            recyclerViewMessage.setAdapter(messageAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                            linearLayoutManager.setStackFromEnd(true);
                            recyclerViewMessage.setLayoutManager(linearLayoutManager);
                            if (messageAdapter.getItemCount()>0){
                                recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                            }
                            handler.removeCallbacks(this);
                        }else {
                            handler.postDelayed(this,500);
                        }
                    }
                },500);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ChatBox.this, "get list message fail ", Toast.LENGTH_LONG).show();
                System.err.println("get list message fail"+t.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Emitter.Listener responeMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String mess = null;
                    String isgroup = null;
                    try {
                        mess = data.getString("message");
                        isgroup = data.getString("isChatGroup");
                        Gson gson = new Gson();
                        Message message = gson.fromJson(mess, Message.class);
                        messages.add(message);
                        messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                        recyclerViewMessage.setAdapter(messageAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerViewMessage.setLayoutManager(linearLayoutManager);
                        if (messageAdapter.getItemCount()>0){
                            recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener responseReaction = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Message mess = new Gson().fromJson(data.getString("message"), Message.class);
                        System.out.println(mess.toString());
                        for (int i = 0; i < messages.size(); i++) {
                            if (mess.getId().equals(messages.get(i).getId())){
                                messages.get(i).setReaction(mess.getReaction());
                                messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                                recyclerViewMessage.setAdapter(messageAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                                linearLayoutManager.setStackFromEnd(true);
                                recyclerViewMessage.setLayoutManager(linearLayoutManager);
                                if (messageAdapter.getItemCount()>0){
                                    recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                                }
                            }
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
                        String isChatGroup = data.getString("isChatGroup");
                        messObject = new Gson().fromJson(mess.get(0).toString(), Message.class);
                        messages.add(messObject);
                        if(isChatGroup.equals("false")){
                            Toast.makeText(ChatBox.this, "cc" + isChatGroup, Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(ChatBox.this, "dsads" + isChatGroup, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    messageAdapter = new MessageAdapter(messages, ChatBox.this,userCurrent, friendCurrent);
                    recyclerViewMessage.setAdapter(messageAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerViewMessage.setLayoutManager(linearLayoutManager);
                    if (messageAdapter.getItemCount()>0){
                        recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                    }
                }
            });
        }
    };
    public void showFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent,PICKFILE_RESULT_CODE );
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    Uri fileUri = data.getData();
                    File file = new File(getPath(fileUri));
                    UUID uuid = UUID.randomUUID();
                    String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                    try {
                        InputStream exampleInputStream = getContentResolver().openInputStream(fileUri);
                        com.amplifyframework.core.Amplify.Storage.uploadInputStream(
                                uuid + "." + file.getName(),
                                exampleInputStream,
                                result -> {
                                    Message message = new Message();
                                    String userId = userCurrent.getId();
                                    message.setSenderId(userId);
                                    message.setReceiverId(friendCurrent.getId());
                                    message.setChatType("personal");
                                    if (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("svg") || extension.equals("gif"))
                                        message.setMessageType("image");
                                    else
                                        message.setMessageType("file");
                                    message.setCreatedAt(new Date().getTime());
                                    message.setFileName(uuid + "." + file.getName());
                                    Toast.makeText(ChatBox.this, "" + message.getMessageType(), Toast.LENGTH_SHORT).show();
                                    Call<Message> messageCall = dataService.postMessage(message);
                                    messageCall.enqueue(new Callback<Message>() {
                                        @Override
                                        public void onResponse(Call<Message> call, Response<Message> response) {
                                            Message message1 = response.body();
                                            List<Message> list = new ArrayList<>();
                                            list.add(message1);
                                            socket.sendFile(list, "false");
                                            messages.add(message1);
                                            messageAdapter = new MessageAdapter(messages, ChatBox.this, userCurrent, friendCurrent);
                                            recyclerViewMessage.setAdapter(messageAdapter);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBox.this, LinearLayoutManager.VERTICAL, false);
                                            linearLayoutManager.setStackFromEnd(true);
                                            recyclerViewMessage.setLayoutManager(linearLayoutManager);
                                            if (messageAdapter.getItemCount() > 0) {
                                                recyclerViewMessage.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Message> call, Throwable t) {

                                        }
                                    });
                                    Toast.makeText(ChatBox.this, "Hoàn Tất", Toast.LENGTH_LONG).show();
                                },
                                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                        );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

}