package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.Full_Image_Avatar;
import com.example.chatappcongnghemoi.activities.Personal;

import java.util.List;

public class ChatboxOptionImageAdapter extends RecyclerView.Adapter<ChatboxOptionImageAdapter.ViewHolder> {
    private List<String> urlLinks;
    private Context context;

    public ChatboxOptionImageAdapter(List<String> urlLinks, Context context) {
        this.urlLinks = urlLinks;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatboxOptionImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_image_sent_chatbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatboxOptionImageAdapter.ViewHolder holder, int position) {
        String imageUrl = urlLinks.get(position);
        String url = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/"+imageUrl;
        Glide.with(context).load(url).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Full_Image_Avatar.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlLinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_chatbox_option_image);
        }
    }
}
