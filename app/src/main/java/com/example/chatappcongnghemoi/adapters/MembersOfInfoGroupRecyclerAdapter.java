package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersOfInfoGroupRecyclerAdapter extends RecyclerView.Adapter<MembersOfInfoGroupRecyclerAdapter.ViewHolder> {
    List<User> members;
    Context context;
    ChatGroup chatGroup;
    public MembersOfInfoGroupRecyclerAdapter(List<User> members, Context context,ChatGroup chatGroup) {
        this.members = members;
        this.context = context;
        this.chatGroup = chatGroup;
    }

    @NonNull
    @Override
    public MembersOfInfoGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_of_group,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersOfInfoGroupRecyclerAdapter.ViewHolder holder, int position) {
        User user = members.get(position);
        members.forEach((u) ->{
            if(user.getId().equals(chatGroup.getUserId()))
                holder.leader.setText("Trường nhóm");
        });
        Glide.with(context).load(user.getAvatar()).into(holder.avatar);
        holder.username.setText(user.getUserName());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView username,leader;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatarMemberInfoGroup);
            username = itemView.findViewById(R.id.tvUserNameInfoGroup);
            leader = itemView.findViewById(R.id.tvLeaderInfoGroup);
        }
    }
}
