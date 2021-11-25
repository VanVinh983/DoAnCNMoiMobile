package com.example.chatappcongnghemoi.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.Full_Image_Avatar;
import com.example.chatappcongnghemoi.activities.Personal;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MessageSocket;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private Context context;
    private User userCurrent;
    private User friend;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static int message_type;
    private DataService dataService;
    private MessageSocket messageSocket;

    public MessageAdapter(List<Message> messages, Context context, User userCurrent, User friend) {
        this.messages = messages;
        this.context = context;
        this.userCurrent = userCurrent;
        this.friend = friend;
        dataService = ApiService.getService();
        messageSocket = new MessageSocket();
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
        String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        if (message != null) {
            if (message.getMessageType().equals("image")) {
                android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                layoutParams.width = 100;
                layoutParams.height = 100;
                holder.image_message.setLayoutParams(layoutParams);
                Glide.with(context).load(url_s3+message.getFileName()).into(holder.image_message);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0,0);
                holder.txt_content.setLayoutParams(layoutParams1);
                holder.gifImageView.setLayoutParams(layoutParams1);
                holder.image_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, Full_Image_Avatar.class);
                        intent.putExtra("url", url_s3+message.getFileName());
                        context.startActivity(intent);
                    }
                });
                android.view.ViewGroup.LayoutParams layoutParamsLoad = holder.img_download.getLayoutParams();
                layoutParamsLoad.width = 70;
                layoutParamsLoad.height = 70;
                holder.img_download.setLayoutParams(layoutParamsLoad);
                holder.img_download.setImageResource(R.drawable.download);
            } else if (message.getMessageType().equals("file")){
                holder.txt_content.setTextColor(Color.parseColor("#008ae6"));
                holder.txt_content.setTypeface(holder.txt_content.getTypeface(), Typeface.ITALIC);
                holder.txt_content.setText(message.getFileName());
            }else {
                holder.txt_content.setText(message.getText());
            }

            Glide.with(context).load(friend.getAvatar()).into(holder.avatar);
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

            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(!message.getSenderId().equals(userCurrent.getId())){
                        if(!message.getMessageType().equals("note")){
                            sendReactionForMessage(friend,message);
                        }
                    }
                    return  true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_content,username, time;
        private CircleImageView avatar;
        private View view;
        private ImageView image_message,img_download;
        private GifImageView gifImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content = itemView.findViewById(R.id.txt_content_message);
            avatar = itemView.findViewById(R.id.image_message_avatar);
            username = itemView.findViewById(R.id.tvUsernameChatBoxLeft);
            time = itemView.findViewById(R.id.tvTimeSendLeft);
            view = itemView;
            image_message = itemView.findViewById(R.id.image_message);
            gifImageView = itemView.findViewById(R.id.image_gif);
            img_download = itemView.findViewById(R.id.imgDownload);
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
        dialog.show();
    }
}
