package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Local;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import org.mindrot.jbcrypt.BCrypt;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPassword extends AppCompatActivity {
    DataService dataService;
    EditText txtPassword,txtPassword2;
    Button btnDone;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        btnDone = findViewById(R.id.btnDone);
        txtPassword = findViewById(R.id.txtPassword);
        txtPassword2 = findViewById(R.id.txtPassword2);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtPassword.getText().toString();
                String password2 = txtPassword2.getText().toString();
                if(password.matches("^[a-zA-Z0-9]{6,32}$")){
                    if(password.equals(password2)){
                        dataService = ApiService.getService();
                        Call<UserDTO> callback = dataService.getUserByPhone(phone);
                        callback.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                if(response.isSuccessful()){
                                    User user = response.body().getUser();
                                    Local local = user.getLocal();
                                    local.setPassword(BCrypt.hashpw(password,BCrypt.gensalt(10)));
                                    user.setLocal(local);
                                    Call<UserDTO> callbackUpdatePassword = dataService.updateUser(user.getId(),user);
                                    callbackUpdatePassword.enqueue(new Callback<UserDTO>() {
                                        @Override
                                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                            if(response.isSuccessful()) {
                                                Toast.makeText(ConfirmPassword.this, "C???p nh???t m???t kh???u th??nh c??ng", Toast.LENGTH_SHORT).show();
                                                Intent intent_Login = new Intent(ConfirmPassword.this,Login.class);
                                                startActivity(intent_Login);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserDTO> call, Throwable t) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDTO> call, Throwable t) {

                            }
                        });
                    }else
                        Toast.makeText(ConfirmPassword.this, "Nh???p l???i m???t kh???u ph???i tr??ng v???i m???t kh???u", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ConfirmPassword.this, "M???t kh???u ph???i c?? t??? 6 ?????n 32 k?? t??? v?? kh??ng ch???a k?? t??? ?????c bi???t", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}