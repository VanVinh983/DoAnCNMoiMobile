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
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Local;
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
import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp_OTP extends AppCompatActivity {
    ImageView imgBack;
    Button btnConfirm;
    EditText txtOTP;
    TextView tvResendOTP;
    FirebaseAuth auth;
    DataService dataService;
    String phone,phone_number,mVerificationId,username;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    public static final  String TAG = SignUp_SDT.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_otp);
        getSupportActionBar().hide();
        imgBack = findViewById(R.id.imgBackSignUpOTP);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtOTP = findViewById(R.id.txtOTP_SignUp);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        phone_number = intent.getStringExtra("phone_number");
        mVerificationId = intent.getStringExtra("verificationId");
        username = intent.getStringExtra("username");
        Toast.makeText(SignUp_OTP.this, phone, Toast.LENGTH_SHORT).show();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp_OTP.this,SignUp_SDT.class);
                startActivity(intent);
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = txtOTP.getText().toString().trim();
                confirmOTP(otp);
            }
        });
        tvResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTPAgain(phone_number);
            }
        });
    }

    private void confirmOTP(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
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
                            createUser();
                            goToPassword(user.getPhoneNumber());

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUp_OTP.this, "Mã xác nhận không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void createUser(){
        dataService = ApiService.getService();
        Local local = new Local();
        local.setPhone(phone);
        local.setPassword(BCrypt.hashpw("123456",BCrypt.gensalt(10)));
        User user = new User(local,username);
        user.setActive(true);
        Call<UserDTO> callback = dataService.createUser(user);
        callback.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if(response.isSuccessful())
                    Toast.makeText(SignUp_OTP.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SignUp_OTP.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
    }
    private void goToPassword(String phoneNumber) {
        // them user vao database set password = 123456 chuyển sang activity confirm password để đổi password
        Intent intent = new Intent(SignUp_OTP.this,ConfirmPassword.class);
        intent.putExtra("phone",phone);
        startActivity(intent);
        finish();
    }
    public void sendOTPAgain(String sdt){
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
                                Toast.makeText(SignUp_OTP.this, "Gửi lại mã xác nhận không thành công", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                goToSignUpOTP(sdt,verificationId);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void goToSignUpOTP(String phoneNumber,String verificationId){
        Intent intent_OPT = new Intent(SignUp_OTP.this,SignUp_OTP.class);
        intent_OPT.putExtra("phone_number",phoneNumber);
        intent_OPT.putExtra("verificationId",verificationId);
        intent_OPT.putExtra("username",username);
        intent_OPT.putExtra("phone",phone);
        startActivity(intent_OPT);
        finish();
    }
}