package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Local;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.ListenSocket;

import org.mindrot.jbcrypt.BCrypt;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Animation topAnim,bottomAnim;
    ImageView logo,slogan;
    DataService dataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        logo = findViewById(R.id.imgLogo);
        slogan = findViewById(R.id.slogan);
        logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
//        createUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ListenSocket.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
    private void createUser(){
        dataService = ApiService.getService();
        Local local = new Local();
        local.setPhone("0147258369");
        local.setPassword(BCrypt.hashpw("123456",BCrypt.gensalt(10)));
        User user = new User(local,"Nguyen Van D");
        Call<UserDTO> callback = dataService.createUser(user);
        callback.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
//                if(response.isSuccessful())
//                    Toast.makeText(SignUp_OTP.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(SignUp_OTP.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
    }
}