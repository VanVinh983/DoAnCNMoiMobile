package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
import com.example.chatappcongnghemoi.models.Gif;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GifRecyclerAdapter extends RecyclerView.Adapter<GifRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<Gif> gifs;
    DataService dataService;
    String groupId;
    List<User> members = new ArrayList<>();
    User userCurrent;
    MessageSocket socket;
    public GifRecyclerAdapter(Context context, List<Gif> gifs,String groupId,User userCurrent) {
        this.context = context;
        this.gifs = gifs;
        this.groupId = groupId;
        this.userCurrent = userCurrent;
    }

    @NonNull
    @Override
    public GifRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gif, parent, false);
        return new GifRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GifRecyclerAdapter.ViewHolder holder, int position) {
        Gif gif = gifs.get(position);
        dataService = ApiService.getService();
        Glide.with(context).load(gif.getUrl()).into(holder.gif);
        Call<List<ChatGroup>> call = dataService.getChatGroupByUserId(userCurrent.getId());
        call.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                socket = new MessageSocket(response.body(),userCurrent);
            }

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {

            }
        });
        holder.gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                String userId = new DataLoggedIn(context).getUserIdLoggedIn();
                message.setSenderId(userId);
                message.setReceiverId(groupId);
                message.setChatType("group");
                message.setMessageType("gif");
                message.setCreatedAt(new Date().getTime());
                message.setFileName(gif.getUrl());
                Call<Message> messageCall = dataService.postMessage(message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        Call<ChatGroup> chatGroupCall = dataService.getGroupById(groupId);
                        chatGroupCall.enqueue(new Callback<ChatGroup>() {
                            @Override
                            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                                ArrayList<Map<String,String>> mapMembers = response.body().getMembers();
                                List<String> listId = new ArrayList<>();
                                mapMembers.forEach(map ->{
                                    listId.add(map.get("userId"));
                                });
                                listId.forEach(id ->{
                                    Call<UserDTO> userDTOCall = dataService.getUserById(id);
                                    userDTOCall.enqueue(new Callback<UserDTO>() {
                                        @Override
                                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                            members.add(response.body().getUser());
                                            if (members.size() == listId.size()){
                                                socket.sendMessage(message1,"true");
                                                ChatBoxGroup.messages.add(message1);
                                                ChatBoxGroup.adapter = new ChatBoxGroupRecyclerAdapter(ChatBoxGroup.messages, context,userCurrent, members);
                                                ChatBoxGroup.recyclerView.setAdapter(ChatBoxGroup.adapter);
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                                linearLayoutManager.setStackFromEnd(true);
                                                ChatBoxGroup.recyclerView.setLayoutManager(linearLayoutManager);
                                                if (ChatBoxGroup.adapter.getItemCount()>0){
                                                    ChatBoxGroup.recyclerView.scrollToPosition(ChatBoxGroup.messages.size() - 1);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserDTO> call, Throwable t) {

                                        }
                                    });
                                });
                            }

                            @Override
                            public void onFailure(Call<ChatGroup> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return gifs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        GifImageView gif;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gif = itemView.findViewById(R.id.gif);
        }
    }
}
