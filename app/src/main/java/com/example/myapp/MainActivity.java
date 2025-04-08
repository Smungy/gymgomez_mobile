package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String savedUser = prefs.getString("username", null);

        if (savedUser != null) {
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.putExtra("USERNAME", savedUser);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (user.equals("admin") && pass.equals("1234")) {
                SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                editor.putString("username", user);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.putExtra("USERNAME", user);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
