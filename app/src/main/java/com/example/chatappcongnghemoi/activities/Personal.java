package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.chatappcongnghemoi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class Personal extends AppCompatActivity implements View.OnClickListener {
    private EditText input_name, input_email, input_gender, input_yearOfBirth, input_numberPhone, input_address;
    private BottomNavigationView bottomNavigationView;
    private Button btn_update_info;
    private DatePickerDialog.OnDateSetListener setListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        getSupportActionBar().hide();

        //initialize variable
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        input_name = findViewById(R.id.input_personal_name);
        input_address = findViewById(R.id.input_personal_address);
        input_email = findViewById(R.id.input_personal_email);
        input_numberPhone = findViewById(R.id.input_personal_numberphone);
        input_gender = findViewById(R.id.input_personal_gender);
        input_yearOfBirth = findViewById(R.id.input_personal_yearOfBirth);
        btn_update_info = findViewById(R.id.btn_personal_update);

        //set edit text can't input letter

        input_gender.setEnabled(false);
        input_name.setEnabled(false);
        input_email.setEnabled(false);
        input_address.setEnabled(false);
        input_numberPhone.setEnabled(false);
        input_yearOfBirth.setEnabled(false);

        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.menuCaNhan);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.menuDanhBa:
                        startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        // create input calendar for input year of birth
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        //add onclick listener for input year of birth
        input_yearOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Personal.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day +"/"+month+"/"+year;
                        input_yearOfBirth.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //add onclick listener for button
        btn_update_info.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_personal_update:{
                if (btn_update_info.getText().equals("Cập nhật thông tin")){
                    btn_update_info.setText("Lưu");
                    //set edit text can input letter
                    input_gender.setEnabled(true);
                    input_name.setEnabled(true);
                    input_email.setEnabled(true);
                    input_address.setEnabled(true);
                    input_numberPhone.setEnabled(true);
                    input_yearOfBirth.setEnabled(true);
                }else {
                    btn_update_info.setText("Cập nhật thông tin");
                }
            }
        }
    }
}