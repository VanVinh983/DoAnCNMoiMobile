package com.example.chatappcongnghemoi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator2;

public class StartApp extends AppCompatActivity {
    ViewPager2 viewPager2;
    LinearLayout linearLayout;
    StartAdapter adapter;
    TextView[] dots;
    List<StartItem> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        getSupportActionBar().hide();
        list.add(new StartItem("AAAAAAAAAAAAAAAAA",R.drawable.background_login_screen));
        list.add(new StartItem("BBBBBBBBBBBBBBBBB",R.drawable.iconappchat));
        list.add(new StartItem("CCCCCCCCCCCCCCCCC",R.drawable.background_welcome_screen));
        viewPager2 = findViewById(R.id.viewpager);
        linearLayout = findViewById(R.id.linearStart);
        adapter  = new StartAdapter(this,list);
        viewPager2.setAdapter(adapter);
        dots = new TextView[list.size()];
        dotsIndicator();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectIndicator(position);
                super.onPageSelected(position);
            }
        });

    }

    private void selectIndicator(int position) {
        for (int i = 0;i<dots.length;i++){
            if(i == position)
                dots[i].setTextColor(Color.BLACK);
            else
                dots[i].setTextColor(Color.GRAY);
        }
    }

    private void dotsIndicator() {
        for (int i = 0 ; i< dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#6979;"));
            dots[i].setTextSize(18);
            linearLayout.addView(dots[i]);

        }
    }

}