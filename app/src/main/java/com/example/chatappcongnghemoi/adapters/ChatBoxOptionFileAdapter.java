package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;

import java.util.List;

public class ChatBoxOptionFileAdapter extends RecyclerView.Adapter<ChatBoxOptionFileAdapter.ViewHolder> {
    private List<String> list;
    private Context context;

    public ChatBoxOptionFileAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatBoxOptionFileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_chatbox_option_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxOptionFileAdapter.ViewHolder holder, int position) {
        String s = list.get(position);
//        String url = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/"+s;
        holder.txt_name.setText(s.substring(37));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_chatbox_option_name_file);
        }
    }
}
