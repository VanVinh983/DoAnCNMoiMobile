package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.AddGroupChat;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhoneBookRecyclerAdapterAddGroup extends RecyclerView.Adapter<PhoneBookRecyclerAdapterAddGroup.ViewHolder>{
    private Context context;
    private ArrayList<User> users;

    public PhoneBookRecyclerAdapterAddGroup(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public PhoneBookRecyclerAdapterAddGroup.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_add_group, parent, false);
        return new PhoneBookRecyclerAdapterAddGroup.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneBookRecyclerAdapterAddGroup.ViewHolder holder, int position) {
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
        holder.imgChosen.setVisibility(View.INVISIBLE);
        for(int i = 0; i< AddGroupChat.listFriendsClickAdd.size() ; i++){
            if(AddGroupChat.listFriendsClickAdd.get(i).getId().equals(user.getId()))
                holder.imgChosen.setVisibility(View.VISIBLE);
            else holder.imgChosen.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                for(int i = 0 ; i< AddGroupChat.listFriendsClickAdd.size() ; i++){
                    if(AddGroupChat.listFriendsClickAdd.get(i).getId().equals(user.getId())){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    holder.imgChosen.setVisibility(View.VISIBLE);
                    AddGroupChat.listFriendsClickAdd.add(user);
                    AddGroupChat.imageFriendsWhenClickAddGroupRecyclerAdapter = new ImageFriendsWhenClickAddGroupRecyclerAdapter(context.getApplicationContext(),AddGroupChat.listFriendsClickAdd);
                    AddGroupChat.recyclerViewImage.setAdapter(AddGroupChat.imageFriendsWhenClickAddGroupRecyclerAdapter);
                }else{
                    holder.imgChosen.setVisibility(View.INVISIBLE);
                    AddGroupChat.listFriendsClickAdd.remove(user);
                    AddGroupChat.imageFriendsWhenClickAddGroupRecyclerAdapter = new ImageFriendsWhenClickAddGroupRecyclerAdapter(context.getApplicationContext(),AddGroupChat.listFriendsClickAdd);
                    AddGroupChat.recyclerViewImage.setAdapter(AddGroupChat.imageFriendsWhenClickAddGroupRecyclerAdapter);
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
