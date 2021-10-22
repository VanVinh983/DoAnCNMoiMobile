package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity {
    ImageView imgBack;
    EditText txtSDT;
    Button btnNext;
    DataService dataService;
    FirebaseAuth auth;
    String phone;
    public static final  String TAG = SignUp_SDT.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().hide();
        imgBack = findViewById(R.id.imgBack_ForgetPassword);
        txtSDT = findViewById(R.id.txtSDT_ForgetPassword);
        btnNext = findViewById(R.id.btnNextForgetPassword);
        auth = FirebaseAuth.getInstance();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdt = txtSDT.getText().toString().trim();
                dataService = ApiService.getService();
                Call<UserDTO> callback = dataService.getUserByPhone(sdt);
                callback.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if(response.isSuccessful()){
                            User user = response.body().getUser();
                            if(user == null){
                                Toast.makeText(ForgetPassword.this, "Số điện thoại chưa đăng ký tài khoản", Toast.LENGTH_SHORT).show();
                            }else{
                                sendOTP("+84"+sdt.substring(1));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                    }
                });
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            // Update UI
                            Intent intent = new Intent(ForgetPassword.this,ForgetPassword_OTP.class);
                            intent.putExtra("phone_number",user.getPhoneNumber());
                            startActivity(intent);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(ForgetPassword.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public void sendOTP(String sdt){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(sdt)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ForgetPassword.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                goToForgetPasswordOTP(sdt,verificationId);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void goToForgetPasswordOTP(String phoneNumber,String verificationId){
        phone = txtSDT.getText().toString().trim();
        Intent intent_OPT = new Intent(ForgetPassword.this,ForgetPassword_OTP.class);
        intent_OPT.putExtra("phone_number",phoneNumber);
        intent_OPT.putExtra("verificationId",verificationId);
        intent_OPT.putExtra("phone",phone);
        startActivity(intent_OPT);
        finish();
    }
}