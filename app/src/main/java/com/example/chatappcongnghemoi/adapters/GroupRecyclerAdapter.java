package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<ChatGroup> chatGroups;

    public GroupRecyclerAdapter(Context context, List<ChatGroup> chatGroups) {
        this.context = context;
        this.chatGroups = chatGroups;
    }

    @NonNull
    @Override
    public GroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerAdapter.ViewHolder holder, int position) {
        holder.sizeGroup.setText(chatGroups.size() + " thành viên");
        holder.groupName.setText(chatGroups.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return chatGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView groupName,sizeGroup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatarGroup);
            groupName = itemView.findViewById(R.id.tvGroupName);
            sizeGroup = itemView.findViewById(R.id.tvSizeGroup);
        }
    }
}
