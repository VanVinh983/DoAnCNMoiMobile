package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalOfOthers extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private TextView txtMainUserName, txtUserName, txtEmail, txtBirth, txtGender, txtPhone, txtAddress;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_of_others);

        imgAvatar = findViewById(R.id.personal_friend_image_personal_avatar);
        txtMainUserName = findViewById(R.id.txt_personal_friend_name_primary);
        txtUserName = findViewById(R.id.input_personal_friend_name);
        txtEmail = findViewById(R.id.input_personal_friend_email);
        txtBirth = findViewById(R.id.input_personal_friend_yearOfBirth);
        txtGender = findViewById(R.id.input_personal_friend_gender);
        txtPhone = findViewById(R.id.input_personal_friend_numberphone);
        txtAddress = findViewById(R.id.input_personal_friend_address);

        Intent intent = getIntent();
        if(intent.hasExtra("user")){
            user = (User) intent.getParcelableExtra("user");
            //Log.d("tag", user.getUserName());
            Picasso.get().load(user.getAvatar()).into(imgAvatar);
            txtMainUserName.setText(user.getUserName());
            txtUserName.setText(user.getUserName());
            txtBirth.setText(user.getBirthday());
            txtGender.setText(user.getGender());
            txtPhone.setText(user.getLocal().getPhone());
            txtAddress.setText(user.getAddress());
        }

    }
}