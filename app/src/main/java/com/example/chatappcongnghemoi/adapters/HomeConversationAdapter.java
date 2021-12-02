package com.example.chatappcongnghemoi.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBox;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.activities.Full_Image_Avatar;
import com.example.chatappcongnghemoi.activities.Home;
import com.example.chatappcongnghemoi.activities.Personal;
import com.example.chatappcongnghemoi.models.Conversation;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.google.android.gms.common.api.Api;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                opendialogConversation(Gravity.CENTER, conversation);
                return true;
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
    private void opendialogConversation(int gravity, Conversation conversation){
        MessageSocket socket = new MessageSocket();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_home_conversation);

        Window window = dialog.getWindow();
        if (window==null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        }else {
            dialog.setCancelable(false);
        }
        TextView txtname = dialog.findViewById(R.id.txt_name_dialog_conversation);
        Button btnXoa = dialog.findViewById(R.id.btn_dialog_conversation_xoa);
        String reveicerid = null;
        String userid = new DataLoggedIn(context).getUserIdLoggedIn();
        if (conversation.getFriend()!=null){
            txtname.setText(conversation.getFriend().getUserName());
            reveicerid = conversation.getFriend().getId();
        }else {
            txtname.setText(conversation.getChatGroup().getName());
            reveicerid = conversation.getChatGroup().getId();
        }
        String finalReveicerid = reveicerid;
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataService dataService = ApiService.getService();
                Call<List<Message>> listCall = dataService.getAllMessage();
                listCall.enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        List<Message> list = response.body();
                        for (Message message: list) {
                            if (conversation.getFriend()!=null){
                                if (message.getSenderId().equals(userid) && message.getReceiverId().equals(finalReveicerid) ){
                                    Call<Message> messageCall = dataService.deleteMessage(message.getId());
                                    messageCall.enqueue(new Callback<Message>() {
                                        @Override
                                        public void onResponse(Call<Message> call, Response<Message> response) {
                                            socket.deleteMessage(message);
                                            Intent intent = new Intent(context, Home.class);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        }

                                        @Override
                                        public void onFailure(Call<Message> call, Throwable t) {

                                        }
                                    });
                                }
                                if (message.getSenderId().equals(finalReveicerid) && message.getReceiverId().equals(userid)){
                                    Call<Message> messageCall = dataService.deleteMessage(message.getId());
                                    messageCall.enqueue(new Callback<Message>() {
                                        @Override
                                        public void onResponse(Call<Message> call, Response<Message> response) {
                                            socket.deleteMessage(message);
                                            Intent intent = new Intent(context, Home.class);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        }

                                        @Override
                                        public void onFailure(Call<Message> call, Throwable t) {

                                        }
                                    });
                                }
                            }else {
                                if (message.getReceiverId().equals(conversation.getChatGroup().getId())||message.getSenderId().equals(conversation.getChatGroup().getId())){
                                    System.out.println("có nhận");
                                    Call<Message> messageCall = dataService.deleteMessage(message.getId());
                                    messageCall.enqueue(new Callback<Message>() {
                                        @Override
                                        public void onResponse(Call<Message> call, Response<Message> response) {
                                            socket.deleteMessage(message);
                                            Intent intent = new Intent(context, Home.class);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        }

                                        @Override
                                        public void onFailure(Call<Message> call, Throwable t) {

                                        }
                                    });
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {

                    }
                });

            }
        });

        dialog.show();
    }
}
