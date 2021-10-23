package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.UserHomeAdapter;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    private ImageView btnLogout;
    private DataService dataService;
    private RecyclerView recyclerView;
    private List<User> listConversation = new ArrayList<User>();
    private UserHomeAdapter userHomeAdapter;
    private User userCurrent = null;
    public static  final String SHARED_PREFERENCES= "saveID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        btnLogout = findViewById(R.id.btnLogout);
        //initialize
        dataService = ApiService.getService();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_home);
        recyclerView = findViewById(R.id.recyclerview_home);
        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuCaNhan:
                        startActivity(new Intent(getApplicationContext(), Personal.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuDanhBa:
                        startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        System.out.println("id la: "+new DataLoggedIn(this).getUserIdLoggedIn());

        //get user current
        getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userCurrent != null){
                    System.out.println(userCurrent);
                }else {
                    handler.postDelayed(this, 500);
                }
            }
        },500);

//        SharedPreferences sharedPreferences = getSharedPreferences("saveID", MODE_PRIVATE);
//        String id = sharedPreferences.getString("userId","");
//        if (!id.equals("")){
//            Log.d("tag","Preferences is: "+ id);
//            Log.d("tag", "ID: "+ new DataLoggedIn(this).getUserIdLoggedIn());
//        }

//         ContactService contactService = new ContactService(this, "614ddf15fe79c83cac2a7423");
//        UserService userService = new UserService(this, "614ddf15fe79c83cac2a7423");
//        Handler handler = new Handler();
//        contactService.VolleyGetContactsByUserId();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(contactService.isFlag())
//                    handler.postDelayed(this, 500);
//                else{
//                    contactIdList = contactService.getContactIdList();
//                    userService.getContactUserList(contactIdList);
//                    handler.removeCallbacks(this);
//                }
//            }
//        }, 500);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog =new Dialog(Home.this);
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
                TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                TextView tvLogout = dialog.findViewById(R.id.tvLogout_Dialog);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                tvLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call<UserDTO> callback = dataService.getUserById(new DataLoggedIn(Home.this).getUserIdLoggedIn());
                        callback.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                if(response.isSuccessful()){
                                    dialog.dismiss();
                                    User user = response.body().getUser();
                                    user.setOnline(false);
                                    saveIDLogout();
                                    Intent intent = new Intent(Home.this,StartApp.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {

                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        //initialize recyclerview
        userHomeAdapter = new UserHomeAdapter(listConversation, this);
        recyclerView.setAdapter(userHomeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void saveIDLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId","");
        editor.apply();
    }

    private void addUserByIdIntoUsers(String id){

        Call<UserDTO> dtoCall = dataService.getUserById(id);
        dtoCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                listConversation.add(response.body().getUser());
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(Home.this, "Fail get User By Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get User By Id"+t.toString());
            }
        });
    }

    private void getUserById(String id) {
        Call<UserDTO> dtoCall = dataService.getUserById(id);
        dtoCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                userCurrent = response.body().getUser();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(Home.this, "Fail get User By Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get User By Id" + t.toString());
            }
        });
    }

}