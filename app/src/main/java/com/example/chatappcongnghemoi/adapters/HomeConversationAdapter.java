package com.example.chatappcongnghemoi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBox;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.models.Conversation;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeConversationAdapter extends RecyclerView.Adapter<HomeConversationAdapter.ViewHolder> {
    private List<Conversation> conversations;
    private Context context;

    public HomeConversationAdapter(List<Conversation> conversations, Context context) {
        this.conversations = conversations;
        this.context = context;
        Collections.sort(conversations, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation conversation1, Conversation conversation2) {
                return (conversation2.getNewMessage().getCreatedAt().compareTo(conversation1.getNewMessage().getCreatedAt()));
            }
        });
    }

    @NonNull
    @Override
    public HomeConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_user_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeConversationAdapter.ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        String userid = new DataLoggedIn(context).getUserIdLoggedIn();
        if (conversation.getChatGroup()==null) {
            Glide.with(context).load(conversation.getFriend().getAvatar()).into(holder.avatar);
            holder.txt_name.setText(conversation.getFriend().getUserName());
            String mess = null;
            if (conversation.getNewMessage().getSenderId().equals(userid)){
                if (conversation.getNewMessage().getMessageType().equals("image")){
                    mess = "Bạn: đã gửi ảnh";
                }else if (conversation.getNewMessage().getMessageType().equals("file")){
                    mess = "Bạn: đã gửi file";
                }else if (conversation.getNewMessage().getMessageType().equals("gif")){
                    mess = "Bạn: đã gửi ảnh động";
                }else {
                    mess = "Bạn: "+ conversation.getNewMessage().getText();
                }
            }else {
                if (conversation.getNewMessage().getMessageType().equals("image")){
                    mess = conversation.getFriend().getUserName()+": đã gửi ảnh";
                }else if (conversation.getNewMessage().getMessageType().equals("file")){
                    mess = conversation.getFriend().getUserName()+": đã gửi file";
                }else if (conversation.getNewMessage().getMessageType().equals("gif")){
                    mess = conversation.getFriend().getUserName()+": đã gửi ảnh động";
                }else {
                    mess = conversation.getFriend().getUserName()+": "+ conversation.getNewMessage().getText();
                }
            }
            if (conversation.getNewMessage().isRead()==false) {
                holder.txt_message.setTextColor(Color.BLACK);
                holder.txt_message.setTypeface(holder.txt_message.getTypeface(), Typeface.BOLD);
            }
            holder.txt_message.setText(mess);
        } else {
            Glide.with(context).load(conversation.getChatGroup().getAvatar()).into(holder.avatar);
            holder.txt_name.setText(conversation.getChatGroup().getName());
            String mess = null;
            if (conversation.getNewMessage().getSenderId().equals(userid)){
                if (conversation.getNewMessage().getMessageType().equals("image")){
                    mess = "Bạn: đã gửi ảnh";
                }else if (conversation.getNewMessage().getMessageType().equals("file")){
                    mess = "Bạn: đã gửi file";
                }else if (conversation.getNewMessage().getMessageType().equals("gif")){
                    mess = "Bạn: đã gửi ảnh động";
                }else {
                    mess = "Bạn: "+ conversation.getNewMessage().getText();
                }
            }else {
                if (conversation.getNewMessage().getMessageType().equals("image")){
                    mess = "Nhóm: đã gửi ảnh";
                }else if (conversation.getNewMessage().getMessageType().equals("file")){
                    mess = "Nhóm: đã gửi file";
                }else if (conversation.getNewMessage().getMessageType().equals("gif")){
                    mess = "Nhóm: đã gửi ảnh động";
                }else {
                    mess = "Nhóm: "+ conversation.getNewMessage().getText();
                }
            }
            if (conversation.getNewMessage().isRead()==false) {
                holder.txt_message.setTextColor(Color.BLACK);
                holder.txt_message.setTypeface(holder.txt_message.getTypeface(), Typeface.BOLD);
            }
            holder.txt_message.setText(mess);
        }
        if(new Date().getTime() - conversation.getNewMessage().getCreatedAt() < 86400000){
            holder.txt_time.setText(DateUtils.getRelativeTimeSpanString(conversation.getNewMessage().getCreatedAt()));
        }
        else{
            String date = DateFormat.getDateFormat(context).format(conversation.getNewMessage().getCreatedAt());
            holder.txt_time.setText(date);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conversation.getChatGroup()==null){
                    Intent intent = new Intent(context, ChatBox.class);
                    intent.putExtra("friendId", conversation.getFriend().getId());
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }else {
                    Intent intent = new Intent(context, ChatBoxGroup.class);
                    intent.putExtra("groupId", conversation.getChatGroup().getId());
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView avatar;
        private TextView txt_name, txt_message,txt_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_avatart_home_item);
            txt_name = itemView.findViewById(R.id.txt_home_item_user_name);
            txt_message = itemView.findViewById(R.id.txt_home_item_first_message);
            txt_time = itemView.findViewById(R.id.txt_home_time_message);

        }
    }
}
