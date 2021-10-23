package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chatappcongnghemoi.R;

public class UpdateIntroduce extends AppCompatActivity {
    private EditText input_introduce;
    private Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_introduce);
        getSupportActionBar().hide();

        input_introduce = findViewById(R.id.input_updateintroduce_introduce);
        btn_save = findViewById(R.id.btn_updateintroduce_save);

        Intent intent = getIntent();
        String introduce = intent.getStringExtra("introduce");

        if (!introduce.equals("")){
            input_introduce.setText(introduce);
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = input_introduce.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", result);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        findViewById(R.id.btn_introduce_callback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}