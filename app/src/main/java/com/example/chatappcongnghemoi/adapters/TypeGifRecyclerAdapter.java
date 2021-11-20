package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.models.Gif;
import com.example.chatappcongnghemoi.models.InitGif;
import com.example.chatappcongnghemoi.models.TypeGif;
import com.example.chatappcongnghemoi.models.User;

import java.util.List;

public class TypeGifRecyclerAdapter extends RecyclerView.Adapter<TypeGifRecyclerAdapter.ViewHolder>{
    private List<TypeGif> typeGifs;
    private Context context;
    String selecting = "all";
    String groupId;
    User userCurrent;
    public TypeGifRecyclerAdapter(List<TypeGif> typeGifs, Context context,String groupId,User userCurrent) {
        this.typeGifs = typeGifs;
        this.context = context;
        this.groupId = groupId;
        this.userCurrent = userCurrent;
    }

    @NonNull
    @Override
    public TypeGifRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_type_gif, parent, false);
        return new TypeGifRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeGifRecyclerAdapter.ViewHolder holder, int position) {
        TypeGif typeGif = typeGifs.get(position);
        holder.btnTypyGif.setText(typeGif.getName());
        holder.btnTypyGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeGif.getType().equals("all")){
                    List<Gif> gifs = new InitGif().getGifsByType(typeGif.getType());
                    ChatBoxGroup.gifAdapter = new GifRecyclerAdapter(context,gifs,groupId,userCurrent);
                    ChatBoxGroup.recyclerViewGif.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                    ChatBoxGroup.recyclerViewGif.setAdapter(ChatBoxGroup.gifAdapter);
                }else{
                    List<Gif> gifs = new InitGif().addGif();
                    ChatBoxGroup.gifAdapter = new GifRecyclerAdapter(context,gifs,groupId,userCurrent);
                    ChatBoxGroup.recyclerViewGif.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
                    ChatBoxGroup.recyclerViewGif.setAdapter(ChatBoxGroup.gifAdapter);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeGifs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button btnTypyGif;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTypyGif = itemView.findViewById(R.id.btnTypeGif);
        }
    }
}
