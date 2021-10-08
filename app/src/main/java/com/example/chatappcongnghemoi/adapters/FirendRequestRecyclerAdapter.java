package com.example.chatappcongnghemoi.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.PersonalOfOthers;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactDTO;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;

public class FirendRequestRecyclerAdapter extends RecyclerView.Adapter<FirendRequestRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private ArrayList<Contact> contacts;

    public FirendRequestRecyclerAdapter(Context context, ArrayList<User> users, ArrayList<Contact> contacts) {
        this.context = context;
        this.users = users;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);
        Picasso.get().load(user.getAvatar()).into(holder.avatar);
        holder.txtName.setText(user.getUserName());

        holder.btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                deleteApi(contacts.get(position).getId());
                                break;
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có muốn xóa yêu cầu kết bạn này?")
                        .setPositiveButton("Không", dialogClickListener)
                        .setNegativeButton("Có", dialogClickListener).show();
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = contacts.get(0);
                contact.setStatus(true);
                putApi(contact.getId(), contact);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalOfOthers.class);
                intent.putExtra("user",user);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar, btnX;
        Button btnAccept;
        TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.itmFriendRequest_avatar);
            btnAccept = itemView.findViewById(R.id.itmFriendRequest_btnAccept);
            txtName = itemView.findViewById(R.id.itmFriendRequest_txtName);
            btnX = itemView.findViewById(R.id.itmFriendRequest_btnX);
        }
    }

    private void putApi(String id, Contact contact) {
        DataService dataService = ApiService.getService();
        Call<PUT> callback = dataService.updateContact(id, contact);
        callback.enqueue(new Callback<PUT>() {
            @Override
            public void onResponse(Call<PUT> call, retrofit2.Response<PUT> response) {
            }

            @Override
            public void onFailure(Call<PUT> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Toast.makeText(context, "Thêm bạn thành công", Toast.LENGTH_SHORT).show();
        restartActivity((Activity) context);

    }

    private void deleteApi(String id) {
        DataService dataService = ApiService.getService();
        Call<DELETE> callback = dataService.deteleContactById(id);
        callback.enqueue(new Callback<DELETE>() {
            @Override
            public void onResponse(Call<DELETE> call, Response<DELETE> response) {
            }

            @Override
            public void onFailure(Call<DELETE> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Toast.makeText(context, "Xóa yêu cầu kết bạn thành công", Toast.LENGTH_SHORT).show();
        restartActivity((Activity) context);
    }

    public void restartActivity(Activity act) {
        Intent intent = new Intent(act, act.getClass());
        act.finish();
        act.startActivity(intent);
    }
}
