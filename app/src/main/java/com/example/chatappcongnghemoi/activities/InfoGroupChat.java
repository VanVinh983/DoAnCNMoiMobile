package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.MembersOfInfoGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoGroupChat extends AppCompatActivity {
    CircleImageView imgAvatarGroup;
    TextView tvGroupName,tvQuantityMember;
    ChatGroup chatGroup;
    String groupId;
    DataService dataService;
    ImageView btnBack;
    ImageButton btnRename;
    LinearLayout btnWatchMembers,btnChangeBackground;
    User user = null;
    List<User> members ;
    DatabaseReference database;
    String background = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_group_chat);
        getSupportActionBar().hide();
        dataService = ApiService.getService();
        database = FirebaseDatabase.getInstance().getReference("background");
        mapping();
        init();
        btnBack.setOnClickListener((view) ->{
            Intent intent = new Intent(InfoGroupChat.this,ChatBoxGroup.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
            finish();
        });
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog =new Dialog(InfoGroupChat.this);
                dialog.setContentView(R.layout.dialog_rename_group);
                Window window = dialog.getWindow();
                if(window == null)
                    return;
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.setCancelable(true);
                TextView tvCancel = dialog.findViewById(R.id.tvCancelRename);
                TextView tvConfirm = dialog.findViewById(R.id.tvRename_Dialog);
                EditText txtRename = dialog.findViewById(R.id.txtRename);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = txtRename.getText().toString().trim();
                        if(!newName.matches("^[0-9\\u0041-\\u00ff\\u0100-\\u013c\\u01cd-\\u01ce\\s]{2,40}$")){
                            Toast.makeText(InfoGroupChat.this, "Tên nhóm không được chứa kí tự đặc biệt, có từ 2-40 kí tự", Toast.LENGTH_SHORT).show();
                        }else{
                            chatGroup.setName(newName);
                            Call<ChatGroup> chatGroupCall = dataService.updateGroup(groupId,chatGroup);
                            chatGroupCall.enqueue(new Callback<ChatGroup>() {
                                @Override
                                public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                                }

                                @Override
                                public void onFailure(Call<ChatGroup> call, Throwable t) {

                                }
                            });
                            dialog.dismiss();
                            Toast.makeText(InfoGroupChat.this, "Đổi tên nhóm thành công", Toast.LENGTH_SHORT).show();
                            tvGroupName.setText(newName);
                        }
                    }
                });
                dialog.show();
            }
        });
        btnWatchMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchMembers();
            }
        });
        btnChangeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackground();
            }
        });
    }
    public  void mapping(){
        tvGroupName = findViewById(R.id.tvGroupNameInfoGroup);
        imgAvatarGroup = findViewById(R.id.imgAvatarInfoGroup);
        Picasso.get().load("https://www.iucn.org/sites/dev/files/content/images/2020/shutterstock_1458128810.jpg").into(imgAvatarGroup);
        tvQuantityMember = findViewById(R.id.tvQuantityMemberInfoGroup);
        btnBack = findViewById(R.id.imgBackInfoGroupChat);
        btnRename = findViewById(R.id.btnRename);
        btnWatchMembers = findViewById(R.id.btnWatchMembers);
        btnChangeBackground = findViewById(R.id.btnChangeBackgroundInfoGroup);
    }
    public void init(){
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        Call<ChatGroup> chatGroupCall = dataService.getGroupById(groupId);
        chatGroupCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                tvGroupName.setText(chatGroup.getName());
                tvQuantityMember.setText("Xem thành viên ("+chatGroup.getMembers().size()+")");
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {

            }
        });
    }
    public void watchMembers(){
//        MembersOfInfoGroupRecyclerAdapter adapter;
        final Dialog dialog =new Dialog(InfoGroupChat.this);
        dialog.setContentView(R.layout.dialog_members_info_group);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        ImageView tvClose = dialog.findViewById(R.id.btnCloseDialogMembersInfoGroup);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerviewMembersInfoGroup);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        members = new ArrayList<>();
        List<Map<String,String>> listMap = chatGroup.getMembers();
        List<String> listId = new ArrayList<>();
        listMap.forEach((map) -> {
            listId.add(map.get("userId"));
        });
        listId.forEach((id) ->{
            Call<UserDTO> userCall = dataService.getUserById(id);
            userCall.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    User user = response.body().getUser();
                    members.add(user);
                    if(members.size() == listId.size()){
                        MembersOfInfoGroupRecyclerAdapter adapter = new MembersOfInfoGroupRecyclerAdapter(members,InfoGroupChat.this,chatGroup);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InfoGroupChat.this));
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {

                }
            });
        });
        dialog.show();
    }
    public void setBackGround(String newBackground){
        background = newBackground;
    }
    public void changeBackground(){
        final Dialog dialog =new Dialog(InfoGroupChat.this);
        dialog.setContentView(R.layout.dialog_change_background_chat);
        Window window = dialog.getWindow();
        if(window == null)
            return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);
        Button btnCancel = dialog.findViewById(R.id.btnCancelChangeBackgroundInfoGroup);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmChangeBackgroundInfoGroup);
        ImageButton imgDefault = dialog.findViewById(R.id.imgBackGroundDefaultInfoGroup);
        ImageButton imgYellow = dialog.findViewById(R.id.imgBackGroundYellowInfoGroup);
        ImageButton imgBlue = dialog.findViewById(R.id.imgBackGroundBlueInfoGroup);
        ImageButton imgGreen = dialog.findViewById(R.id.imgBackGroundGreenInfoGroup);
        ImageButton imgRed = dialog.findViewById(R.id.imgBackGroundRedInfoGroup);
        ImageButton imgPink = dialog.findViewById(R.id.imgBackGroundPinkInfoGroup);
        imgGreen.setBackgroundColor(Color.WHITE);
        imgDefault.setBackgroundColor(Color.WHITE);
        imgYellow.setBackgroundColor(Color.WHITE);
        imgRed.setBackgroundColor(Color.WHITE);
        imgBlue.setBackgroundColor(Color.WHITE);
        imgPink.setBackgroundColor(Color.WHITE);
        imgDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("default");
                imgDefault.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("yellow");
                imgYellow.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("blue");
                imgBlue.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("green");
                imgGreen.setBackgroundColor(Color.BLUE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        imgPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("pink");
                imgPink.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgRed.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
            }
        });
        imgRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGround("red");
                imgRed.setBackgroundColor(Color.BLUE);
                imgGreen.setBackgroundColor(Color.WHITE);
                imgYellow.setBackgroundColor(Color.WHITE);
                imgDefault.setBackgroundColor(Color.WHITE);
                imgBlue.setBackgroundColor(Color.WHITE);
                imgPink.setBackgroundColor(Color.WHITE);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(background == ""){
                    Toast.makeText(InfoGroupChat.this, "Vui lòng chọn hình nền", Toast.LENGTH_SHORT).show();
                }else{
                    database.child(groupId).child("background").setValue(background);
                    dialog.dismiss();
                    Toast.makeText(InfoGroupChat.this, "Đổi hình nền chat thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}