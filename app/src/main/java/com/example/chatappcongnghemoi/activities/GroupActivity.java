package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupActivity extends AppCompatActivity {

    private TextView mTabPhonebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().hide();

        mapping();
        init();
    }

    private void mapping() {
        mTabPhonebook = findViewById(R.id.actPhoneBook_tabPhoneBook);
    }

    public void init(){
        //initialize
        BottomNavigationView bottomNavigationView = findViewById(R.id.actGroup_bottomNavagation);
        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.menuDanhBa);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuCaNhan:
                        startActivity(new Intent(getApplicationContext(), Personal.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        mTabPhonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
            }
        });
    }
}