package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBox;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.models.Gif;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;

import java.util.Date;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GifForChatBoxAdapter extends RecyclerView.Adapter<GifForChatBoxAdapter.ViewHolder> {
    private List<Gif> gifs;
    private User userCurrent;
    private User friend;
    private DataService dataService;
    private MessageSocket socket;
    private Context context;

    public GifForChatBoxAdapter(List<Gif> gifs, User userCurrent, User friend, DataService dataService, MessageSocket socket, Context context) {
        this.gifs = gifs;
        this.userCurrent = userCurrent;
        this.friend = friend;
        this.dataService = dataService;
        this.socket = socket;
        this.context = context;
    }

    @NonNull
    @Override
    public GifForChatBoxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GifForChatBoxAdapter.ViewHolder holder, int position) {
        Gif gif = gifs.get(position);
        Glide.with(context).load(gif.getUrl()).into(holder.gif);
        holder.gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                String userId = userCurrent.getId();
                message.setSenderId(userId);
                message.setReceiverId(friend.getId());
                message.setChatType("personal");
                message.setMessageType("gif");
                message.setCreatedAt(new Date().getTime());
                message.setFileName(gif.getUrl());
                Call<Message> messageCall = dataService.postMessage(message);
                messageCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.body()!=null){
                            socket.sendMessage(message, userCurrent.getId());
                            ChatBox.messages.add(message);
                            ChatBox.messageAdapter = new MessageAdapter(ChatBox.messages, context, userCurrent, friend);
                            ChatBox.recyclerViewMessage.setAdapter(ChatBox.messageAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            linearLayoutManager.setStackFromEnd(true);
                            ChatBox.recyclerViewMessage.setLayoutManager(linearLayoutManager);
                            if (ChatBox.messageAdapter.getItemCount()>0){
                                ChatBox.recyclerViewMessage.scrollToPosition(ChatBoxGroup.messages.size() - 1);
                            }
                        }
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        GifImageView gif;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gif = itemView.findViewById(R.id.gif);
        }
    }
}
