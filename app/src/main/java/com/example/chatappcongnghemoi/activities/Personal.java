package com.example.chatappcongnghemoi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataLoggedIn;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.example.chatappcongnghemoi.socket.MySocket;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Personal extends AppCompatActivity implements View.OnClickListener {
    private EditText input_name, input_yearOfBirth, input_numberPhone, input_address;
    private BottomNavigationView bottomNavigationView;
    private RadioGroup radiogroupGender;
    private RadioButton radioBtnMale, radioBtnFemale;
    private Button btn_update_info;
    private ImageView imageView_background;
    private CircleImageView imageView_Avatar;
    private TextView txt_introduce, txt_personal_primary, txt_search_user;
    private DataService dataService;
    private User user = null;
    private int RESULT_LOAD_IMAGE = 1024;
    private int RESULT_LOAD_BACKGROUND = 2;
    private static  final String SHARED_PREFERENCES= "saveID";
    private static Socket mSocket = MySocket.getInstance().getSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        getSupportActionBar().hide();
        new AmplifyInitialize(Personal.this).amplifyInitialize();
        //initialize variable
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        input_name = findViewById(R.id.input_personal_name);
        input_address = findViewById(R.id.input_personal_address);
        input_numberPhone = findViewById(R.id.input_personal_numberphone);
        input_yearOfBirth = findViewById(R.id.input_personal_yearOfBirth);
        btn_update_info = findViewById(R.id.btn_personal_update);
        radioBtnMale = findViewById(R.id.radio_male);
        radioBtnFemale = findViewById(R.id.radio_female);
        radiogroupGender = findViewById(R.id.radiogroup_gender);
        imageView_Avatar = findViewById(R.id.image_personal_avatar);
        txt_introduce = findViewById(R.id.txt_personal_introduce);
        txt_personal_primary = findViewById(R.id.txt_personal_name_primary);
        txt_search_user = findViewById(R.id.input_personal_search);
        imageView_background = findViewById(R.id.image_personal_background);
        //initialize dataservice
        dataService = ApiService.getService();

        //set edit text can't input letter
        radioBtnFemale.setEnabled(false);
        radioBtnMale.setEnabled(false);
        input_name.setEnabled(false);
        input_address.setEnabled(false);
        input_numberPhone.setEnabled(false);
        input_yearOfBirth.setEnabled(false);

        //set personal selected
        bottomNavigationView.setSelectedItemId(R.id.menuCaNhan);
        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.menuDanhBa:
                        startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        // create input calendar for input year of birth
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        //add onclick listener for input year of birth
        input_yearOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Personal.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day +"/"+month+"/"+year;
                        input_yearOfBirth.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //add onclick listener for button
        btn_update_info.setOnClickListener(this);

        // event dialog avatar
        imageView_Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAvatar(Gravity.CENTER);
            }
        });
        imageView_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBackground(Gravity.CENTER);
            }
        });
        //event introduce
        txt_introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Personal.this, UpdateIntroduce.class);
                intent.putExtra("introduce", txt_introduce.getText().toString());
                startActivityForResult(intent, 1);
            }
        });
        getUserById(new DataLoggedIn(this).getUserIdLoggedIn());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null){
                    txt_personal_primary.setText(user.getUserName());
                    input_name.setText(user.getUserName());
//                    input_gender.setText(user.getGender());
                    if (user.getGender().equals("male")){
                        radioBtnMale.setChecked(true);
                    }else if (user.getGender().equals("female")){
                        radioBtnFemale.setChecked(true);
                    }
                    input_yearOfBirth.setText(user.getBirthday());
                    input_numberPhone.setText(user.getLocal().getPhone());
                    input_address.setText(user.getAddress());
                    txt_introduce.setText(user.getDescription());
                    if (user.getBackground()!=null){
                        Glide.with( Personal.this).load(user.getBackground()).into(imageView_background);
                    }
                    Glide.with( Personal.this).load(user.getAvatar()).into(imageView_Avatar);
                }else {
                    handler.postDelayed(this, 500);
                }
            }
        },500);
        // onclick search
        txt_search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Personal.this.startActivity(new Intent(Personal.this, SearchUser.class));
            }
        });
        // onclick logout
        findViewById(R.id.btn_personal_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog =new Dialog(Personal.this);
                dialog.setContentView(R.layout.dialog_logout);
                Window window = dialog.getWindow();
                if(window == null)
                    return;
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                window.setLayout(layoutParams.MATCH_PARENT,layoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.setCancelable(true);
                TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                TextView tvLogout = dialog.findViewById(R.id.tvLogout_Dialog);
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                tvLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveIDLogout();
                        dialog.dismiss();
                        updateOffline();
                        Intent intent = new Intent(Personal.this,StartApp.class);
                        MySocket.getInstance().getSocket().disconnect();
                        startActivity(intent);
                        mSocket.disconnect();
                        finish();
                    }
                });
                dialog.show();
            }
        });
    }
    public void saveIDLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId","");
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_personal_update: {
                if (btn_update_info.getText().equals("C???p nh???t th??ng tin")) {
                    btn_update_info.setText("L??u");
                    //set edit text can input letter
                    radioBtnMale.setEnabled(true);
                    radioBtnFemale.setEnabled(true);
                    input_name.setEnabled(true);
                    input_address.setEnabled(true);
                    input_yearOfBirth.setEnabled(true);
                } else {
                    if (checkinput()==true){
                        btn_update_info.setText("C???p nh???t th??ng tin");
//                        input_gender.setEnabled(false);
                        radioBtnFemale.setEnabled(false);
                        radioBtnMale.setEnabled(false);
                        input_name.setEnabled(false);
                        input_address.setEnabled(false);
                        input_yearOfBirth.setEnabled(false);
                        updateUser();
                    }
                }
            }
        }
    }

    private void openDialogAvatar(int gravity){
         final Dialog dialog = new Dialog(Personal.this);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.setContentView(R.layout.layout_dialog_avatar_personal);

         Window window = dialog.getWindow();
         if (window==null){
             return;
         }

         window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

         WindowManager.LayoutParams windowAttributes = window.getAttributes();
         windowAttributes.gravity = gravity;
         window.setAttributes(windowAttributes);

         if (Gravity.CENTER == gravity){
             dialog.setCancelable(true);
         }else {
             dialog.setCancelable(false);
         }
         Button btn_fullscreen = dialog.findViewById(R.id.btn_dialogbackground_fullscreen);

         btn_fullscreen.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(Personal.this, Full_Image_Avatar.class);
                 intent.putExtra("url", user.getAvatar());
                 Personal.this.startActivity(intent);
             }
         });

         Button btn_choose_image_form_device = dialog.findViewById(R.id.btn_dialog_background_choose_device);
         btn_choose_image_form_device.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i = new Intent(
                         Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  Personal.this.startActivityForResult(i, RESULT_LOAD_IMAGE);
                  dialog.dismiss();
             }
         });

         dialog.show();
    }
    private void openDialogBackground(int gravity){
        final Dialog dialog = new Dialog(Personal.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_blackground_personal);

        Window window = dialog.getWindow();
        if (window==null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        }else {
            dialog.setCancelable(false);
        }
        Button btn_fullscreen = dialog.findViewById(R.id.btn_dialogbackground_fullscreen);

        btn_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Personal.this, Full_Image_Avatar.class);
                intent.putExtra("url", user.getBackground());
                Personal.this.startActivity(intent);
            }
        });

        Button btn_choose_image_form_device = dialog.findViewById(R.id.btn_dialog_background_choose_device);
        btn_choose_image_form_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Personal.this.startActivityForResult(i, RESULT_LOAD_BACKGROUND);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                if (!result.equals("")) {
                    user.setDescription(result);
                    Call<UserDTO> putCall = dataService.updateUser(user.getId(), user);
                    putCall.enqueue(new Callback<UserDTO>() {
                        @Override
                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                            Toast.makeText(Personal.this, "C???p Nh???t Gi???i Thi???u Th??nh C??ng !", Toast.LENGTH_LONG).show();
                            txt_introduce.setText(user.getDescription().toString());
                        }
                        @Override
                        public void onFailure(Call<UserDTO> call, Throwable t) {
                            System.err.println("Fail update description"+ t.getMessage().toString());
                        }
                    });
                }
            }
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            File file1 = new File(getPath(selectedImage));
            UUID uuid = UUID.randomUUID();
            try {
                InputStream exampleInputStream = getContentResolver().openInputStream(selectedImage);
                com.amplifyframework.core.Amplify.Storage.uploadInputStream(
                        uuid+"."+file1.getName(),
                        exampleInputStream,
                        result -> {
                            Toast.makeText(Personal.this, "Ho??n T???t", Toast.LENGTH_LONG).show();
                            String url = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/"+uuid+"."+file1.getName();
                            updateImage(uuid+"."+file1.getName());
                            Glide.with(Personal.this).load(url).into(imageView_Avatar);
                        },
                        storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                );
            }  catch ( FileNotFoundException error) {
                Log.e("MyAmplifyApp", "Could not find file to open for input stream.", error);
            }
        }
        if (requestCode == RESULT_LOAD_BACKGROUND && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            File file1 = new File(getPath(selectedImage));
            UUID uuid = UUID.randomUUID();
            try {
                InputStream exampleInputStream = getContentResolver().openInputStream(selectedImage);
                com.amplifyframework.core.Amplify.Storage.uploadInputStream(
                        uuid+"."+file1.getName(),
                        exampleInputStream,
                        result -> {
                            Toast.makeText(Personal.this, "Ho??n T???t", Toast.LENGTH_LONG).show();
                            user.setBackground(uuid+"."+file1.getName());
                            Call<UserDTO> userDTOCall = dataService.updateUser(user.getId(), user);
                            userDTOCall.enqueue(new Callback<UserDTO>() {
                                @Override
                                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                    Glide.with(Personal.this).load(user.getBackground()).into(imageView_background);
                                }

                                @Override
                                public void onFailure(Call<UserDTO> call, Throwable t) {

                                }
                            });
                        },
                        storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                );
            }  catch ( FileNotFoundException error) {
                Log.e("MyAmplifyApp", "Could not find file to open for input stream.", error);
            }
        }
    }
    private void getUserById(String id){
        Call<UserDTO> dtoCall = dataService.getUserById(id);
        dtoCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                user = response.body().getUser();
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(Personal.this, "Fail get User By Id", Toast.LENGTH_SHORT).show();
                System.err.println("Fail get User By Id"+t.toString());
            }
        });
    }
    private void updateUser(){
        if (checkinput()==true){
            user.setUserName(input_name.getText().toString());
//            user.setGender(input_gender.getText().toString());
            String gender = "";
            if (radioBtnFemale.isChecked()){
                gender = "female";
            }
            if (radioBtnMale.isChecked()){
                gender = "male";
            }
            user.setGender(gender);
            user.setBirthday(input_yearOfBirth.getText().toString());
            user.getLocal().setPhone(input_numberPhone.getText().toString());
            user.setAddress(input_address.getText().toString());

            Call<UserDTO> putCall = dataService.updateUser(user.getId(), user);
            putCall.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    Toast.makeText(Personal.this, "Th??nh C??ng !", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    System.err.println("Fail Put User "+ t.getMessage().toString());
                }
            });
        }
    }
    private boolean checkinput(){
        boolean kq = true;
        if (input_name.getText().toString().trim().equals("")){
            Toast.makeText(this, "T??n kh??ng ???????c tr???ng", Toast.LENGTH_LONG).show();
            kq = false;
        }
        if (input_yearOfBirth.getText().toString().trim()!=null){
            try {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                df.setLenient(false);
                System.out.println( df.parse(input_yearOfBirth.getText().toString().trim()));
            } catch (ParseException e) {
                Toast.makeText(this, "Ng??y sinh ph???i theo d???ng Ng??y/Th??ng/N??m", Toast.LENGTH_LONG).show();
                kq = false;
            }
        }
        if (input_numberPhone.getText().toString().trim().length()!=10){
            Toast.makeText(this, "S??? ??i???n tho???i ph???i c?? 10 s???", Toast.LENGTH_LONG).show();
            kq = false;
        }
        try {
            Long i = Long.parseLong(input_numberPhone.getText().toString().trim());
        }catch (NumberFormatException e){
            Toast.makeText(this, "S??? ??i???n tho???i ph???i l?? chu???i s???", Toast.LENGTH_LONG).show();
            kq = false;
        }
        return kq;
    }

    private void updateImage(String url){
        user.setAvatar(url);
        Call<UserDTO> putCall = dataService.updateUser(user.getId(), user);
        putCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                Toast.makeText(Personal.this, "???? c???p nh???t AVATAR m???i!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                System.err.println("Fail Put User "+ t.getMessage().toString());
            }
        });
    }
    private String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    public void updateOffline(){
        user.setOnline(false);
        user.setUpdatedAt(new Date().getTime());
        Call<UserDTO> call = dataService.updateUser(user.getId(),user);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {

            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
    }
}