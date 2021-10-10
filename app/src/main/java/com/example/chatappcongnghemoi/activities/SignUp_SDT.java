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

public class SignUp_SDT extends AppCompatActivity {

    Button btnNext;
    EditText txtSDT;
    FirebaseAuth auth;
    ImageView imgBackSignUpSDT;
    DataService dataService;
    public static final  String TAG = SignUp_SDT.class.getName();
    String username,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sdt);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        btnNext = findViewById(R.id.btnNextSignUpSDT);
        txtSDT = findViewById(R.id.txtSDT_SignUp);
        auth = FirebaseAuth.getInstance();
        imgBackSignUpSDT = findViewById(R.id.imgBackSignUpSDT);
        imgBackSignUpSDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_SignUpSDT_SignUp = new Intent(SignUp_SDT.this,SignUp.class);
                startActivity(intent_SignUpSDT_SignUp);
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
                                sendOTP("+84"+sdt.substring(1));
                            }else{
                                Toast.makeText(SignUp_SDT.this, "Số điện thoại đã đăng ký tài khoản", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SignUp_SDT.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
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
        phone = txtSDT.getText().toString().trim();
        Intent intent_OPT = new Intent(SignUp_SDT.this,SignUp_OTP.class);
        intent_OPT.putExtra("phone_number",phoneNumber);
        intent_OPT.putExtra("verificationId",verificationId);
        intent_OPT.putExtra("username",username);
        intent_OPT.putExtra("phone",phone);
        startActivity(intent_OPT);
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
                            Intent intent = new Intent(SignUp_SDT.this,Login.class);
                            intent.putExtra("phone_number",user.getPhoneNumber());
                            startActivity(intent);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUp_SDT.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}