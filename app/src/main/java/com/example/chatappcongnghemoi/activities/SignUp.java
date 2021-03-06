package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Local;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    EditText txtUsername;
    Button btnNext,btnLogin;
    ImageView btnBack;
    private DataService dataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        txtUsername = findViewById(R.id.txtUsernameSignUp);
        btnNext = findViewById(R.id.btnNextSignUp);
        btnLogin = findViewById(R.id.btnLogin_SignUp);
        btnBack = findViewById(R.id.imgBackSignUp);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,StartApp.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString().trim();
                if(username.matches("^[\\u0041-\\u00ff\\u0100-\\u013c\\u01cd-\\u01ce\\s]{2,40}$")) {
                    Intent intent = new Intent(SignUp.this,SignUp_SDT.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(SignUp.this, "T??n ng?????i d??ng t??? 2-40 k?? t???, kh??ng ch???a k?? t??? s??? v?? k?? t??? ?????c bi???t", Toast.LENGTH_SHORT).show();
            }
        });
    }
}