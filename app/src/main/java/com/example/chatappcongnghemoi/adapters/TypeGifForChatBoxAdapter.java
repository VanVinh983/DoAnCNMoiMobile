package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBox;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.models.Gif;
import com.example.chatappcongnghemoi.models.InitGif;
import com.example.chatappcongnghemoi.models.TypeGif;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;

import java.lang.reflect.Type;
import java.util.List;

public class TypeGifForChatBoxAdapter extends RecyclerView.Adapter<TypeGifForChatBoxAdapter.ViewHolder> {
    private List<TypeGif> typeGifs;
    private User userCurrent;
    private User friend;
    private DataService dataService;
    private MessageSocket socket;
    private Context context;

    public TypeGifForChatBoxAdapter(List<TypeGif> typeGifs, User userCurrent, User friend, DataService dataService, MessageSocket socket, Context context) {
        this.typeGifs = typeGifs;
        this.userCurrent = userCurrent;
        this.friend = friend;
        this.dataService = dataService;
        this.socket = socket;
        this.context = context;
    }

    @NonNull
    @Override
    public TypeGifForChatBoxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_type_gif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeGifForChatBoxAdapter.ViewHolder holder, int position) {
        TypeGif typeGif = typeGifs.get(position);
        holder.btnTypyGif.setText(typeGif.getName());
        holder.btnTypyGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeGif.getType().equals("all")){
                    List<Gif> gifs = new InitGif().getGifsByType(typeGif.getType());
                    ChatBox.gifForChatBoxAdapter = new GifForChatBoxAdapter(gifs, userCurrent, friend, dataService, socket, context);
                    ChatBox.recyclerViewgif.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                    ChatBox.recyclerViewgif.setAdapter(ChatBox.gifForChatBoxAdapter);
                }else{
                    List<Gif> gifs = new InitGif().addGif();
                    ChatBox.gifForChatBoxAdapter = new GifForChatBoxAdapter(gifs, userCurrent, friend, dataService, socket, context);
                    ChatBox.recyclerViewgif.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                    ChatBox.recyclerViewgif.setAdapter(ChatBox.gifForChatBoxAdapter);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeGifs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button btnTypyGif;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTypyGif = itemView.findViewById(R.id.btnTypeGif);
        }
    }
}
