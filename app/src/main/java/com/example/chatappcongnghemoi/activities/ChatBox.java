package com.example.chatappcongnghemoi.activities;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.GifForChatBoxAdapter;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.adapters.TypeGifForChatBoxAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Gif;
import com.example.chatappcongnghemoi.models.InitGif;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.TypeGif;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatBox extends AppCompatActivity {
    private TextView txt_username, txt_chatbox_time;
    private DataService dataService;
    private User userCurrent=null;
    private User friendCurrent=null;
    public static List<Message> messages;
    public static MessageAdapter messageAdapter;
    public static RecyclerView recyclerViewMessage, recyclerViewgif, recyclerViewtypegif;
    private EditText input_message_text;
    public static GifForChatBoxAdapter gifForChatBoxAdapter;
    private TypeGifForChatBoxAdapter typeGifForChatBoxAdapter;
    private ImageView btnGoToBottom;
    private MessageSocket socket;
    private ImageButton btn_chatbox_file, btn_chatbox_gif, btn_cahtbox_exitgif;
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
        mSocket.on("response-add-new-file", responeAddFile);
        mSocket.on("response-reaction", responseReaction);
        mSocket.on("check-online-offline", checkOnlineOffline);
        findViewById(R.id.btn_chatboxoption_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatBox.this, Home.class));
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
                if (!input_message_text.getText().toString().trim().equals("")){
                    Message message = new Message();
                    message.setSenderId(userCurrent.getId());
                    message.setReceiverId(friendId);
                    message.setChatType("personal");
                    message.setMessageType("text");
                    message.setRead(false);
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
                }else {
                    Toast.makeText(ChatBox.this, "khung nhập tin nhắn không được trống",Toast.LENGTH_LONG).show();
                }
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
        btn_chatbox_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGif(userCurrent);
                getGitType();
                float dp = getApplicationContext().getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams layoutParams = btn_cahtbox_exitgif.getLayoutParams();
                layoutParams.height =(int)(50*dp);
                layoutParams.width = (int)(50*dp);
                btn_cahtbox_exitgif.setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParamsChoose = btn_chatbox_gif.getLayoutParams();
                layoutParamsChoose.height = 0;
                layoutParamsChoose.width = 0;
                btn_chatbox_gif.setLayoutParams(layoutParamsChoose);
                ViewGroup.LayoutParams layoutParamsGif = recyclerViewgif.getLayoutParams();
                layoutParamsGif.height =(int)(100*dp);
                layoutParamsGif.width = ViewGroup.LayoutParams.MATCH_PARENT;
                recyclerViewgif.setLayoutParams(layoutParamsGif);
            }
        });
        btn_cahtbox_exitgif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float dp = getApplicationContext().getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams layoutParams = btn_chatbox_gif.getLayoutParams();
                layoutParams.height =(int)(50*dp);
                layoutParams.width = (int)(50*dp);
                btn_chatbox_gif.setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParamsExit = btn_cahtbox_exitgif.getLayoutParams();
                layoutParamsExit.height = 0;
                layoutParamsExit.width = 0;
                btn_cahtbox_exitgif.setLayoutParams(layoutParamsExit);
                ViewGroup.LayoutParams layoutParamsGif = recyclerViewgif.getLayoutParams();
                layoutParamsGif.height =0;
                layoutParamsGif.width = 0;
                recyclerViewgif.setLayoutParams(layoutParamsGif);
                ViewGroup.LayoutParams layoutParamsTypeGif = recyclerViewtypegif.getLayoutParams();
                layoutParamsTypeGif.height =0;
                layoutParamsTypeGif.width = 0;
                recyclerViewtypegif.setLayoutParams(layoutParamsTypeGif);
            }
        });
        findViewById(R.id.btn_chatbox_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ChatBox.this, ChatBoxOption.class);
                intent1.putExtra("friendId", friendCurrent.getId());
                intent1.putExtra("userId", userCurrent.getId());
                startActivity(intent1);
            }
        });

    }
    private void getGif(User sender){
        List<Gif> gifs = new InitGif().addGif();
        System.out.println("gifs: "+gifs.toString());
        gifForChatBoxAdapter = new GifForChatBoxAdapter(gifs, userCurrent, friendCurrent, dataService, socket, ChatBox.this);
        recyclerViewgif.setAdapter(gifForChatBoxAdapter);
        recyclerViewgif.setLayoutManager(new LinearLayoutManager(ChatBox.this,RecyclerView.HORIZONTAL,false));
    }

    private void getGitType(){
        List<TypeGif> gifs = new InitGif().addTypeGif();
        typeGifForChatBoxAdapter = new TypeGifForChatBoxAdapter(gifs, userCurrent, friendCurrent, dataService, socket, ChatBox.this);
        recyclerViewtypegif.setAdapter(typeGifForChatBoxAdapter);
        recyclerViewtypegif.setLayoutManager(new LinearLayoutManager(ChatBox.this,RecyclerView.HORIZONTAL,false));
    }
    private void mapping(){
        txt_username = findViewById(R.id.txt_chatbox_username);
        txt_chatbox_time = findViewById(R.id.txt_chatbox_time);
        recyclerViewMessage = findViewById(R.id.recylerview_message);
        input_message_text = findViewById(R.id.input_chatbox_message);
        btn_chatbox_file = findViewById(R.id.btn_chatbox_file);
        btn_chatbox_gif = findViewById(R.id.btn_chatbox_gif);
        btnGoToBottom = findViewById(R.id.image_btn_gobottom);
        btnGoToBottom.setVisibility(View.INVISIBLE);
        btn_cahtbox_exitgif = findViewById(R.id.btn_chatbox_exitgif);
        recyclerViewgif = findViewById(R.id.recyclerview_chatbox_gif);
        recyclerViewtypegif = findViewById(R.id.recyclerview_chatbox_typegif);

        ViewGroup.LayoutParams layoutParams = btn_cahtbox_exitgif.getLayoutParams();
        layoutParams.height =0;
        layoutParams.width = 0;
        btn_cahtbox_exitgif.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParamsgif = recyclerViewgif.getLayoutParams();
        layoutParamsgif.width=0;
        layoutParamsgif.height=0;
        recyclerViewtypegif.setLayoutParams(layoutParamsgif);
        recyclerViewgif.setLayoutParams(layoutParamsgif);
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
                            updateIsRead(messages);
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

    private void updateIsRead(List<Message> lm){
        for (Message m:
             lm) {
            m.setRead(true);
            Call<Message> messageCall = dataService.updateMessage(m.getId(), m);
            messageCall.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {

                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {

                }
            });
        }
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
                        message.setRead(true);
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
                        messObject = new Gson().fromJson(mess.get(0).toString(), Message.class);
                        messObject.setRead(true);
                        if (!messages.get(messages.size()-1).getId().equals(messObject.getId())){
                            messages.add(messObject);
                        }
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
    private Emitter.Listener checkOnlineOffline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Object[] jsonArray = Arrays.stream(args).toArray();
                    JSONArray arr =(JSONArray) jsonArray[0];
                    String[] strings = new Gson().fromJson(String.valueOf(arr), String[].class);
                    List<String> ss = new ArrayList<String>();
                    for (String s:
                         strings) {
                        ss.add(s);
                    }
                    if (ss.contains(friendCurrent.getId())) {
                        txt_chatbox_time.setText("Mới vừa truy cập");
                    }else {
                        txt_chatbox_time.setText("đã ngắt kết nối");
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
                                    message.setRead(false);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatBox.this, Home.class));
        finish();
    }
}