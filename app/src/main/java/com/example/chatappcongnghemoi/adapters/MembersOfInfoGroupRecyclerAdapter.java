package com.example.chatappcongnghemoi.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.activities.ChatBoxGroup;
import com.example.chatappcongnghemoi.activities.Home;
import com.example.chatappcongnghemoi.activities.InfoGroupChat;
import com.example.chatappcongnghemoi.activities.StartApp;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.GroupSocket;
import com.example.chatappcongnghemoi.socket.MessageSocket;
import com.example.chatappcongnghemoi.socket.MySocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersOfInfoGroupRecyclerAdapter extends RecyclerView.Adapter<MembersOfInfoGroupRecyclerAdapter.ViewHolder> {
    List<User> members;
    Context context;
    ChatGroup chatGroup;
    DataService dataService;
    User userCurrent = null;
    private GroupSocket groupSocket;
    private MessageSocket messageSocket;
    private static Socket mSocket = MySocket.getInstance().getSocket();
    private Emitter.Listener responeMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

        }
    };
    public MembersOfInfoGroupRecyclerAdapter(List<User> members, Context context,ChatGroup chatGroup,User userCurrent) {
        this.members = members;
        this.context = context;
        this.chatGroup = chatGroup;
        this.userCurrent = userCurrent;
    }

    @NonNull
    @Override
    public MembersOfInfoGroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_of_group,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersOfInfoGroupRecyclerAdapter.ViewHolder holder, int position) {
        User user = members.get(position);
        mSocket.on("response-add-new-text", responeMessage);
        dataService = ApiService.getService();
        Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(userCurrent.getId());
        listCall.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                groupSocket = new GroupSocket(response.body(),userCurrent);
                messageSocket = new MessageSocket(response.body(),userCurrent);
            }
            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                System.err.println("fail get list group by user"+t.getMessage());
            }
        });
        members.forEach((u) ->{
            if(user.getId().equals(chatGroup.getUserId()))
                holder.leader.setText("Trường nhóm");
        });
        Glide.with(context).load(user.getAvatar()).into(holder.avatar);
        holder.username.setText(user.getUserName());
        holder.btnOptions.setVisibility(View.INVISIBLE);
        if(chatGroup.getUserId().equals(new DataLoggedIn(context).getUserIdLoggedIn())){
                if(!user.getId().equals(new DataLoggedIn(context).getUserIdLoggedIn())){
                    holder.btnOptions.setVisibility(View.VISIBLE);
                }
        }
        String[] items = {"","Nhường trưởng nhóm","Yêu cầu rời nhóm"};
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.btnOptions.setAdapter(adapter);
        holder.btnOptions.setAutofillHints("***");
        holder.btnOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    final Dialog dialog =new Dialog(context);
                    dialog.setContentView(R.layout.dialog_logout);
                    Window window = dialog.getWindow();
                    if(window == null)
                        return;
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    layoutParams.gravity = Gravity.CENTER;
                    window.setAttributes(layoutParams);
                    dialog.setCancelable(true);
                    TextView textView21= dialog.findViewById(R.id.textView21);
                    TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                    TextView tvConfirm = dialog.findViewById(R.id.tvLogout_Dialog);
                    tvConfirm.setText("XÁC NHẬN");
                    textView21.setText("Nhường trưởng phòng cho "+user.getUserName());
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    tvConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chatGroup.setUserId(user.getId());
                            Call<ChatGroup> chatGroupCall = dataService.updateGroup(chatGroup.getId(),chatGroup);
                            chatGroupCall.enqueue(new Callback<ChatGroup>() {
                                @Override
                                public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {

                                }

                                @Override
                                public void onFailure(Call<ChatGroup> call, Throwable t) {

                                }
                            });
                            Message message = new Message();
                            message.setCreatedAt(new Date().getTime());
                            message.setMessageType("note");
                            message.setReceiverId(chatGroup.getId());
                            message.setSenderId(new DataLoggedIn(context).getUserIdLoggedIn());
                            message.setText("Đã nhường trưởng nhóm cho "+user.getUserName());
                            message.setChatType("group");
                            Call<Message> messageCall = dataService.postMessage(message);
                            messageCall.enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    Message message1 = response.body();
                                    messageSocket.sendMessage(message1,"true");
                                    Toast.makeText(context, "Nhường trưởng nhóm thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, ChatBoxGroup.class);
                                    intent.putExtra("groupId",chatGroup.getId());
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {

                                }
                            });
                        }
                    });
                    dialog.show();
                }else if(position==2){
                    final Dialog dialog =new Dialog(context);
                    dialog.setContentView(R.layout.dialog_logout);
                    Window window = dialog.getWindow();
                    if(window == null)
                        return;
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    layoutParams.gravity = Gravity.CENTER;
                    window.setAttributes(layoutParams);
                    dialog.setCancelable(true);
                    TextView textView21= dialog.findViewById(R.id.textView21);
                    TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                    TextView tvConfirm = dialog.findViewById(R.id.tvLogout_Dialog);
                    tvConfirm.setText("XÁC NHẬN");
                    textView21.setText("Đã yêu cầu "+user.getUserName()+" rời nhóm.");
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    tvConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<Map<String,String>> members = chatGroup.getMembers();
                            for(int i = 0 ; i< members.size();i++){
                                if(members.get(i).get("userId").equals(user.getId())){
                                    members.remove(members.get(i));
                                    break;
                                }
                            }
                            chatGroup.setMembers(members);
                            Call<ChatGroup> chatGroupCall = dataService.updateGroup(chatGroup.getId(),chatGroup);
                            chatGroupCall.enqueue(new Callback<ChatGroup>() {
                                @Override
                                public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {

                                }

                                @Override
                                public void onFailure(Call<ChatGroup> call, Throwable t) {

                                }
                            });
                            Message message = new Message();
                            message.setCreatedAt(new Date().getTime());
                            message.setMessageType("note");
                            message.setReceiverId(chatGroup.getId());
                            message.setSenderId(new DataLoggedIn(context).getUserIdLoggedIn());
                            message.setText("Đã yêu cầu "+user.getUserName() + " rời nhóm.");
                            message.setChatType("group");
                            Call<Message> messageCall = dataService.postMessage(message);
                            messageCall.enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    Message message1 = response.body();
                                    messageSocket.sendMessage(message1,"true");
                                    Toast.makeText(context, "Yêu cầu rời nhóm thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, ChatBoxGroup.class);
                                    intent.putExtra("groupId",chatGroup.getId());
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {

                                }
                            });
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView username,leader;
        Spinner btnOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatarMemberInfoGroup);
            username = itemView.findViewById(R.id.tvUserNameInfoGroup);
            leader = itemView.findViewById(R.id.tvLeaderInfoGroup);
            btnOptions = itemView.findViewById(R.id.btnOptionsMembersOfGroup);
        }
    }

}
