package com.example.chatappcongnghemoi.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.activities.FileSent;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageGhimRecyclerAdapter extends RecyclerView.Adapter<MessageGhimRecyclerAdapter.ViewHolder>{
    private List<Map<String,String>> messages;
    private Context context;
    private DataService dataService;
    private ChatGroup chatGroup;
    List<User> members;
    User userCurrent;
    List<Message> allMessages;
    public MessageGhimRecyclerAdapter(List<Map<String,String>> messages,Context context,ChatGroup chatGroup,List<User> members,User userCurrent) {
        this.messages = messages;
        this.context = context;
        this.chatGroup = chatGroup;
        this.members = members;
        this.userCurrent = userCurrent;
    }

    @NonNull
    @Override
    public MessageGhimRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ghim, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageGhimRecyclerAdapter.ViewHolder holder, int position) {
        Map<String,String> map = messages.get(position);
        dataService = ApiService.getService();
        String messageId = map.get("messageId");
        String userGhimId = map.get("userId");
        Call<Message> messageCall = dataService.getMessageById(messageId);
        messageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = response.body();
                Call<UserDTO> userDTOCall = dataService.getUserById(message.getSenderId());
                userDTOCall.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        User senderMessage = response.body().getUser();
                        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
                        Glide.with(context).load(url_s3+senderMessage.getAvatar()).into(holder.avatar);
                        holder.tvUsernameGhim.setText(senderMessage.getUserName()+" đã gửi tin nhắn");
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {

                    }
                });
                String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
                LinearLayout.LayoutParams layoutParamsEmpty = new LinearLayout.LayoutParams(0,0);
                if(message.getMessageType().equals("text")){
                    holder.imgMessage.setLayoutParams(layoutParamsEmpty);
                    holder.gifMessage.setLayoutParams(layoutParamsEmpty);
                    ViewGroup.LayoutParams layoutParams =  holder.tvMessage.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.tvMessage.setText("[Tin nhắn] "+message.getText());
                }else if(message.getMessageType().equals("file")){
                    holder.imgMessage.setLayoutParams(layoutParamsEmpty);
                    holder.gifMessage.setLayoutParams(layoutParamsEmpty);
                    ViewGroup.LayoutParams layoutParams =  holder.tvMessage.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.tvMessage.setText("[File]"+message.getFileName().substring(37));
                }
                else if(message.getMessageType().equals("image")){
                    holder.gifMessage.setLayoutParams(layoutParamsEmpty);
                    holder.tvMessage.setLayoutParams(layoutParamsEmpty);
                    float dp = context.getResources().getDisplayMetrics().density;
                    ViewGroup.LayoutParams layoutParams = holder.imgMessage.getLayoutParams();
                    layoutParams.height =(int)(100*dp);
                    layoutParams.width = (int)(100*dp);
                    holder.imgMessage.setLayoutParams(layoutParams);
                    Glide.with(context).load(url_s3+message.getFileName()).into(holder.imgMessage);
                }
                else if(message.getMessageType().equals("gif")){
                    holder.tvMessage.setLayoutParams(layoutParamsEmpty);
                    holder.imgMessage.setLayoutParams(layoutParamsEmpty);
                    float dp = context.getResources().getDisplayMetrics().density;
                    ViewGroup.LayoutParams layoutParams = holder.gifMessage.getLayoutParams();
                    layoutParams.height =(int)(100*dp);
                    layoutParams.width = (int)(100*dp);
                    holder.gifMessage.setLayoutParams(layoutParams);
                    Glide.with(context).load(message.getFileName()).into(holder.gifMessage);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        Call<UserDTO> userDTOCall1 =  dataService.getUserById(userGhimId);
        userDTOCall1.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                User userGhim = response.body().getUser();
//              Glide.with(context).load(url_s3+userGhim.getAvatar()).into(holder.avatar);
                holder.tvUsernameGhim.setText(userGhim.getUserName() + " đã ghim tin nhắn");
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        String[] items = {"","Đi đến","Xoá ghim"};
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.btnOption.setAdapter(adapter);
        holder.btnOption.setAutofillHints("***");
        holder.btnOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    ArrayList<Map<String,String>> mapMembers = chatGroup.getMembers();
                    List<String> listId = new ArrayList<>();
                    allMessages  = new ArrayList<>();
                    mapMembers.forEach(map ->{
                        listId.add(map.get("userId"));
                    });
                    for(int i = 0 ;i < listId.size();i++){
                        Call<List<Message>> call = dataService.getAllMessages(listId.get(i),chatGroup.getId());
                        call.enqueue(new Callback<List<Message>>() {
                            @Override
                            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                                List<Message> messages = response.body();
                                messages.forEach(mess ->{
                                    allMessages.add(mess);
                                });
                                }
                            @Override
                            public void onFailure(Call<List<Message>> call, Throwable t) {

                            }
                        });
                    }
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Collections.sort(allMessages, new Comparator<Message>() {
                                @Override
                                public int compare(Message o1, Message o2) {
                                    return Integer.valueOf(o1.getCreatedAt().compareTo(o2.getCreatedAt()));
                                }
                            });
                            ChatBoxGroup.count = allMessages.size();
                            ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(allMessages,context,userCurrent,members);
                            ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            linearLayoutManager.setStackFromEnd(true);
                            ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                            int position = 0;
                            if (ChatBoxGroup.adapter.getItemCount()>0){
                                for (int j = 0 ;j<allMessages.size();j++){
                                    if(allMessages.get(j).getId().equals(map.get("messageId"))){
                                        position = j;
                                        break;
                                    }
                                }
                                ChatBoxGroup.recyclerView.scrollToPosition(position);
                            }
                        }
                    },500);
                }else if(position==2){
                    List<Map<String,String>> mapPins = chatGroup.getPins();
                    for(int i = 0 ; i < mapPins.size() ;i++){
                        if(mapPins.get(i).get("messageId").equals(map.get("messageId"))){
                            mapPins.remove(i);
                        }
                    }
                    chatGroup.setPins(mapPins);
                    ChatBoxGroup.getGhim(chatGroup,dataService);
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
                    message1.setText("Đã BỎ GHIM tin nhắn của "+userCurrent.getUserName());
                    message1.setChatType("group");
                    Call<Message> messageCall = dataService.postMessage(message1);
                    messageCall.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            Message message2 = response.body();
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
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView tvUsernameGhim,tvUsernameSender,tvMessage;
        ImageView imgMessage;
        GifImageView gifMessage;
        Spinner btnOption;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatarGhim);
            tvUsernameGhim = itemView.findViewById(R.id.tvUsernameGhimShowMessage);
            tvUsernameSender = itemView.findViewById(R.id.tvUsernameSenderMessageGhim);
            tvMessage = itemView.findViewById(R.id.tvMessageGhimShowMessage);
            imgMessage = itemView.findViewById(R.id.imgMessageGhimShowMessage);
            gifMessage = itemView.findViewById(R.id.gifMessageGhimShowMessage);
            btnOption = itemView.findViewById(R.id.btnOptionGhim);
        }
    }
}
