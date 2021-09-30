package com.example.chatappcongnghemoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineContactRecyclerAdapter extends RecyclerView.Adapter<OnlineContactRecyclerAdapter.ViewHolder> {

    private final int LIMIT_CONTACT_DISPLAY = 3;
    private Context context;
    private ArrayList<User> users;

    public OnlineContactRecyclerAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        Picasso.get().load(user.getAvatar()).into(holder.avatar);
        holder.txtName.setText(user.getUserName());
    }

    @Override
    public int getItemCount() {
        if (users.size() < LIMIT_CONTACT_DISPLAY)
            return users.size();
        else
            return LIMIT_CONTACT_DISPLAY;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar, btnCall, btnVideoCall;
        TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.itmContact_avatar);
            btnCall = itemView.findViewById(R.id.itmContact_btnCall);
            btnVideoCall = itemView.findViewById(R.id.itmContact_btnVideoCall);
            txtName = itemView.findViewById(R.id.itmContact_txtName);
        }
    }
}
