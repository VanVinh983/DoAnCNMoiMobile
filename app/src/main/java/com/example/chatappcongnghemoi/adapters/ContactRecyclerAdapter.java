package com.example.chatappcongnghemoi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.OutgoingCallActivity;
import com.example.chatappcongnghemoi.activities.PersonalOfOthers;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.socket.ListenSocket;
import com.example.chatappcongnghemoi.socket.VideoCallSocket;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;

    public ContactRecyclerAdapter(Context context, ArrayList<User> users) {
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
        try {
            if (user.getAvatar() != null)
                Picasso.get().load(user.getAvatar()).into(holder.avatar);
        } catch (NullPointerException e) {
            holder.avatar.setImageResource(R.drawable.avatar);
        }
        try {
            holder.txtName.setText(user.getUserName());
        } catch (NullPointerException e) {
            holder.txtName.setText("User Name");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalOfOthers.class);
                intent.putExtra("user", user);
                ((Activity)context).finish();
                context.startActivity(intent);
            }
        });

        holder.btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OutgoingCallActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Receiver", user);
                bundle.putString("type", "video");
                intent.putExtras(bundle);
//                intent.putExtra("Receiver", user);
//                intent.putExtra("type", "video");
                ((Activity)context).finish();
                context.startActivity(intent);
            }
        });

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OutgoingCallActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Receiver", user);
                bundle.putString("type", "audio");
                intent.putExtras(bundle);
//                intent.putExtra("Receiver", user);
//                intent.putExtra("type", "audio");
                ((Activity)context).finish();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
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
