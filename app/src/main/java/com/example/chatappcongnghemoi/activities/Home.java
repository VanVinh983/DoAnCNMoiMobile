package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    ImageView btnLogout;
    public static  final String SHARED_PREFERENCES= "saveID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        btnLogout = findViewById(R.id.btnLogout);
        //initialize
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_home);
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
                        saveIDLogout();
                        dialog.dismiss();
                        Intent intent = new Intent(Home.this,StartApp.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.show();
            }
        });

    }
    public void saveIDLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId","");
        editor.apply();
    }
}