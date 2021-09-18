package com.example.chatappcongnghemoi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.StartItem;

import java.util.List;

public class StartAdapter extends RecyclerView.Adapter<StartAdapter.ViewHolder> {
    private List<StartItem> list;

    public StartAdapter(List<StartItem> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public StartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_start,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartAdapter.ViewHolder holder, int position) {
        holder.img.setImageResource(list.get(position).getImg());
        holder.name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgStart);
            name = itemView.findViewById(R.id.name);
        }
    }
}
