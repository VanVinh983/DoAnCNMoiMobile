package com.example.chatappcongnghemoi.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.Personal;
import com.example.chatappcongnghemoi.activities.StartApp;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;
import android.text.format.DateUtils.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBoxGroupRecyclerAdapter extends RecyclerView.Adapter<ChatBoxGroupRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private Context context;
    private User userCurrent;
    private List<User> members;
    boolean isReceiver = true;
    User sender = null;
    List<String> listKey = new ArrayList<>();
    List<String> listValue = new ArrayList<>();
    int haha = 0;
    int like = 0;
    int love = 0;
    int cry = 0;
    int wow = 0;
    int angry = 0;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int CENTER = 2;
    DatabaseReference database;
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
        }else if (viewType==CENTER){
            view = inflater.inflate(R.layout.layout_chatbox_center,parent,false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxGroupRecyclerAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        database = FirebaseDatabase.getInstance().getReference("reaction");
        members.forEach((user) ->{
            if(user.getId().equals(message.getSenderId())) {
                Glide.with(context).load(user.getAvatar()).into(holder.avatar);
                holder.txt_username.setText(user.getUserName().toString());
                sender = user;
            }
        });
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> tempKey = new ArrayList<>();
                List<String> tempValue = new ArrayList<>();
                for(DataSnapshot snap : snapshot.child(message.getId()).getChildren()){
                    if(snap.getValue() == null){

                    }else{
                        tempKey.add(snap.getKey());
                        tempValue.add(snap.getValue().toString());
                        if(snapshot.child(message.getId()).getChildrenCount() == tempKey.size()){
                            listKey.addAll(tempKey);
                            listValue.addAll(tempValue);
                            tempKey.removeAll(tempKey);
                            tempValue.removeAll(tempValue);
                            if(listKey.size() == 0){
                                holder.txt_quantityReaction.setText("");
                            }else{
                                holder.txt_quantityReaction.setText(listKey.size()+"");
                                listValue.forEach((react) -> {
                                    if(react.equals("haha"))
                                        haha++;
                                    else if(react.equals("like"))
                                        like++;
                                    else if(react.equals("love"))
                                        love++;
                                    else if(react.equals("wow"))
                                        wow++;
                                    else if(react.equals("cry"))
                                        cry++;
                                    else if(react.equals("angry"))
                                        angry++;
                                });
                                if(like == 0){
                                    holder.imgReaction1.setVisibility(View.INVISIBLE);
                                }else{
                                    holder.imgReaction1.setImageResource(R.drawable.like);
                                }
                                if(love == 0){
                                    holder.imgReaction2.setVisibility(View.INVISIBLE);
                                }else{
                                    holder.imgReaction2.setImageResource(R.drawable.heart);
                                }
                                if(haha == 0){
                                    holder.imgReaction3.setVisibility(View.INVISIBLE);
                                }else{
                                    holder.imgReaction3.setImageResource(R.drawable.laughing);
                                }
                                if(wow == 0){
                                    holder.imgReaction4.setVisibility(View.INVISIBLE);
                                }else{
                                    holder.imgReaction4.setImageResource(R.drawable.wow);
                                }
                                if(cry == 0){
                                    holder.imgReaction5.setVisibility(View.INVISIBLE);
                                }else{
                                    holder.imgReaction5.setImageResource(R.drawable.crying);
                                }
                                if(angry == 0){
                                    holder.imgReaction6.setVisibility(View.INVISIBLE);
                                }else{
                                    holder.imgReaction6.setImageResource(R.drawable.angry);
                                }
                            }
                        }
                        listKey.removeAll(listKey);
                        listValue.removeAll(listValue);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (message != null) {
            if (message.getText() == null) {
                holder.txt_content.setText(message.getFileName().toString());
            } else {
                holder.txt_content.setText(message.getText().toString());
            }
            if(new Date().getTime() - message.getCreatedAt() < 86400000){
                holder.txt_timeSend.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
            }
            else{
                String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
                holder.txt_timeSend.setText(date);
            }
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if(!message.getSenderId().equals(userCurrent.getId())){
                    if(!message.getMessageType().equals("note")){
                        sendReactionForMessage(sender,message);
                    }
//                }
                return  true;
            }
        });
    }
    public void sendReactionForMessage(User sender,Message message){
        final Dialog dialog =new Dialog(context);
        dialog.setContentView(R.layout.dialog_reaction_of_message);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        CircleImageView avatar = dialog.findViewById(R.id.imgAvatarReaction);
        TextView username = dialog.findViewById(R.id.tvUsernameReaction);
        TextView content = dialog.findViewById(R.id.tvMessageReaction);
        TextView time = dialog.findViewById(R.id.tvTimeReaction);
        ImageButton imgLike = dialog.findViewById(R.id.imgLikeReaction);
        ImageButton imgLove = dialog.findViewById(R.id.imgLoveReaction);
        ImageButton imgHaha = dialog.findViewById(R.id.imgHahaReaction);
        ImageButton imgWow = dialog.findViewById(R.id.imgWowReaction);
        ImageButton imgCry = dialog.findViewById(R.id.imgCryReaction);
        ImageButton imgAngry = dialog.findViewById(R.id.imgAngryReaction);
        Glide.with(context).load(sender.getAvatar()).into(avatar);
        username.setText(sender.getUserName());
        content.setText(message.getText());
        if(new Date().getTime() - message.getCreatedAt() < 86400000){
            time.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        }
        else{
            String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
            time.setText(date);
        }
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.child(message.getId()).getChildren()) {
                    if (snap.getValue() == null) {

                    } else { ;
                        if(snap.getKey().equals(userCurrent.getId())){
                            if(snap.getValue().toString().equals("like")){
                                imgLike.setBackgroundResource(R.drawable.background_message_center);
                            }else if(snap.getValue().toString().equals("love"))
                                imgLove.setBackgroundResource(R.drawable.background_message_center);
                            else if(snap.getValue().toString().equals("haha"))
                                imgHaha.setBackgroundResource(R.drawable.background_message_center);
                            else if(snap.getValue().toString().equals("wow"))
                                imgWow.setBackgroundResource(R.drawable.background_message_center);
                            else if(snap.getValue().toString().equals("cry"))
                                imgCry.setBackgroundResource(R.drawable.background_message_center);
                            else if(snap.getValue().toString().equals("angry"))
                                imgAngry.setBackgroundResource(R.drawable.background_message_center);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_content,txt_username,txt_timeSend,txt_quantityReaction;
        private CircleImageView avatar,imgReaction1,imgReaction2,imgReaction3,imgReaction6,imgReaction4,imgReaction5;
        private ImageView btnOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content = itemView.findViewById(R.id.txt_content_message);
            avatar = itemView.findViewById(R.id.image_message_avatar);
            txt_username = itemView.findViewById(R.id.tvUsernameChatBoxLeft);
            txt_timeSend = itemView.findViewById(R.id.tvTimeSendLeft);
            txt_quantityReaction = itemView.findViewById(R.id.tvQuantityReaction);
            imgReaction1 = itemView.findViewById(R.id.imgReactionOfMessage1);
            imgReaction2 = itemView.findViewById(R.id.imgReactionOfMessage2);
            imgReaction3 = itemView.findViewById(R.id.imgReactionOfMessage3);
            imgReaction4 = itemView.findViewById(R.id.imgReactionOfMessage4);
            imgReaction5 = itemView.findViewById(R.id.imgReactionOfMessage5);
            imgReaction6 = itemView.findViewById(R.id.imgReactionOfMessage6);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getMessageType().equals("note")){
            return  CENTER;
        }else {
            if (messages.get(position).getSenderId().equals(userCurrent.getId())) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
    }
}
