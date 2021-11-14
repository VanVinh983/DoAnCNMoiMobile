package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.os.Handler;
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
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private Context context;
    private User userCurrent;
    private User friend;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static int message_type;

    public MessageAdapter(List<Message> messages, Context context, User userCurrent, User friend) {
        this.messages = messages;
        this.context = context;
        this.userCurrent = userCurrent;
        this.friend = friend;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message != null) {
            if (message.getText() == null) {
                holder.txt_content.setText(message.getFileName().toString());
            } else {
                holder.txt_content.setText(message.getText().toString());
            }

            Glide.with(context).load(userCurrent.getAvatar()).into(holder.avatar);
            if (message.getSenderId().equals(userCurrent.getId())){
                holder.username.setText(userCurrent.getUserName());
            }else {
                holder.username.setText(friend.getUserName());
            }
            if(new Date().getTime() - message.getCreatedAt() < 86400000){
                holder.time.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
            }
            else{
                String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
                holder.time.setText(date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_content,username, time;
        private CircleImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content = itemView.findViewById(R.id.txt_content_message);
            avatar = itemView.findViewById(R.id.image_message_avatar);
            username = itemView.findViewById(R.id.tvUsernameChatBoxLeft);
            time = itemView.findViewById(R.id.tvTimeSendLeft);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(userCurrent.getId())){
            message_type = RIGHT;
            return RIGHT;
        }else {
            message_type = LEFT;
            return LEFT;
        }
    }
}
