package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.UserPhonebook;

import java.util.ArrayList;

public class UserPhonebookAdapter extends RecyclerView.Adapter<UserPhonebookAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UserPhonebook> list;

    public UserPhonebookAdapter(Context context, ArrayList<UserPhonebook> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_phonebook, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPhonebook userPhonebook = list.get(position);
        holder.txtName.setText(userPhonebook.getName());
        holder.txtPhone.setText(userPhonebook.getPhone());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPhone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.itmUserPhonebook_txtName);
            txtName = itemView.findViewById(R.id.itmUserPhonebook_txtPhone);
        }
    }
}
