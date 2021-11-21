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
