package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.UpdateIntroduce;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personal extends AppCompatActivity implements View.OnClickListener {
    private EditText input_name, input_email, input_gender, input_yearOfBirth, input_numberPhone, input_address;
    private BottomNavigationView bottomNavigationView;
    private Button btn_update_info;
    private CircleImageView imageView_Avatar;
    private TextView txt_introduce;
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
        imageView_Avatar = findViewById(R.id.image_personal_avatar);
        txt_introduce = findViewById(R.id.txt_personal_introduce);

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

        // event dialog avatar
        imageView_Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAvatar(Gravity.CENTER);
            }
        });

        //event introduce
        txt_introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Personal.this, UpdateIntroduce.class);
                intent.putExtra("introduce", txt_introduce.getText().toString());
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_personal_update: {
                if (btn_update_info.getText().equals("Cập nhật thông tin")) {
                    btn_update_info.setText("Lưu");
                    //set edit text can input letter
                    input_gender.setEnabled(true);
                    input_name.setEnabled(true);
                    input_email.setEnabled(true);
                    input_address.setEnabled(true);
                    input_numberPhone.setEnabled(true);
                    input_yearOfBirth.setEnabled(true);
                } else {
                    btn_update_info.setText("Cập nhật thông tin");
                    input_gender.setEnabled(false);
                    input_name.setEnabled(false);
                    input_email.setEnabled(false);
                    input_address.setEnabled(false);
                    input_numberPhone.setEnabled(false);
                    input_yearOfBirth.setEnabled(false);
                }
            }
        }
    }

    private void openDialogAvatar(int gravity){
         final Dialog dialog = new Dialog(Personal.this);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.setContentView(R.layout.layout_dialog_avatar_personal);

         Window window = dialog.getWindow();
         if (window==null){
             return;
         }

         window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

         WindowManager.LayoutParams windowAttributes = window.getAttributes();
         windowAttributes.gravity = gravity;
         window.setAttributes(windowAttributes);

         if (Gravity.CENTER == gravity){
             dialog.setCancelable(true);
         }else {
             dialog.setCancelable(false);
         }
         dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                String result = data.getStringExtra("result");
                if (!result.equals("")){
                    txt_introduce.setText(result);
                }
            }
        }
    }
}