package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ChatBoxGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.GifRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.MessageAdapter;
import com.example.chatappcongnghemoi.adapters.MessageGhimRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.TypeGifRecyclerAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
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

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxGroup extends AppCompatActivity {
    public static RecyclerView recyclerViewGhim;
    public static MessageGhimRecyclerAdapter messageGhimRecyclerAdapter;
    private EditText txtMessage;
    public static Button btnShowGhim;
    public static LinearLayout viewGhim;
    CircleImageView btnChooseGif,btnExitGif;
    private TextView tvGroupName,tvQuantityMember;
    public static  TextView tvMessageGhim,tvUsernameGhim;
    private ImageView btnBack,btnMenu,btnOption,btnReaction,btnSend,btnGoToBottom;
    public static ChatBoxGroupRecyclerAdapter adapter;
    public static RecyclerView recyclerView;
    public static RecyclerView recyclerViewGif;
    private String groupId;
    private DataService dataService;
    public ChatGroup chatGroup = null;
    RecyclerView recyclerViewTypeGif;
    TypeGifRecyclerAdapter typeGifRecyclerAdapter;
    public static GifRecyclerAdapter gifAdapter;
    public static List<Message> messages = new ArrayList<>();
    List<String> listId;
    List<User> members;
    User userCurrent = null;
    private MessageSocket socket;
    private static Socket mSocket = MySocket.getInstance().getSocket();
    public static final int PICKFILE_RESULT_CODE = 1;
    public static int  count = 0;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_group);
        getSupportActionBar().hide();
        new AmplifyInitialize(ChatBoxGroup.this).amplifyInitialize();
        dataService = ApiService.getService();
        count= 0;
        mapping();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        Call<ChatGroup> groupDTOCall = dataService.getGroupById(groupId);
        groupDTOCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                getGhim(chatGroup,dataService);
                tvGroupName.setText(chatGroup.getName());
                listId = new ArrayList<>();
                List<Map<String,String>> listMembers = chatGroup.getMembers();
                listMembers.forEach((map) ->{
                    listId.add(map.get("userId"));
                });
                tvQuantityMember.setText(listId.size()+" thành viên");
                getMessages(listId);
                if(chatGroup.getBackground().equals("blue")){
                    recyclerView.setBackgroundResource(R.drawable.background_chat_blue);
                }
                else if (chatGroup.getBackground().equals("yellow")){
                    recyclerView.setBackgroundResource(R.drawable.background_chat_yellow);
                }
                else if (chatGroup.getBackground().equals("pink")){
                    recyclerView.setBackgroundResource(R.drawable.background_chat_pink);
                }
                else if (chatGroup.getBackground().equals("green")){
                    recyclerView.setBackgroundResource(R.drawable.background_chat_green);
                }
                else if (chatGroup.getBackground().equals("red")){
                    recyclerView.setBackgroundResource(R.drawable.background_chat_red);
                }
                else{
                    recyclerView.setBackgroundResource(R.drawable.background_chat_default);
                }
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {
                Toast.makeText(ChatBoxGroup.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatBoxGroup.this, Home.class));
                finish();
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
        socket = new MessageSocket();
        Call<UserDTO> userDTOCall = dataService.getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userCurrent = response.body().getUser();
                Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrent.getId());
                listCall.enqueue(new Callback<List<ChatGroup>>() {
                    @Override
                    public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                        List<String> groupIds = new ArrayList<>();
                        response.body().forEach(group -> {
                            groupIds.add(group.getId());
                        });
                        getGifs(userCurrent);
                        getTypeGifs(userCurrent);
                        mSocket.on("response-add-new-text", responseMessage);
                        mSocket.on("response-delete-group",responseDeleteGroup);
                        mSocket.on("response-leave-group",responseLeaveGroup);
                        mSocket.on("response-add-new-file", responseAddFile);
                        mSocket.on("response-reaction",responseReaction);
                    }
                    @Override
                    public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                        System.err.println("fail get list group by user"+t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                String userId = new DataLoggedIn(ChatBoxGroup.this).getUserIdLoggedIn();
                message.setSenderId(userId);
                message.setReceiverId(groupId);
                message.setChatType("group");
                message.setMessageType("text");
                message.setRead(true);
                message.setCreatedAt(new Date().getTime());
                message.setText(txtMessage.getText().toString());
                Call<Message> messageCall = dataService.postMessage(message);
                txtMessage.setText("");
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        socket.sendMessage(message1,"true");
//                        btnGoToBottom.setVisibility(View.INVISIBLE);
//                        messages.add(message1);
//                        adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
//                        recyclerView.setAdapter(adapter);
//                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
//                        linearLayoutManager.setStackFromEnd(true);
//                        recyclerView.setLayoutManager(linearLayoutManager);
//                        if (adapter.getItemCount()>0){
//                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
//                        }
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
                finish();
            }
        });
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        btnChooseGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float dp = getApplicationContext().getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams layoutParams = btnExitGif.getLayoutParams();
                layoutParams.height =(int)(50*dp);
                layoutParams.width = (int)(50*dp);
                btnExitGif.setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParamsChoose = btnChooseGif.getLayoutParams();
                layoutParamsChoose.height = 0;
                layoutParamsChoose.width = 0;
                btnChooseGif.setLayoutParams(layoutParamsChoose);
                ViewGroup.LayoutParams layoutParamsGif = recyclerViewGif.getLayoutParams();
                layoutParamsGif.height =ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParamsGif.width = (int)(400*dp);
                recyclerViewGif.setLayoutParams(layoutParamsGif);
            }
        });
        btnShowGhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ChatGroup> chatGroupCall = dataService.getGroupById(groupId);
                chatGroupCall.enqueue(new Callback<ChatGroup>() {
                    @Override
                    public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                        showGhim(response.body(),ChatBoxGroup.this,members,userCurrent);
                    }

                    @Override
                    public void onFailure(Call<ChatGroup> call, Throwable t) {

                    }
                });
            }
        });
        btnExitGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float dp = getApplicationContext().getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams layoutParams = btnChooseGif.getLayoutParams();
                layoutParams.height =(int)(50*dp);
                layoutParams.width = (int)(50*dp);
                btnChooseGif.setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParamsExit = btnExitGif.getLayoutParams();
                layoutParamsExit.height = 0;
                layoutParamsExit.width = 0;
                btnExitGif.setLayoutParams(layoutParamsExit);
                ViewGroup.LayoutParams layoutParamsGif = recyclerViewGif.getLayoutParams();
                layoutParamsGif.height =0;
                layoutParamsGif.width = 0;
                recyclerViewGif.setLayoutParams(layoutParamsGif);
                ViewGroup.LayoutParams layoutParamsTypeGif = recyclerViewTypeGif.getLayoutParams();
                layoutParamsTypeGif.height =0;
                layoutParamsTypeGif.width = 0;
                recyclerViewTypeGif.setLayoutParams(layoutParamsTypeGif);
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        btnGoToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(messages.size()-1);
                btnGoToBottom.setVisibility(View.INVISIBLE);
            }
        });
    }
    public static void getGhim(ChatGroup chatGroup,DataService service){
        List<Map<String,String>> pins = chatGroup.getPins();
        if(pins.size() <= 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
            viewGhim.setLayoutParams(layoutParams);
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(130));
            viewGhim.setLayoutParams(layoutParams);
            btnShowGhim.setText("+"+(pins.size()-1));
            Map<String,String> lastPin = pins.get(pins.size()-1);
            String messagePinId = lastPin.get("messageId");
            String userPinId = lastPin.get("userId");
            Call<Message> messageCall = service.getMessageById(messagePinId);
            messageCall.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Message message = response.body();
                    if(message.getMessageType().equals("text")){
                        tvMessageGhim.setText("[Tin nhắn] "+message.getText());
                    }else if(message.getMessageType().equals("file")){
                        tvMessageGhim.setText("[File] "+message.getFileName().substring(37));
                    }else if(message.getMessageType().equals("image")){
                        tvMessageGhim.setText("[Hình ảnh]");
                    }else if(message.getMessageType().equals("gif")){
                        tvMessageGhim.setText("[Gif]");
                    }
                    Call<UserDTO> userDTOCall = service.getUserById(message.getSenderId());
                    userDTOCall.enqueue(new Callback<UserDTO>() {
                        @Override
                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                            User senderMessage = response.body().getUser();
                            tvUsernameGhim.setText("Gửi bởi "+senderMessage.getUserName());
                        }

                        @Override
                        public void onFailure(Call<UserDTO> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {

                }
            });
        }
    }
    public static void showGhim(ChatGroup chatGroup,Context context,List<User> members,User userCurrent){
        Dialog dialogGhim =new Dialog(context);
        dialogGhim.setContentView(R.layout.dialog_ghim_message);
        Window window = dialogGhim.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialogGhim.setCancelable(true);
        Button btnClose = dialogGhim.findViewById(R.id.btnHiddenGhim);
        List<Map<String,String>> pins = chatGroup.getPins();
        RecyclerView recyclerViewGhim = dialogGhim.findViewById(R.id.recyclerview_ghim);
        messageGhimRecyclerAdapter = new MessageGhimRecyclerAdapter(pins,context,chatGroup,members,userCurrent);
        recyclerViewGhim.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewGhim.setAdapter(messageGhimRecyclerAdapter);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGhim.dismiss();
            }
        });
        dialogGhim.show();
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
                        message.setRead(true);
                        if(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("svg") || extension.equals("gif"))
                            message.setMessageType("image");
                        else if(extension.equals("mp4") || extension.equals("mkv") || extension.equals("avi") || extension.equals("webm"))
                            message.setMessageType("video");
                        else
                            message.setMessageType("file");
                        message.setCreatedAt(new Date().getTime());
                        message.setFileName(uuid+"."+file.getName());
                        Call<Message> messageCall = dataService.postMessage(message);
                        messageCall.enqueue(new Callback<Message>() {
                            @Override
                            public void onResponse(Call<Message> call, Response<Message> response) {
                                Message message1 = response.body();
                                List<Message> list = new ArrayList<>();
                                list.add(message1);
                                socket.sendFile(list,"true");
//                                messages.add(message1);
//                                adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
//                                recyclerView.setAdapter(adapter);
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
//                                linearLayoutManager.setStackFromEnd(true);
//                                recyclerView.setLayoutManager(linearLayoutManager);
//                                if (adapter.getItemCount()>0){
//                                    recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
//                                }
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
        btnChooseGif = findViewById(R.id.btnChooseGif);
        btnExitGif = findViewById(R.id.btnExitGif);
        tvMessageGhim = findViewById(R.id.tvMessageGhim);
        tvUsernameGhim = findViewById(R.id.tvUsernameGhim);
        btnShowGhim = findViewById(R.id.btnShowGhim);
        recyclerViewGif = findViewById(R.id.recyclerview_gif);
        viewGhim = findViewById(R.id.viewGhim);
        ViewGroup.LayoutParams layoutParams = btnExitGif.getLayoutParams();
        layoutParams.height = 0;
        layoutParams.width = 0;
        btnExitGif.setLayoutParams(layoutParams);
        recyclerViewTypeGif = findViewById(R.id.recyclerview_typeGif);
        ViewGroup.LayoutParams layoutParamsGif = recyclerViewGif.getLayoutParams();
        layoutParamsGif.height = 0;
        layoutParamsGif.width = 0;
        recyclerViewGif.setLayoutParams(layoutParamsGif);
        recyclerViewTypeGif.setLayoutParams(layoutParamsGif);
        recyclerViewGhim = findViewById(R.id.recyclerview_ghim);
        btnGoToBottom = findViewById(R.id.imgGoToBottomChatBoxGroup);
        btnGoToBottom.setVisibility(View.INVISIBLE);
    }
    public void getGifs(User userCurrent){
        List<Gif> gifs = new InitGif().addGif();
        gifAdapter = new GifRecyclerAdapter(ChatBoxGroup.this,gifs,groupId,userCurrent);
        recyclerViewGif.setLayoutManager(new LinearLayoutManager(ChatBoxGroup.this,RecyclerView.HORIZONTAL,false));
        recyclerViewGif.setAdapter(gifAdapter);
    }
    public void getTypeGifs(User userCurrent){
        List<TypeGif> gifs = new InitGif().addTypeGif();
        typeGifRecyclerAdapter = new TypeGifRecyclerAdapter(gifs,ChatBoxGroup.this,groupId,userCurrent);
        recyclerViewTypeGif.setLayoutManager(new LinearLayoutManager(ChatBoxGroup.this,RecyclerView.HORIZONTAL,false));
        recyclerViewTypeGif.setAdapter(typeGifRecyclerAdapter);
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
                                        updateIsRead(messages);
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
    private Emitter.Listener responseMessage = new Emitter.Listener() {
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
                        message.setRead(true);
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
    private Emitter.Listener responseAddFile = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    System.out.println(data+"");
                    JSONArray mess = null;
                    Message messObject = null;
                    try {
                        mess = data.getJSONArray("messages");
                        messObject = new Gson().fromJson(mess.get(0).toString(), Message.class);
                        messObject.setRead(true);
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
                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                    }
                }
            });
        }
    };
    private Emitter.Listener responseDeleteGroup = new Emitter.Listener() {
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
    private Emitter.Listener responseLeaveGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String chatGroupJsonObject = data.getString("group");
                        ChatGroup group = gson.fromJson(chatGroupJsonObject,ChatGroup.class);
                        chatGroup = group;
                        tvQuantityMember.setText(chatGroup.getMembers().size()+" thành viên");
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
                                adapter = new ChatBoxGroupRecyclerAdapter(messages, ChatBoxGroup.this,userCurrent, members);
                                recyclerView.setAdapter(adapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBoxGroup.this, LinearLayoutManager.VERTICAL, false);
                                linearLayoutManager.setStackFromEnd(true);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                if (adapter.getItemCount()>0){
                                    recyclerView.smoothScrollToPosition(messages.size() - 1);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatBoxGroup.this, Home.class));
        finish();
    }
}