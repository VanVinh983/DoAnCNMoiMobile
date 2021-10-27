package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.AddGroupChat;
import com.example.chatappcongnghemoi.activities.PersonalOfOthers;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRecyclerAdapterAddGroup extends RecyclerView.Adapter<FriendsRecyclerAdapterAddGroup.ViewHolder>{
    private Context context;
    private ArrayList<User> users;

    public FriendsRecyclerAdapterAddGroup(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FriendsRecyclerAdapterAddGroup.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_add_group, parent, false);
        return new FriendsRecyclerAdapterAddGroup.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecyclerAdapterAddGroup.ViewHolder holder, int position) {
        User user = users.get(position);
        try {
            if (user.getAvatar() != null)
                Picasso.get().load(user.getAvatar()).into(holder.avatar);
        } catch (NullPointerException e) {
            holder.avatar.setImageResource(R.drawable.avatar);
        }
        try {
            holder.txtName.setText(user.getUserName());
        } catch (NullPointerException e) {
            holder.txtName.setText("User Name");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupChat.listFriendsClickAdd.add(user);
                AddGroupChat.imageFriendsWhenClickAddGroupRecyclerAdapter = new ImageFriendsWhenClickAddGroupRecyclerAdapter(context.getApplicationContext(),AddGroupChat.listFriendsClickAdd);
                AddGroupChat.recyclerViewImage.setAdapter(AddGroupChat.imageFriendsWhenClickAddGroupRecyclerAdapter);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgFriend_ListFriend_AddGroup);
            txtName = itemView.findViewById(R.id.tvUserName_ListFriend_AddGroup);
        }
    }
}
