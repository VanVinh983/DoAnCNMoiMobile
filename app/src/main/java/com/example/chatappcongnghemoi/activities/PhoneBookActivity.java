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

public class PhoneBookActivity extends AppCompatActivity {

    private TextView mTabGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook);
        getSupportActionBar().hide();

        mapping();
        init();

    }

    private void mapping() {
        mTabGroup = findViewById(R.id.actPhoneBook_tabGroup);
    }

    public void init(){
        //initialize
        BottomNavigationView bottomNavigationView = findViewById(R.id.actPhonebook_bottomNavagation);
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

        mTabGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GroupActivity.class));
            }
        });
    }
}