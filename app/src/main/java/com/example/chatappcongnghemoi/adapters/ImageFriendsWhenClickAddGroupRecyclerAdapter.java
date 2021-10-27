package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageFriendsWhenClickAddGroupRecyclerAdapter extends RecyclerView.Adapter<ImageFriendsWhenClickAddGroupRecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<User> users;
    public ImageFriendsWhenClickAddGroupRecyclerAdapter(Context context,ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public ImageFriendsWhenClickAddGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_friend_click_addgroup, parent, false);
        return new ImageFriendsWhenClickAddGroupRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFriendsWhenClickAddGroupRecyclerAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        Picasso.get().load(user.getAvatar()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgFriend_Click_AddGroup);
        }
    }
}
