package com.example.chatappcongnghemoi.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.activities.Full_Image_Avatar;
import com.example.chatappcongnghemoi.activities.InfoGroupChat;
import com.example.chatappcongnghemoi.activities.Personal;
import com.example.chatappcongnghemoi.activities.StartApp;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;
import android.text.format.DateUtils.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxGroupRecyclerAdapter extends RecyclerView.Adapter<ChatBoxGroupRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private Context context;
    private User userCurrent;
    private List<User> members;
    User userByMessage;
    boolean isReceiver = true;
    DataService dataService;
    User sender = null;
    MessageSocket messageSocket;
    int positionSelect = -1;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int CENTER = 2;
    public ChatBoxGroupRecyclerAdapter(List<Message> messages, Context context, User userCurrent, List<User> members) {
        this.messages = messages;
        this.context = context;
        this.userCurrent = userCurrent;
        this.members = members;
    }

    @NonNull
    @Override
    public ChatBoxGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if (viewType==LEFT){
            view = inflater.inflate(R.layout.layout_chatbox_left,parent,false);
        }else if (viewType==RIGHT){
            view = inflater.inflate(R.layout.layout_chatbox_right,parent,false);
        }else if (viewType==CENTER){
            view = inflater.inflate(R.layout.layout_chatbox_center,parent,false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ChatBoxGroupRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Message message = messages.get(position);
        dataService = ApiService.getService();
        Call<UserDTO> call = dataService.getUserById(message.getSenderId());
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userByMessage =  response.body().getUser();
                Glide.with(context).load(userByMessage.getAvatar()).into(holder.avatar);
                holder.txt_username.setText(userByMessage.getUserName());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        Call<List<ChatGroup>> chatGroupCall = dataService.getChatGroupByUserId(userCurrent.getId());
        chatGroupCall.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                messageSocket = new MessageSocket(response.body(),userCurrent);
            }

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {

            }
        });

        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message != null) {
            if (message.getMessageType().equals("image")) {
                android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                layoutParams.width = 100;
                layoutParams.height = 100;
                holder.image_message.setLayoutParams(layoutParams);
                Glide.with(context).load(url_s3+message.getFileName()).into(holder.image_message);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0,0);
                holder.txt_content.setLayoutParams(layoutParams1);
                holder.gifImageView.setLayoutParams(layoutParams1);
                holder.image_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, Full_Image_Avatar.class);
                        intent.putExtra("url", url_s3+message.getFileName());
                        context.startActivity(intent);
                    }
                });
                android.view.ViewGroup.LayoutParams layoutParamsLoad = holder.img_download.getLayoutParams();
                layoutParamsLoad.width = 70;
                layoutParamsLoad.height = 70;
                holder.img_download.setLayoutParams(layoutParamsLoad);
                holder.img_download.setImageResource(R.drawable.download);
            } else if (message.getMessageType().equals("file")){
                holder.txt_content.setTypeface(holder.txt_content.getTypeface(), Typeface.ITALIC);
                String fileName = message.getFileName().substring(37);
                android.view.ViewGroup.LayoutParams layoutParamsContent = holder.txt_content.getLayoutParams();
                layoutParamsContent.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParamsContent.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.txt_content.setLayoutParams(layoutParamsContent);
                holder.txt_content.setText(fileName);
                android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                layoutParams.width = 100;
                layoutParams.height = 100;
                holder.image_message.setLayoutParams(layoutParams);
                holder.image_message.setImageResource(R.drawable.file);
                LinearLayout.LayoutParams layoutParamsGif = new LinearLayout.LayoutParams(0,0);
                holder.gifImageView.setLayoutParams(layoutParamsGif);
                android.view.ViewGroup.LayoutParams layoutParamsLoad = holder.img_download.getLayoutParams();
                layoutParamsLoad.width = 70;
                layoutParamsLoad.height = 70;
                holder.img_download.setLayoutParams(layoutParamsLoad);
                holder.img_download.setImageResource(R.drawable.download);
            }else if (message.getMessageType().equals("gif")){
                android.view.ViewGroup.LayoutParams layoutParamsGif = holder.gifImageView.getLayoutParams();
                layoutParamsGif.width = 400;
                layoutParamsGif.height = 350;
                holder.gifImageView.setLayoutParams(layoutParamsGif);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
                holder.txt_content.setLayoutParams(layoutParams);
                holder.image_message.setLayoutParams(layoutParams);
                holder.img_download.setLayoutParams(layoutParams);
                Glide.with(context).load(message.getFileName()).into(holder.gifImageView);
            }else {
//                android.view.ViewGroup.LayoutParams layoutParamsEmpty = holder.image_message.getLayoutParams();
//                layoutParamsEmpty.width = 0;
//                layoutParamsEmpty.height = 0;
//                holder.gifImageView.setLayoutParams(layoutParamsEmpty);
//                holder.image_message.setLayoutParams(layoutParamsEmpty);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
                holder.gifImageView.setLayoutParams(layoutParams);
                holder.image_message.setLayoutParams(layoutParams);
                holder.img_download.setLayoutParams(layoutParams);
                android.view.ViewGroup.LayoutParams layoutParamsContent = holder.txt_content.getLayoutParams();
                layoutParamsContent.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParamsContent.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.txt_content.setLayoutParams(layoutParamsContent);
                holder.txt_content.setText(message.getText());
            }
            if(new Date().getTime() - message.getCreatedAt() < 86400000){
                holder.txt_timeSend.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
            }
            else{

                holder.txt_timeSend.setText(android.text.format.DateFormat.format("hh:mm a, dd-MM-yyyy", message.getCreatedAt()));
            }
        }
        List<Map<String,String>> mapReact = message.getReaction();
        if(mapReact.size() > 0){
            holder.imgReaction.setVisibility(View.VISIBLE);
            String react = mapReact.get(mapReact.size()-1).get("react");
            if(react.equals("thich")){
                holder.imgReaction.setImageResource(R.drawable.like);
            }else if(react.equals("yeu")){
                holder.imgReaction.setImageResource(R.drawable.heart);
            }else if(react.equals("cuoi")){
                holder.imgReaction.setImageResource(R.drawable.laughing);
            }else if(react.equals("wow")){
                holder.imgReaction.setImageResource(R.drawable.wow);
            }else if(react.equals("khoc")){
                holder.imgReaction.setImageResource(R.drawable.crying);
            }else if(react.equals("gian")){
                holder.imgReaction.setImageResource(R.drawable.angry);
            }
            holder.txt_quantityReaction.setText(mapReact.size()+"");
        }else{
            holder.txt_quantityReaction.setText("");
            holder.imgReaction.setVisibility(View.INVISIBLE);
        }
        holder.img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
                String url = url_s3 + message.getFileName();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String title = URLUtil.guessFileName(url,null,null);
                request.setTitle(title);
                request.setDescription("Đang tải");
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie",cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,url);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
                Toast.makeText(context, "Đang tải", Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                positionSelect = position;
                if(!message.getSenderId().equals(userCurrent.getId())){
                    if(!message.getMessageType().equals("note")){
                        sendReactionForMessageMembers(userByMessage,message);
                    }
                }else{
                    if(!message.getMessageType().equals("note")){
                        sendReactionForMessagePersonal(userByMessage,message);
                    }
                }
                return  true;
            }
        });
    }
    public void sendReactionForMessageMembers(User sender,Message message){
        final Dialog dialog =new Dialog(context);
        dialog.setContentView(R.layout.dialog_reaction_of_message);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        CircleImageView avatar = dialog.findViewById(R.id.imgAvatarReaction);
        TextView username = dialog.findViewById(R.id.tvUsernameReaction);
        TextView content = dialog.findViewById(R.id.tvMessageReaction);
        TextView time = dialog.findViewById(R.id.tvTimeReaction);
        ImageView btnCopy = dialog.findViewById(R.id.btnCopyMessage);
        ImageView btnDeleteMessage = dialog.findViewById(R.id.btnDeleteMessage);
        TextView tvDeleteMessage = dialog.findViewById(R.id.tvDeleteMessage);
        ImageView btnGhim = dialog.findViewById(R.id.btnGhimMessage);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0,0);
        btnDeleteMessage.setLayoutParams(layoutParams1);
        tvDeleteMessage.setLayoutParams(layoutParams1);
        ImageButton imgLike = dialog.findViewById(R.id.imgLikeReaction);
        ImageButton imgLove = dialog.findViewById(R.id.imgLoveReaction);
        ImageButton imgHaha = dialog.findViewById(R.id.imgHahaReaction);
        ImageButton imgWow = dialog.findViewById(R.id.imgWowReaction);
        ImageButton imgCry = dialog.findViewById(R.id.imgCryReaction);
        ImageButton imgAngry = dialog.findViewById(R.id.imgAngryReaction);
        ImageView imgMessage =dialog.findViewById(R.id.imgMessageReaction);
        CircleImageView imgRemoveReaction = dialog.findViewById(R.id.imgRemoveReaction);
        Glide.with(context).load(sender.getAvatar()).into(avatar);
        username.setText(sender.getUserName());
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message.getMessageType().equals("image")) {
            android.view.ViewGroup.LayoutParams layoutParamsReaction = imgMessage.getLayoutParams();
            layoutParamsReaction.width = 100;
            layoutParamsReaction.height = 100;
            imgMessage.setLayoutParams(layoutParamsReaction);
            Glide.with(context).load(url_s3+message.getFileName()).into(imgMessage);
            content.setWidth(0);
            content.setHeight(0);
        } else if (message.getMessageType().equals("file")){
            content.setTextColor(Color.parseColor("#008ae6"));
            content.setTypeface(content.getTypeface(), Typeface.ITALIC);
            content.setText(message.getFileName().substring(message.getFileName().indexOf(".")+1));
        }else {
            content.setText(message.getText());
        }
        if(new Date().getTime() - message.getCreatedAt() < 86400000){
            time.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        }
        else{
            String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
            time.setText(date);
        }
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","thich");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgHaha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","cuoi");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","yeu");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgCry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","khoc");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgWow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","wow");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgAngry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","gian");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgRemoveReaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                for(int i=0;i<mapReact.size();i++){
                    if(mapReact.get(i).get("userId").equals(userCurrent.getId())){
                        mapReact.remove(i);
                    }
                }
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied Text",message.getText());
                clipboardManager.setPrimaryClip(clipData);
                dialog.dismiss();
                Toast.makeText(context, "Đã sao chép", Toast.LENGTH_SHORT).show();
            }
        });
        btnGhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ChatGroup> groupCall = dataService.getGroupById(message.getReceiverId());
                groupCall.enqueue(new Callback<ChatGroup>() {
                    @Override
                    public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                        ChatGroup chatGroup = response.body();
                        List<Map<String,String>> mapPins = chatGroup.getPins();
                        if(mapPins.size() == 3){
                            Toast.makeText(context, "Nhóm chat đã có 3 tin nhắn được ghim", Toast.LENGTH_SHORT).show();
                        }else{
                            Map<String,String> map =new HashMap<>();
                            map.put("messageId",message.getId());
                            map.put("userId",userCurrent.getId());
                            mapPins.add(map);
                            chatGroup.setPins(mapPins);
                            ChatBoxGroup.getGhim(chatGroup,dataService);
                            ChatBoxGroup.showGhim(chatGroup,context,members,userCurrent);
                            Call<ChatGroup> updateGroupCall = dataService.updateGroup(chatGroup.getId(),chatGroup);
                            updateGroupCall.enqueue(new Callback<ChatGroup>() {
                                @Override
                                public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {

                                }

                                @Override
                                public void onFailure(Call<ChatGroup> call, Throwable t) {

                                }
                            });

                            Message message1 = new Message();
                            message1.setCreatedAt(new Date().getTime());
                            message1.setMessageType("note");
                            message1.setReceiverId(chatGroup.getId());
                            message1.setSenderId(new DataLoggedIn(context).getUserIdLoggedIn());
                            message1.setText("Đã GHIM tin nhắn của "+sender.getUserName());
                            message1.setChatType("group");
                            Call<Message> messageCall = dataService.postMessage(message1);
                            messageCall.enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    Message message2 = response.body();
                                    messageSocket.sendMessage(message2,"true");
                                    Toast.makeText(context, "Ghim tin nhắn thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    ChatBoxGroup.messages.add(message2);
                                    ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                                    ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                    linearLayoutManager.setStackFromEnd(true);
                                    ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                                    if (ChatBoxGroup.adapter.getItemCount()>0){
                                        ChatBoxGroup.recyclerView.scrollToPosition(ChatBoxGroup.messages.size()-1);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatGroup> call, Throwable t) {

                    }
                });
            }
        });
        dialog.show();
    }
    public void sendReactionForMessagePersonal(User sender,Message message) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reaction_of_message);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT, layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        CircleImageView avatar = dialog.findViewById(R.id.imgAvatarReaction);
        TextView username = dialog.findViewById(R.id.tvUsernameReaction);
        TextView content = dialog.findViewById(R.id.tvMessageReaction);
        TextView time = dialog.findViewById(R.id.tvTimeReaction);
        ImageView imgMessage = dialog.findViewById(R.id.imgMessageReaction);
        ImageView btnDeleteMessage = dialog.findViewById(R.id.btnDeleteMessage);
        ImageView btnCopy = dialog.findViewById(R.id.btnCopyMessage);
        ImageView btnGhim = dialog.findViewById(R.id.btnGhimMessage);
        ImageButton imgLike = dialog.findViewById(R.id.imgLikeReaction);
        ImageButton imgLove = dialog.findViewById(R.id.imgLoveReaction);
        ImageButton imgHaha = dialog.findViewById(R.id.imgHahaReaction);
        ImageButton imgWow = dialog.findViewById(R.id.imgWowReaction);
        ImageButton imgCry = dialog.findViewById(R.id.imgCryReaction);
        ImageButton imgAngry = dialog.findViewById(R.id.imgAngryReaction);
        CircleImageView imgRemoveReaction = dialog.findViewById(R.id.imgRemoveReaction);
        Glide.with(context).load(sender.getAvatar()).into(avatar);
        username.setText(sender.getUserName());
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message.getMessageType().equals("image")) {
            android.view.ViewGroup.LayoutParams layoutParamsReaction = imgMessage.getLayoutParams();
            layoutParamsReaction.width = 100;
            layoutParamsReaction.height = 100;
            imgMessage.setLayoutParams(layoutParamsReaction);
            Glide.with(context).load(url_s3 + message.getFileName()).into(imgMessage);
            content.setWidth(0);
            content.setHeight(0);
        } else if (message.getMessageType().equals("file")) {
            content.setTextColor(Color.parseColor("#008ae6"));
            content.setTypeface(content.getTypeface(), Typeface.ITALIC);
            content.setText(message.getFileName().substring(message.getFileName().indexOf(".")+1));
        } else {
            content.setText(message.getText());
        }
        if (new Date().getTime() - message.getCreatedAt() < 86400000) {
            time.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        } else {
            String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
            time.setText(date);
        }
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","thich");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgHaha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","cuoi");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","yeu");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgCry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","khoc");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgWow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","wow");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgAngry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                Map<String,String> map = new HashMap<>();
                map.put("userId",userCurrent.getId());
                map.put("react","gian");
                mapReact.add(map);
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });

            }
        });
        imgRemoveReaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String,String>> mapReact = message.getReaction();
                for(int i=0;i<mapReact.size();i++){
                    if(mapReact.get(i).get("userId").equals(userCurrent.getId())){
                        mapReact.remove(i);
                    }
                }
                message.setReaction(mapReact);
                Call<Message> messageCall = dataService.updateMessage(message.getId(),message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messageSocket.sendReaction(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
        btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Message> callDelete = dataService.deleteMessage(message.getId());
                callDelete.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        ChatBoxGroup.messages.remove(positionSelect);
                        messageSocket.deleteMessage(message1);
                        ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                        ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        linearLayoutManager.setStackFromEnd(true);
                        ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                        if (ChatBoxGroup.adapter.getItemCount()>0){
                            ChatBoxGroup.recyclerView.scrollToPosition(positionSelect+1);
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
                dialog.dismiss();
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied Text",message.getText());
                clipboardManager.setPrimaryClip(clipData);
                dialog.dismiss();
                Toast.makeText(context, "Đã sao chép", Toast.LENGTH_SHORT).show();
            }
        });
        btnGhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ChatGroup> groupCall = dataService.getGroupById(message.getReceiverId());
                groupCall.enqueue(new Callback<ChatGroup>() {
                    @Override
                    public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                        ChatGroup chatGroup = response.body();
                        List<Map<String,String>> mapPins = chatGroup.getPins();
                        if(mapPins.size() == 3){
                            Toast.makeText(context, "Nhóm chat đã có 3 tin nhắn được ghim", Toast.LENGTH_SHORT).show();
                        }else{
                            Map<String,String> map =new HashMap<>();
                            map.put("messageId",message.getId());
                            map.put("userId",userCurrent.getId());
                            mapPins.add(map);
                            chatGroup.setPins(mapPins);
                            ChatBoxGroup.getGhim(chatGroup,dataService);
                            ChatBoxGroup.showGhim(chatGroup,context,members,userCurrent);
                            Call<ChatGroup> updateGroupCall = dataService.updateGroup(chatGroup.getId(),chatGroup);
                            updateGroupCall.enqueue(new Callback<ChatGroup>() {
                                @Override
                                public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {

                                }

                                @Override
                                public void onFailure(Call<ChatGroup> call, Throwable t) {

                                }
                            });

                            Message message1 = new Message();
                            message1.setCreatedAt(new Date().getTime());
                            message1.setMessageType("note");
                            message1.setReceiverId(chatGroup.getId());
                            message1.setSenderId(new DataLoggedIn(context).getUserIdLoggedIn());
                            message1.setText("Đã GHIM tin nhắn của "+sender.getUserName());
                            message1.setChatType("group");
                            Call<Message> messageCall = dataService.postMessage(message1);
                            messageCall.enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    Message message2 = response.body();
                                    messageSocket.sendMessage(message2,"true");
                                    Toast.makeText(context, "Ghim tin nhắn thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    ChatBoxGroup.messages.add(message2);
                                    ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                                    ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                    linearLayoutManager.setStackFromEnd(true);
                                    ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                                    if (ChatBoxGroup.adapter.getItemCount()>0){
                                        ChatBoxGroup.recyclerView.scrollToPosition(ChatBoxGroup.messages.size()-1);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatGroup> call, Throwable t) {

                    }
                });
            }
        });
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_message,img_download;
        private TextView txt_content,txt_username,txt_timeSend,txt_quantityReaction;
        private CircleImageView avatar,imgReaction;
        private ImageView btnOptions;
        private GifImageView gifImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content = itemView.findViewById(R.id.txt_content_message);
            avatar = itemView.findViewById(R.id.image_message_avatar);
            txt_username = itemView.findViewById(R.id.tvUsernameChatBoxLeft);
            txt_timeSend = itemView.findViewById(R.id.tvTimeSendLeft);
            txt_quantityReaction = itemView.findViewById(R.id.tvQuantityReaction);
            imgReaction = itemView.findViewById(R.id.imgReactionOfMessage);
            image_message = itemView.findViewById(R.id.image_message);
            gifImageView = itemView.findViewById(R.id.image_gif);
            img_download = itemView.findViewById(R.id.imgDownload);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getMessageType().equals("note")){
            return  CENTER;
        }else {
            if (messages.get(position).getSenderId().equals(userCurrent.getId())) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
    }
}
