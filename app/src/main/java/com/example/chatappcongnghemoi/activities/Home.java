package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.chatappcongnghemoi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

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


    }

}