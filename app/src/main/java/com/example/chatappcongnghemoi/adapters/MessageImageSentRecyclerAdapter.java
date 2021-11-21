package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;

import java.util.Date;
import java.util.List;

public class MessageImageSentRecyclerAdapter extends RecyclerView.Adapter<MessageImageSentRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private Context context;
    public MessageImageSentRecyclerAdapter(Context context,List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageImageSentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_message_sent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageImageSentRecyclerAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(new Date().getTime() - message.getCreatedAt() < 86400000){
            holder.tvTimeImageSent.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        }
        else{

            holder.tvTimeImageSent.setText(android.text.format.DateFormat.format("hh:mm a, dd-MM-yyyy", message.getCreatedAt()));
        }
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        Glide.with(context).load(url_s3+message.getFileName()).into(holder.imgSent);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeImageSent;
        ImageView imgSent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeImageSent = itemView.findViewById(R.id.tvTimeImageSent);
            imgSent = itemView.findViewById(R.id.imgSent);
        }
    }
}
