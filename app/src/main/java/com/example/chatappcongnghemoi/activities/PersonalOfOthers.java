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
import com.example.chatappcongnghemoi.models.Notification;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.ContactSocket;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

public class PersonalOfOthers extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private TextView txtMainUserName, txtUserName, txtBirth, txtGender, txtPhone, txtAddress;
    private Button btnBack, btnAddFriend, btnAddNewMessage;

    private User user;
    private DataService dataService;
    private Contact contact;
    private static Socket mSocket = MySocket.getInstance().getSocket();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_of_others);
        Objects.requireNonNull(getSupportActionBar()).hide();

        imgAvatar = findViewById(R.id.personal_friend_image_personal_avatar);
        txtMainUserName = findViewById(R.id.txt_personal_friend_name_primary);
        txtUserName = findViewById(R.id.input_personal_friend_name);
        txtBirth = findViewById(R.id.input_personal_friend_yearOfBirth);
        txtGender = findViewById(R.id.input_personal_friend_gender);
        txtPhone = findViewById(R.id.input_personal_friend_numberphone);
        txtAddress = findViewById(R.id.input_personal_friend_address);
        btnBack = findViewById(R.id.button13);
        btnAddFriend = findViewById(R.id.button9);
        btnAddNewMessage = findViewById(R.id.btn_addnewmessage);
        dataService = ApiService.getService();

        mSocket.on("response-deny-friend-request", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                restartActivity(PersonalOfOthers.this);
            }
        });
        mSocket.on("response-accept-Friend-Request",  new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
               restartActivity(PersonalOfOthers.this);
            }
        });

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
                if (btnAddFriend.getText().toString().equals("K???t b???n")) {
                    postContact();
                    new ContactSocket().addNewContact(user);
                } else {
                    deleteContact();
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

        btnAddNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PersonalOfOthers.this, ChatBox.class);
                intent1.putExtra("friendId", user.getId());
                PersonalOfOthers.this.startActivity(intent1);
            }
        });
    }

    private void postContact() {
        Contact contact = new Contact();
        contact.setSenderId(new DataLoggedIn(this).getUserIdLoggedIn());
        contact.setReceiverId(user.getId());
        contact.setStatus(false);

        postNotification(contact);

        Call<ContactDTO> callback = dataService.postContact(contact);
        callback.enqueue(new Callback<ContactDTO>() {
            @Override
            public void onResponse(Call<ContactDTO> call, Response<ContactDTO> response) {
                Toast.makeText(PersonalOfOthers.this, "G???i l???i m???i k???t b???n th??nh c??ng", Toast.LENGTH_SHORT).show();
                restartActivity(PersonalOfOthers.this);
            }

            @Override
            public void onFailure(Call<ContactDTO> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    private void postNotification(Contact contact_) {
        Notification notification = new Notification();
        notification.setSenderId(contact_.getSenderId());
        notification.setReceiverId(contact_.getReceiverId());
        notification.setType("add_contact");


        Call<Notification> callback = dataService.postNotification(notification);
        callback.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {

            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {

            }
        });
    }

    private void deleteContact() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        if (btnAddFriend.getText().toString().equals("H???y y??u c???u"))
                            new ContactSocket().removeRequestContact(user);
                        else if (btnAddFriend.getText().toString().equals("H???y k???t b???n"))
                            new ContactSocket().deleteFriend(user);
                        deleteApi(contact.getId());
                        getNotificationToDelete(contact.getSenderId(), contact.getReceiverId());
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (btnAddFriend.getText().toString().equals("H???y y??u c???u"))
            builder.setMessage("B???n c?? mu???n thu h???i l???i m???i k???t b???n n??y?")
                    .setPositiveButton("Kh??ng", dialogClickListener)
                    .setNegativeButton("C??", dialogClickListener).show();
        else if (btnAddFriend.getText().toString().equals("H???y k???t b???n"))
            builder.setMessage("B???n c?? mu???n x??a y??u c???u k???t b???n n??y?")
                    .setPositiveButton("Kh??ng", dialogClickListener)
                    .setNegativeButton("C??", dialogClickListener).show();

    }

    private void deleteApi(String id) {
        DataService dataService = ApiService.getService();
        Call<String> callback = dataService.deteleContactById(id);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (btnAddFriend.getText().toString().equals("H???y y??u c???u"))
                    Toast.makeText(PersonalOfOthers.this, "Thu h???i l???i m???i k???t b???n th??nh c??ng", Toast.LENGTH_SHORT).show();
                else if (btnAddFriend.getText().toString().equals("H???y k???t b???n"))
                    Toast.makeText(PersonalOfOthers.this, "X??a b???n b?? th??nh c??ng", Toast.LENGTH_SHORT).show();
                restartActivity(PersonalOfOthers.this);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void deleteNotification(String id) {
        DataService dataService = ApiService.getService();
        Call<String> callback = dataService.deleteNotification(id);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getNotificationToDelete(String senderId, String receiverId) {
        DataService dataService = ApiService.getService();
        Call<Notification> callback = dataService.getNotificationByContact(senderId, receiverId);
        callback.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
               if(response.body().getId() != null) {
                   deleteNotification(response.body().getId());
               }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {

            }
        });
    }

    private void checkFriend() {
        Call<ContactDTO> callback = dataService.checkContact(new DataLoggedIn(this).getUserIdLoggedIn(), user.getId());
        callback.enqueue(new Callback<ContactDTO>() {
            @Override
            public void onResponse(Call<ContactDTO> call, Response<ContactDTO> response) {
                contact = response.body().getContact();
                if (contact == null)
                    btnAddFriend.setText("K???t b???n");
                else if (contact != null && contact.getStatus())
                    btnAddFriend.setText("H???y k???t b???n");
                else
                    btnAddFriend.setText("H???y y??u c???u");
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