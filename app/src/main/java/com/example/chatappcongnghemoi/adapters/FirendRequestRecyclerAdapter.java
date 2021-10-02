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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.MainActivity;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirendRequestRecyclerAdapter extends RecyclerView.Adapter<FirendRequestRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private ArrayList<Contact> contacts;
    private String url;

    public FirendRequestRecyclerAdapter(Context context, ArrayList<User> users, ArrayList<Contact> contacts, String url) {
        this.context = context;
        this.users = users;
        this.contacts = contacts;
        this.url = url;
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
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteApi(url, contacts.get(position).get_id());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có muốn xóa yêu cầu kết bạn này?")
                        .setPositiveButton("Có", dialogClickListener)
                        .setNegativeButton("Không", dialogClickListener).show();
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putApi(url, contacts.get(position).get_id());
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

    private void putApi(String url, String _id) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT, url + _id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Chấp nhận kêt bạn thành công!", Toast.LENGTH_SHORT).show();
                restartActivity((Activity) context);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("status", "true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void deleteApi(String url, String _id) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url + _id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Xóa yêu cầu kết bạn thành công!", Toast.LENGTH_SHORT).show();
                restartActivity((Activity) context);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void restartActivity(Activity act) {
        Intent intent = new Intent(act, act.getClass());
        act.finish();
        act.startActivity(intent);
    }
}
