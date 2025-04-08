package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    TextView tvInfo;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tvInfo = findViewById(R.id.tvInfo);
        btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("USERNAME");
        tvInfo.setText("Bienvenido " + username + "!\nAquí tienes tu información.");

        btnLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
