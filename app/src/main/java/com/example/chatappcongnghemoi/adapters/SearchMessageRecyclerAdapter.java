package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.Full_Image_Avatar;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMessageRecyclerAdapter extends RecyclerView.Adapter<SearchMessageRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private User userCurrent;
    private ChatGroup chatGroup;
    private DataService dataService;
    private Context context;
    User userByMessage;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    String search;
    String typeSearch;

    public SearchMessageRecyclerAdapter(List<Message> messages, User userCurrent, ChatGroup chatGroup, Context context,String search,String typeSearch) {
        this.messages = messages;
        this.userCurrent = userCurrent;
        this.chatGroup = chatGroup;
        this.context = context;
        this.dataService = ApiService.getService();
        this.search = search;
        this.typeSearch = typeSearch;
    }

    @NonNull
    @Override
    public SearchMessageRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull SearchMessageRecyclerAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
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
        List<Map<String,String>> mapReact = message.getReaction();
        if(mapReact.size() > 0){
            holder.imgReaction.setVisibility(View.VISIBLE);
            String react = mapReact.get(mapReact.size()-1).get("react");
            if(react.equals("thich")){
                holder.imgReaction.setImageResource(R.drawable.like);
            }else if(react.equals("yeu")){
                holder.imgReaction.setImageResource(R.drawable.heart);
            }else if(react.equals("cuoi")){
                holder.imgReaction.setImageResource(R.drawable.laughing);
            }else if(react.equals("wow")){
                holder.imgReaction.setImageResource(R.drawable.wow);
            }else if(react.equals("khoc")){
                holder.imgReaction.setImageResource(R.drawable.crying);
            }else if(react.equals("gian")){
                holder.imgReaction.setImageResource(R.drawable.angry);
            }
            holder.txt_quantityReaction.setText(mapReact.size()+"");
        }else{
            holder.txt_quantityReaction.setText("");
            holder.imgReaction.setVisibility(View.INVISIBLE);
        }
        if (message != null) {
            if(typeSearch.equals("text")) {
                if (message.getMessageType().equals("file")) {
                    holder.txt_content.setTypeface(holder.txt_content.getTypeface(), Typeface.ITALIC);
                    android.view.ViewGroup.LayoutParams layoutParamsContent = holder.txt_content.getLayoutParams();
                    layoutParamsContent.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParamsContent.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.txt_content.setLayoutParams(layoutParamsContent);
                    if (message.getMessageType().equals("file")) {
                        if (!search.equals("")) {
                            String text = message.getFileName().substring(37);
                            int index = text.indexOf(search);
                            if (index == 0) {
                                String colorText = text.substring(index, search.length());
                                String next = text.substring(index + search.length());
                                holder.txt_content.setText(Html.fromHtml("<b><font color=red>" + colorText + "</font></b>" + next), TextView.BufferType.SPANNABLE);
                            } else {
                                String prev = text.substring(0, index);
                                String colorText = text.substring(index, index + search.length());
                                String next = text.substring(index + search.length());
                                holder.txt_content.setText(Html.fromHtml(prev + "<b><font color=red>" + colorText + "</font></b>" + next), TextView.BufferType.SPANNABLE);
                            }
                        } else {
                            holder.txt_content.setText(message.getFileName().substring(37));
                        }

                    }
                    android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                    layoutParams.width = 100;
                    layoutParams.height = 100;
                    holder.image_message.setLayoutParams(layoutParams);
                    holder.image_message.setImageResource(R.drawable.file);
                    LinearLayout.LayoutParams layoutParamsGif = new LinearLayout.LayoutParams(0, 0);
                    android.view.ViewGroup.LayoutParams layoutParamsLoad = holder.img_download.getLayoutParams();
                    layoutParamsLoad.width = 70;
                    layoutParamsLoad.height = 70;
                    holder.img_download.setLayoutParams(layoutParamsLoad);
                    holder.img_download.setImageResource(R.drawable.download);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                    holder.image_message.setLayoutParams(layoutParams);
                    holder.img_download.setLayoutParams(layoutParams);
                    android.view.ViewGroup.LayoutParams layoutParamsContent = holder.txt_content.getLayoutParams();
                    layoutParamsContent.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParamsContent.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.txt_content.setLayoutParams(layoutParamsContent);
                    if (message.getMessageType().equals("text")) {
                        if (!search.equals("")) {
                            String text = message.getText().toString();
                            int index = text.indexOf(search);
                            if (index == 0) {
                                String colorText = text.substring(index, search.length());
                                String next = text.substring(index + search.length());
                                holder.txt_content.setText(Html.fromHtml("<b><font color=red>" + colorText + "</font></b>" + next), TextView.BufferType.SPANNABLE);
                            } else {
                                String prev = text.substring(0, index);
                                String colorText = text.substring(index, index + search.length());
                                String next = text.substring(index + search.length());
                                holder.txt_content.setText(Html.fromHtml(prev + "<b><font color=red>" + colorText + "</font></b>" + next), TextView.BufferType.SPANNABLE);
                            }
                        } else {
                            holder.txt_content.setText(message.getText());
                        }

                    }
                }
            }
            else{
                String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
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
                    holder.txt_content.setTypeface(holder.txt_content.getTypeface(), Typeface.ITALIC);
                    String fileName = message.getFileName().substring(37);
                    android.view.ViewGroup.LayoutParams layoutParamsContent = holder.txt_content.getLayoutParams();
                    layoutParamsContent.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParamsContent.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.txt_content.setLayoutParams(layoutParamsContent);
                    holder.txt_content.setText(fileName);
                    android.view.ViewGroup.LayoutParams layoutParams = holder.image_message.getLayoutParams();
                    layoutParams.width = 100;
                    layoutParams.height = 100;
                    holder.image_message.setLayoutParams(layoutParams);
                    holder.image_message.setImageResource(R.drawable.file);
                    LinearLayout.LayoutParams layoutParamsGif = new LinearLayout.LayoutParams(0,0);
                    holder.gifImageView.setLayoutParams(layoutParamsGif);
                    android.view.ViewGroup.LayoutParams layoutParamsLoad = holder.img_download.getLayoutParams();
                    layoutParamsLoad.width = 70;
                    layoutParamsLoad.height = 70;
                    holder.img_download.setLayoutParams(layoutParamsLoad);
                    holder.img_download.setImageResource(R.drawable.download);
                }else if (message.getMessageType().equals("gif")){
                    android.view.ViewGroup.LayoutParams layoutParamsGif = holder.gifImageView.getLayoutParams();
                    layoutParamsGif.width = 400;
                    layoutParamsGif.height = 350;
                    holder.gifImageView.setLayoutParams(layoutParamsGif);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
                    holder.txt_content.setLayoutParams(layoutParams);
                    holder.image_message.setLayoutParams(layoutParams);
                    holder.img_download.setLayoutParams(layoutParams);
                    Glide.with(context).load(message.getFileName()).into(holder.gifImageView);
                }else {
//                android.view.ViewGroup.LayoutParams layoutParamsEmpty = holder.image_message.getLayoutParams();
//                layoutParamsEmpty.width = 0;
//                layoutParamsEmpty.height = 0;
//                holder.gifImageView.setLayoutParams(layoutParamsEmpty);
//                holder.image_message.setLayoutParams(layoutParamsEmpty);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
                    holder.gifImageView.setLayoutParams(layoutParams);
                    holder.image_message.setLayoutParams(layoutParams);
                    holder.img_download.setLayoutParams(layoutParams);
                    android.view.ViewGroup.LayoutParams layoutParamsContent = holder.txt_content.getLayoutParams();
                    layoutParamsContent.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParamsContent.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.txt_content.setLayoutParams(layoutParamsContent);
                    holder.txt_content.setText(message.getText());
                }
            }
            if(new Date().getTime() - message.getCreatedAt() < 86400000){
                holder.txt_timeSend.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
            }
            else{

                holder.txt_timeSend.setText(android.text.format.DateFormat.format("hh:mm a, dd-MM-yyyy", message.getCreatedAt()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_message,img_download;
        private TextView txt_content,txt_username,txt_timeSend,txt_quantityReaction;
        private CircleImageView avatar,imgReaction;
        private GifImageView gifImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content = itemView.findViewById(R.id.txt_content_message);
            avatar = itemView.findViewById(R.id.image_message_avatar);
            txt_username = itemView.findViewById(R.id.tvUsernameChatBoxLeft);
            txt_timeSend = itemView.findViewById(R.id.tvTimeSendLeft);
            txt_quantityReaction = itemView.findViewById(R.id.tvQuantityReaction);
            imgReaction = itemView.findViewById(R.id.imgReactionOfMessage);
            image_message = itemView.findViewById(R.id.image_message);
            img_download = itemView.findViewById(R.id.imgDownload);
            gifImageView = itemView.findViewById(R.id.image_gif);
        }
    }
    @Override
    public int getItemViewType(int position) {
            if (messages.get(position).getSenderId().equals(userCurrent.getId())) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
}
