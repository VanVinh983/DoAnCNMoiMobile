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
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBoxGroupRecyclerAdapter extends RecyclerView.Adapter<ChatBoxGroupRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private Context context;
    private User userCurrent;
    private User sender;
    private List<User> members;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    public ChatBoxGroupRecyclerAdapter(List<Message> messages, Context context, User userCurrent, List<User> members) {
        this.messages = messages;
        this.context = context;
        this.userCurrent = userCurrent;
        this.members = members;
    }

    @NonNull
    @Override
    public ChatBoxGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if (viewType==LEFT){
            view = inflater.inflate(R.layout.layout_chatbox_left,parent,false);
        }else if (viewType==RIGHT){
            view = inflater.inflate(R.layout.layout_chatbox_right,parent,false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxGroupRecyclerAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        members.forEach((user) ->{
            if(user.getId() == message.getSenderId()) {
                 sender = user;
            }
        });
        if (message != null) {
            if (message.getText() == null) {
                holder.txt_content.setText(message.getFileName().toString());
            } else {
                holder.txt_content.setText(message.getText().toString());
            }
            Glide.with(context).load(sender.getAvatar()).into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_content;
        private CircleImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content = itemView.findViewById(R.id.txt_content_message);
            avatar = itemView.findViewById(R.id.image_message_avatar);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(userCurrent.getId())){
            return RIGHT;
        }else {
            return LEFT;
        }
    }
}
