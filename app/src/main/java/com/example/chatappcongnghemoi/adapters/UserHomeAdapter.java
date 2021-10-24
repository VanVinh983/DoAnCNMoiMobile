package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.Home;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private String userCurrentId;
    private DataService dataService;

    public UserHomeAdapter(List<User> users, Context context, String userCurrentId) {
        this.users = users;
        this.context = context;
        this.userCurrentId = userCurrentId;
        dataService = ApiService.getService();
    }

    @NonNull
    @Override
    public UserHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_user_home, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHomeAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        Glide.with(context).load(user.getAvatar()).into(holder.image_avatar);
        holder.txt_username.setText(user.getUserName());

        List<Message> messages = new ArrayList<>();
        Call<List<Message>> call = dataService.getMessageBySIdAndRId(userCurrentId, user.getId());
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                Message message = response.body().get(response.body().size()-1);
                messages.add(message);
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(context, "Fail get message list By user Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get message list By user Id at adapter call" + t.toString());
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (messages.size()==0){
                    handler.postDelayed(this,500);
                }else {
                    Message m = messages.get(0);
                    if (m.getSenderId().equals(userCurrentId)){
                        holder.txt_last_message.setText("Bạn: "+m.getText());
                    }else {
                        holder.txt_last_message.setText( user.getUserName()+": "+m.getText());
                    }
                }
            }
        },500);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image_avatar;
        private TextView txt_username, txt_last_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_avatar = itemView.findViewById(R.id.image_avatart_home_item);
            txt_username = itemView.findViewById(R.id.txt_home_item_user_name);
            txt_last_message = itemView.findViewById(R.id.txt_home_item_first_message);
        }
    }
}
