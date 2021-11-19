package com.example.chatappcongnghemoi.adapters;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.activities.Full_Image_Avatar;
import com.example.chatappcongnghemoi.activities.Personal;
import com.example.chatappcongnghemoi.activities.StartApp;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxGroupRecyclerAdapter extends RecyclerView.Adapter<ChatBoxGroupRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private Context context;
    private User userCurrent;
    private List<User> members;
    User userByMessage;
    boolean isReceiver = true;
    DataService dataService;
    User sender = null;
    MessageSocket messageSocket;
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
        dataService = ApiService.getService();
        Call<UserDTO> call = dataService.getUserById(message.getSenderId());
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userByMessage =  response.body().getUser();
                Glide.with(context).load(userByMessage.getAvatar()).into(holder.avatar);
                holder.txt_username.setText(userByMessage.getUserName());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        Call<List<ChatGroup>> chatGroupCall = dataService.getChatGroupByUserId(userCurrent.getId());
        chatGroupCall.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                messageSocket = new MessageSocket(response.body(),userCurrent);
            }

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {

            }
        });
        database = FirebaseDatabase.getInstance().getReference("reaction");


//        members.forEach((user) ->{
//            if(user.getId().equals(message.getSenderId())) {
//                Glide.with(context).load(user.getAvatar()).into(holder.avatar);
//                holder.txt_username.setText(user.getUserName().toString());
//                sender = user;
//            }
//        });
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<String> tempKey = new ArrayList<>();
//                List<String> tempValue = new ArrayList<>();
//                for(DataSnapshot snap : snapshot.child(message.getId()).getChildren()){
//                    if(snap.getValue() == null){
//
//                    }else{
//                        tempKey.add(snap.getKey());
//                        tempValue.add(snap.getValue().toString());
//                        if(snapshot.child(message.getId()).getChildrenCount() == tempKey.size()){
//                            listKey.addAll(tempKey);
//                            listValue.addAll(tempValue);
//                            tempKey.removeAll(tempKey);
//                            tempValue.removeAll(tempValue);
//                            if(listKey.size() == 0){
//                                holder.txt_quantityReaction.setText("");
//                            }else{
//                                holder.txt_quantityReaction.setText(listKey.size()+"");
//                                listValue.forEach((react) -> {
//                                    if(react.equals("haha"))
//                                        haha++;
//                                    else if(react.equals("like"))
//                                        like++;
//                                    else if(react.equals("love"))
//                                        love++;
//                                    else if(react.equals("wow"))
//                                        wow++;
//                                    else if(react.equals("cry"))
//                                        cry++;
//                                    else if(react.equals("angry"))
//                                        angry++;
//                                });
//                                if(like == 0){
//                                    holder.imgReaction1.setVisibility(View.INVISIBLE);
//                                }else{
//                                    holder.imgReaction1.setImageResource(R.drawable.like);
//                                }
//                                if(love == 0){
//                                    holder.imgReaction2.setVisibility(View.INVISIBLE);
//                                }else{
//                                    holder.imgReaction2.setImageResource(R.drawable.heart);
//                                }
//                                if(haha == 0){
//                                    holder.imgReaction3.setVisibility(View.INVISIBLE);
//                                }else{
//                                    holder.imgReaction3.setImageResource(R.drawable.laughing);
//                                }
//                                if(wow == 0){
//                                    holder.imgReaction4.setVisibility(View.INVISIBLE);
//                                }else{
//                                    holder.imgReaction4.setImageResource(R.drawable.wow);
//                                }
//                                if(cry == 0){
//                                    holder.imgReaction5.setVisibility(View.INVISIBLE);
//                                }else{
//                                    holder.imgReaction5.setImageResource(R.drawable.crying);
//                                }
//                                if(angry == 0){
//                                    holder.imgReaction6.setVisibility(View.INVISIBLE);
//                                }else{
//                                    holder.imgReaction6.setImageResource(R.drawable.angry);
//                                }
//                            }
//                        }
//                        listKey.removeAll(listKey);
//                        listValue.removeAll(listValue);
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message != null) {
            if (message.getMessageType().equals("image")) {
                android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                layoutParams.width = 100;
                layoutParams.height = 100;
                holder.image_message.setLayoutParams(layoutParams);
                Glide.with(context).load(url_s3+message.getFileName()).into(holder.image_message);
                holder.txt_content.setWidth(0);
                holder.txt_content.setHeight(0);
                holder.image_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, Full_Image_Avatar.class);
                        intent.putExtra("url", url_s3+message.getFileName());
                        context.startActivity(intent);
                    }
                });
            } else if (message.getMessageType().equals("file")){
                holder.txt_content.setTypeface(holder.txt_content.getTypeface(), Typeface.ITALIC);
                String fileName = message.getFileName().substring(37);
                holder.txt_content.setText(fileName);
                android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                layoutParams.width = 100;
                layoutParams.height = 100;
                holder.image_message.setLayoutParams(layoutParams);
                holder.image_message.setImageResource(R.drawable.file);
            }else {
                holder.txt_content.setText(message.getText());
            }
            if(new Date().getTime() - message.getCreatedAt() < 86400000){
                holder.txt_timeSend.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
            }
            else{
                String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
                holder.txt_timeSend.setText(date);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!message.getSenderId().equals(userCurrent.getId())){
                    if(!message.getMessageType().equals("note")){
                        sendReactionForMessageMembers(userByMessage,message);
                    }
                }else{
                    if(!message.getMessageType().equals("note")){
                        sendReactionForMessagePersonal(userByMessage,message);
                    }
                }
                return  true;
            }
        });
    }
    public void sendReactionForMessageMembers(User sender,Message message){
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
        ImageView imgMessage =dialog.findViewById(R.id.imgMessageReaction);
        Glide.with(context).load(sender.getAvatar()).into(avatar);
        username.setText(sender.getUserName());
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message.getMessageType().equals("image")) {
            android.view.ViewGroup.LayoutParams layoutParamsReaction = imgMessage.getLayoutParams();
            layoutParamsReaction.width = 100;
            layoutParamsReaction.height = 100;
            imgMessage.setLayoutParams(layoutParamsReaction);
            Glide.with(context).load(url_s3+message.getFileName()).into(imgMessage);
            content.setWidth(0);
            content.setHeight(0);
        } else if (message.getMessageType().equals("file")){
            content.setTextColor(Color.parseColor("#008ae6"));
            content.setTypeface(content.getTypeface(), Typeface.ITALIC);
            content.setText(message.getFileName().substring(message.getFileName().indexOf(".")+1));
        }else {
            content.setText(message.getText());
        }
        if(new Date().getTime() - message.getCreatedAt() < 86400000){
            time.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        }
        else{
            String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
            time.setText(date);
        }

