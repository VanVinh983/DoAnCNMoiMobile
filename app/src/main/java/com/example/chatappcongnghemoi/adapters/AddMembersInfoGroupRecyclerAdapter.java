package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.AddGroupChat;
import com.example.chatappcongnghemoi.activities.InfoGroupChat;
import com.example.chatappcongnghemoi.activities.PersonalOfOthers;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMembersInfoGroupRecyclerAdapter extends RecyclerView.Adapter<AddMembersInfoGroupRecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<User> users;
    private List<User> members;
    public AddMembersInfoGroupRecyclerAdapter(Context context, ArrayList<User> users,List<User> members) {
        this.context = context;
        this.users = users;
        this.members = members;
    }

    @NonNull
    @Override
    public AddMembersInfoGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_add_group, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddMembersInfoGroupRecyclerAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        try {
            if (user.getAvatar() != null)
                Glide.with(context).load(user.getAvatar()).into(holder.avatar);
        } catch (NullPointerException e) {
            holder.avatar.setImageResource(R.drawable.avatar);
        }
        try {
            holder.txtName.setText(user.getUserName());
        } catch (NullPointerException e) {
            holder.txtName.setText("User Name");
        }
        holder.imgChosen.setVisibility(View.INVISIBLE);
        members.forEach((mem) ->{
            if(mem.getId().equals(user.getId())) {
                holder.itemView.setBackgroundResource(R.drawable.background_chat_default);
                holder.imgChosen.setVisibility(View.VISIBLE);
                holder.itemView.setEnabled(false);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                for(int i = 0 ; i< InfoGroupChat.listAddMembers.size() ; i++){
                    if(InfoGroupChat.listAddMembers.get(i).getId().equals(user.getId())){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    holder.imgChosen.setVisibility(View.VISIBLE);
                    InfoGroupChat.listAddMembers.add(user);
                }else{
                    holder.imgChosen.setVisibility(View.INVISIBLE);
                    InfoGroupChat.listAddMembers.remove(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView txtName;
        ImageView imgChosen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgFriend_ListFriend_AddGroup);
            txtName = itemView.findViewById(R.id.tvUserName_ListFriend_AddGroup);
            imgChosen = itemView.findViewById(R.id.choseFriend);
        }
    }
}
