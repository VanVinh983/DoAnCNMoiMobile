package com.example.chatappcongnghemoi.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactDTO;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

public class PersonalOfOthers extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private TextView txtMainUserName, txtUserName, txtBirth, txtGender, txtPhone, txtAddress;
    private Button btnBack, btnAddFriend;

    private User user;
    private DataService dataService;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_of_others);

        imgAvatar = findViewById(R.id.personal_friend_image_personal_avatar);
        txtMainUserName = findViewById(R.id.txt_personal_friend_name_primary);
        txtUserName = findViewById(R.id.input_personal_friend_name);
        txtBirth = findViewById(R.id.input_personal_friend_yearOfBirth);
        txtGender = findViewById(R.id.input_personal_friend_gender);
        txtPhone = findViewById(R.id.input_personal_friend_numberphone);
        txtAddress = findViewById(R.id.input_personal_friend_address);
        btnBack = findViewById(R.id.button13);
        btnAddFriend = findViewById(R.id.button9);

        dataService = ApiService.getService();

        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            user = (User) intent.getParcelableExtra("user");
            //Log.d("tag", user.getUserName());
            Picasso.get().load(user.getAvatar()).into(imgAvatar);
            txtMainUserName.setText(user.getUserName());
            txtUserName.setText(user.getUserName());
            txtBirth.setText(user.getBirthday());
            txtGender.setText(user.getGender());
            txtPhone.setText(user.getLocal().getPhone());
            txtAddress.setText(user.getAddress());
            checkFriend();
        }

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAddFriend.getText().toString().equals("Hủy kết bạn")) {
                    deleteContact();
                } else {
                    postContact();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(PersonalOfOthers.this, PhoneBookActivity.class));
            }
        });

    }

    private void postContact() {
        Contact contact = new Contact();
        contact.setSenderId(DataLoggedIn.userIdLoggedIn);
        contact.setReceiverId(user.getId());
        contact.setStatus(false);

        Call<ContactDTO> callback = dataService.postContact(contact);
        callback.enqueue(new Callback<ContactDTO>() {
            @Override
            public void onResponse(Call<ContactDTO> call, Response<ContactDTO> response) {
                Toast.makeText(PersonalOfOthers.this, "Gửi lời mời kết bạn thành công", Toast.LENGTH_SHORT).show();
                restartActivity(PersonalOfOthers.this);
            }

            @Override
            public void onFailure(Call<ContactDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void deleteContact() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        deleteApi(contact.getId());
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xóa yêu cầu kết bạn này?")
                .setPositiveButton("Không", dialogClickListener)
                .setNegativeButton("Có", dialogClickListener).show();
    }

    private void deleteApi(String id) {
        DataService dataService = ApiService.getService();
        Call<String> callback = dataService.deteleContactById(id);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(PersonalOfOthers.this, "Xóa bạn bè thành công", Toast.LENGTH_SHORT).show();
                restartActivity(PersonalOfOthers.this);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void checkFriend() {
        Call<ContactDTO> callback = dataService.checkContact(DataLoggedIn.userIdLoggedIn, user.getId());
        callback.enqueue(new Callback<ContactDTO>() {
            @Override
            public void onResponse(Call<ContactDTO> call, Response<ContactDTO> response) {
                contact = response.body().getContact();
                if (contact == null)
                    btnAddFriend.setText("Kết bạn");
                else
                    btnAddFriend.setText("Hủy kết bạn");
            }

            @Override
            public void onFailure(Call<ContactDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void restartActivity(Activity act) {
        Intent intent = new Intent(act, act.getClass());
        intent.putExtra("user", user);
        act.finish();
        act.startActivity(intent);
    }
}