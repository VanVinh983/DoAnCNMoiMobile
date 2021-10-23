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

public class ChangePassword extends AppCompatActivity {
    EditText txtPassword,txtPassword2;
    Button btnDone;
    String phone;
    DataService dataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();
        txtPassword = findViewById(R.id.txtPassword_ChangePassword);
        txtPassword2 = findViewById(R.id.txtPassword2_ChangePassword);
        btnDone = findViewById(R.id.btnDone_ChangePassword);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataService = ApiService.getService();
                String password=  txtPassword.getText().toString();
                String password2 = txtPassword2.getText().toString();
                if(password.matches("^[a-zA-Z0-9]{6,32}$")){
                    if(password2.equals(password)) {
                        Call<UserDTO> callback = dataService.getUserByPhone(phone);
                        callback.enqueue(new Callback<UserDTO>() {
                            @Override
                            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                if(response.isSuccessful()){
                                    User user = response.body().getUser();
                                    Local local = user.getLocal();
                                    local.setPassword(BCrypt.hashpw(password,BCrypt.gensalt(10)));
                                    user.setLocal(local);
                                    Call<UserDTO> callUpdateUser = dataService.updateUser(user.getId(),user);
                                    callUpdateUser.enqueue(new Callback<UserDTO>() {
                                        @Override
                                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                            if(response.isSuccessful()){
                                                Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                Intent intent_Login = new Intent(ChangePassword.this,Login.class);
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
                    }else{
                        Toast.makeText(ChangePassword.this, "Nhập lại mật khẩu phải trùng với mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ChangePassword.this, "Mật khẩu phải có từ 6 đến 32 ký tự và không chứa ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}