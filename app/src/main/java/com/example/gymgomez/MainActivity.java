package com.example.gymgomez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymgomez.modelos.LoginRequest;
import com.example.gymgomez.modelos.LoginResponse;
import com.example.gymgomez.networking.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText editTextId;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String idStr = editTextId.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (idStr.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID debe ser un número", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        LoginRequest loginRequest = new LoginRequest(id, password, "android_app");

        RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Guardar token en SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
                    preferences.edit().putString("token", "Bearer " + loginResponse.getToken()).apply();

                    // Verificar si requiere cambio de contraseña o es primer login
                    if (loginResponse.isFirstLogin() || loginResponse.isRequiresPasswordChange()) {
                        // Ir a la pantalla de cambio de contraseña
                        Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Ir a la pantalla principal (caso de éxito normal)
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // Manejar error de respuesta
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Cuerpo de error vacío";
                        Toast.makeText(MainActivity.this,
                                "Error: " + response.code() + " - " + errorBody,
                                Toast.LENGTH_LONG).show();
                        Log.e("LoginError", "Code: " + response.code() + " Body: " + errorBody);
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this,
                                "Error de inicio de sesión: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
