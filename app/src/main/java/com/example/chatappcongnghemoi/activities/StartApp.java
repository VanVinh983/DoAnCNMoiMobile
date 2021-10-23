package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.StartAdapter;
import com.example.chatappcongnghemoi.models.StartItem;

import java.util.ArrayList;
import java.util.List;


public class StartApp extends AppCompatActivity {
    ViewPager2 viewPager2;
    LinearLayout linearLayout;
    StartAdapter adapter;
    TextView[] dots;
    RecyclerView recyclerView;
    List<StartItem> list = new ArrayList<>();
    Button btnDangKy, btnDangNhap;
    public static  final String SHARED_PREFERENCES= "saveID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        getSupportActionBar().hide();
        autoLogin();
        list.add(new StartItem("Nơi cùng nhau trao đổi, liên lạc với người khác",R.drawable._028004));
        list.add(new StartItem("Nơi giúp bạn tìm kiếm tri kỷ của đời mình",R.drawable._49058));
        list.add(new StartItem("Trao đổi hình ảnh chất lượng cao với người khác thật nhanh và dễ dàng",R.drawable.collection_tour));
        viewPager2 = findViewById(R.id.viewpager);
        linearLayout = findViewById(R.id.linearStart);
        adapter  = new StartAdapter(list);
        viewPager2.setAdapter(adapter);
        viewPager2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dots = new TextView[list.size()];
        dotsIndicator();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectIndicator(position);
                super.onPageSelected(position);
            }
        });

        btnDangKy = findViewById(R.id.btnDangKyStartApp);
        btnDangNhap = findViewById(R.id.btnDangNhapStartApp);
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartApp.this,SignUp.class);
                startActivity(intent);

            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartApp.this,Login.class);
                startActivity(intent);

            }
        });

    }

    private void selectIndicator(int position) {
        for (int i = 0;i<dots.length;i++){
            if(i == position)
                dots[i].setTextColor(Color.BLACK);
            else
                dots[i].setTextColor(Color.WHITE);
        }
    }

    private void dotsIndicator() {
        for (int i = 0 ; i< dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9679;"));
            dots[i].setTextSize(18);
            linearLayout.addView(dots[i]);

        }
    }
    public void autoLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        String id = sharedPreferences.getString("userId","");
        if(id.equals(""))
            return;
        else{
            Intent intent = new Intent(StartApp.this,Home.class);
            startActivity(intent);
            finish();
        }
    }

}