//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snap : snapshot.child(message.getId()).getChildren()) {
//                    if (snap.getValue() == null) {
//
//                    } else { ;
//                        if(snap.getKey().equals(userCurrent.getId())){
//                            if(snap.getValue().toString().equals("like")){
//                                imgLike.setBackgroundResource(R.drawable.background_message_center);
//                            }else if(snap.getValue().toString().equals("love"))
//                                imgLove.setBackgroundResource(R.drawable.background_message_center);
//                            else if(snap.getValue().toString().equals("haha"))
//                                imgHaha.setBackgroundResource(R.drawable.background_message_center);
//                            else if(snap.getValue().toString().equals("wow"))
//                                imgWow.setBackgroundResource(R.drawable.background_message_center);
//                            else if(snap.getValue().toString().equals("cry"))
//                                imgCry.setBackgroundResource(R.drawable.background_message_center);
//                            else if(snap.getValue().toString().equals("angry"))
//                                imgAngry.setBackgroundResource(R.drawable.background_message_center);
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        dialog.show();
    }
    public void sendReactionForMessagePersonal(User sender,Message message) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reaction_of_message);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT, layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        CircleImageView avatar = dialog.findViewById(R.id.imgAvatarReaction);
        TextView username = dialog.findViewById(R.id.tvUsernameReaction);
        TextView content = dialog.findViewById(R.id.tvMessageReaction);
        TextView time = dialog.findViewById(R.id.tvTimeReaction);
        ImageView imgMessage = dialog.findViewById(R.id.imgMessageReaction);
        ImageView btnDeleteMessage = dialog.findViewById(R.id.btnDeleteMessage);
        ImageView btnCopy = dialog.findViewById(R.id.btnCopyMessage);
        LinearLayout viewReaction = dialog.findViewById(R.id.viewReaction);
        viewReaction.setVisibility(View.INVISIBLE);
        Glide.with(context).load(sender.getAvatar()).into(avatar);
        username.setText(sender.getUserName());
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message.getMessageType().equals("image")) {
            android.view.ViewGroup.LayoutParams layoutParamsReaction = imgMessage.getLayoutParams();
            layoutParamsReaction.width = 100;
            layoutParamsReaction.height = 100;
            imgMessage.setLayoutParams(layoutParamsReaction);
            Glide.with(context).load(url_s3 + message.getFileName()).into(imgMessage);
            content.setWidth(0);
            content.setHeight(0);
        } else if (message.getMessageType().equals("file")) {
            content.setTextColor(Color.parseColor("#008ae6"));
            content.setTypeface(content.getTypeface(), Typeface.ITALIC);
            content.setText(message.getFileName().substring(message.getFileName().indexOf(".")+1));
        } else {
            content.setText(message.getText());
        }
        if (new Date().getTime() - message.getCreatedAt() < 86400000) {
            time.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        } else {
            String date = DateFormat.getDateFormat(context).format(message.getCreatedAt());
            time.setText(date);
        }
        btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Message> callDelete = dataService.deleteMessage(message.getId());
                callDelete.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message1 = response.body();
                        messages.remove(message1);
                        messageSocket.deleteMessage(message1);
                        Call<List<Message>> getMessages = dataService.getMessagesGroupByGroupId(message.getReceiverId());
                        getMessages.enqueue(new Callback<List<Message>>() {
                            @Override
                            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                                ChatBoxGroup.sendEventDeleteMessage(response.body(),userCurrent,members,context);
                            }

                            @Override
                            public void onFailure(Call<List<Message>> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
                dialog.dismiss();
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied Text",message.getText());
                clipboardManager.setPrimaryClip(clipData);
                dialog.dismiss();
//                Toast.makeText(context, "Đã sao chép", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_message;
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
            image_message = itemView.findViewById(R.id.image_message);
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
