package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    ImageView imgBack;
    Button btnSignUp,btnLogin;
    TextView tvForgetPassword;
    EditText txtSDT,txtPassword;
    DataService dataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        imgBack = findViewById(R.id.imgBackLogin);
        btnSignUp = findViewById(R.id.btnSignUpLogin);
        btnLogin  = findViewById(R.id.btnLogin_Login);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        txtSDT = findViewById(R.id.txtSDT_Login);
        txtPassword = findViewById(R.id.txtPassword_Login);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,StartApp.class);
                startActivity(intent);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataService = ApiService.getService();
                String sdt = txtSDT.getText().toString();
                String password = txtPassword.getText().toString();
                if(sdt.equals(""))
                    Toast.makeText(Login.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                else{
                    if(password.equals(""))
                        Toast.makeText(Login.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    else{
                        Call<UserDTO> callback = dataService.getUserByPhone(sdt);
                        callback.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                if(response.isSuccessful()){
                                    User user = response.body().getUser();
                                    if(user == null)
                                        Toast.makeText(Login.this, "Vui lòng kiểm tra lại số điện thoại", Toast.LENGTH_SHORT).show();
                                    else{
                                        if(password.equals(user.getLocal().getPassword())){
                                            Toast.makeText(Login.this, "Ok", Toast.LENGTH_SHORT).show();
                                        }else
                                            Toast.makeText(Login.this, "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {

                            }
                        });
                    }
                }
            }
        });

    }
}