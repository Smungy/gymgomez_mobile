package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvSplash = findViewById(R.id.tvSplash);

        Animation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(500);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setRepeatCount(Animation.INFINITE);
        tvSplash.startAnimation(fade);

        String username = getIntent().getStringExtra("USERNAME");

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, InfoActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        }, 2500);
    }
}
