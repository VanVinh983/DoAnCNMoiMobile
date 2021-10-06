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
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Personal extends AppCompatActivity implements View.OnClickListener {
    private EditText input_name, input_email, input_gender, input_yearOfBirth, input_numberPhone, input_address;
    private BottomNavigationView bottomNavigationView;
    private Button btn_update_info;
    private CircleImageView imageView_Avatar;
    private TextView txt_introduce, txt_personal_primary;
    private DataService dataService;
    private User user = null;
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
        txt_personal_primary = findViewById(R.id.txt_personal_name_primary);
        //initialize dataservice
        dataService = ApiService.getService();

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
        getUserById("6159c1f9bf628567ac523586");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null){
                    txt_personal_primary.setText(user.getUserName());
                    input_name.setText(user.getUserName());
                    input_gender.setText(user.getGender());
                    input_yearOfBirth.setText(user.getBirthday());
                    input_numberPhone.setText(user.getLocal().getPhone());
                    input_address.setText(user.getAddress());
                    Glide.with( Personal.this).load(user.getAvatar()).into(imageView_Avatar);
                }else {
                    handler.postDelayed(this, 500);
                }
            }
        },500);
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
    private void getUserById(String id){
        Call<UserDTO> dtoCall = dataService.getUserById(id);
        dtoCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                user = response.body().getUser();
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(Personal.this, "Fail get User By Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get User By Id"+t.toString());
            }
        });
    }
}