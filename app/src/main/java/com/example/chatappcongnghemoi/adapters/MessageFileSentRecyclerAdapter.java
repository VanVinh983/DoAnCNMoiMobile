package com.example.chatappcongnghemoi.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFileSentRecyclerAdapter extends RecyclerView.Adapter<MessageFileSentRecyclerAdapter.ViewHolder>{
    private List<Message> messages;
    private Context context;
    DataService dataService;
    public MessageFileSentRecyclerAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageFileSentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_file_sent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageFileSentRecyclerAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        dataService = ApiService.getService();
        if(new Date().getTime() - message.getCreatedAt() < 86400000){
            holder.tvTimeFileSent.setText(DateUtils.getRelativeTimeSpanString(message.getCreatedAt()));
        }
        else{

            holder.tvTimeFileSent.setText(android.text.format.DateFormat.format("hh:mm a, dd-MM-yyyy", message.getCreatedAt()));
        }
        Call<UserDTO> userDTOCall = dataService.getUserById(message.getSenderId());
        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                User user = response.body().getUser();
                holder.tvUsernameFileSent.setText("Gửi bởi "+user.getUserName());
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
        String fileName = message.getFileName().substring(37);
        holder.tvFileNameFileSent.setText(fileName);
        String extension = fileName.substring(fileName.indexOf(".")+1);
        if(extension.equals("docx")||extension.equals("doc")){
            holder.imgFileSent.setImageResource(R.drawable.doc);
        }else if(extension.equals("txt")){
            holder.imgFileSent.setImageResource(R.drawable.txt);
        }else if(extension.equals("ppt") || extension.equals("pptx") || extension.equals("pptm")){
            holder.imgFileSent.setImageResource(R.drawable.ppt);
        }else if(extension.equals("pdf")){
            holder.imgFileSent.setImageResource(R.drawable.pdf);
        }else if(extension.equals("zip")){
            holder.imgFileSent.setImageResource(R.drawable.zip);
        }else if(extension.equals("mp3")){
            holder.imgFileSent.setImageResource(R.drawable.mp3);
        }else if(extension.equals("msg")){
            holder.imgFileSent.setImageResource(R.drawable.msg_file);
        }else if(extension.equals("eps")){
            holder.imgFileSent.setImageResource(R.drawable.eps);
        }
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_s3 = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
                String url = url_s3 + message.getFileName();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String title = URLUtil.guessFileName(url,null,null);
                request.setTitle(title);
                request.setDescription("Đang tải");
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie",cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,url);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
                Toast.makeText(context, "Đang tải", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeFileSent,tvUsernameFileSent,tvFileNameFileSent;
        ImageView imgFileSent,btnDownload;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileNameFileSent = itemView.findViewById(R.id.tvFileNameFileSent);
            tvUsernameFileSent = itemView.findViewById(R.id.tvUsernameFileSent);
            tvTimeFileSent = itemView.findViewById(R.id.tvTimeFileSent);
            imgFileSent = itemView.findViewById(R.id.imgFileSent);
            btnDownload = itemView.findViewById(R.id.btnDownLoadFileSent);
        }
    }
}
