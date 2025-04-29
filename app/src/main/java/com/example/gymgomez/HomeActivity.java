package com.example.gymgomez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymgomez.modelos.LogoutResponse;
import com.example.gymgomez.modelos.MiembroResponse;
import com.example.gymgomez.networking.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private TextView textViewWelcome;
    private Button buttonMiembro;
    private Button buttonMembresia;
    private Button buttonQr;
    private Button buttonLogout;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonMiembro = findViewById(R.id.buttonMiembro);
        buttonMembresia = findViewById(R.id.buttonMembresia);
        buttonQr = findViewById(R.id.buttonQr);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Cargar datos del miembro
        loadMiembroData();

        buttonMiembro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MiembroActivity.class);
                startActivity(intent);
            }
        });

        buttonMembresia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MembresiaActivity.class);
                startActivity(intent);
            }
        });

        buttonQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, QrActivity.class);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void loadMiembroData() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        Log.d(TAG, "Intentando cargar datos del miembro con token: " + token);

        RetrofitClient.getApiService().getMiembro(token).enqueue(new Callback<MiembroResponse>() {
            @Override
            public void onResponse(Call<MiembroResponse> call, Response<MiembroResponse> response) {
                Log.d(TAG, "Respuesta del servidor: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    MiembroResponse responseData = response.body();
                    MiembroResponse.Miembro miembro = responseData.getMiembro();

                    if (miembro != null) {
                        String nombre = miembro.getNombre() != null ? miembro.getNombre() : "";
                        String apellido = miembro.getApellido() != null ? miembro.getApellido() : "";

                        Log.d(TAG, "Datos del miembro obtenidos: " + nombre + " " + apellido);
                        textViewWelcome.setText("Bienvenido, " + nombre + " " + apellido);
                    } else {
                        Log.e(TAG, "El objeto miembro es nulo");
                        textViewWelcome.setText("Bienvenido");
                        Toast.makeText(HomeActivity.this, "No se pudo obtener la información del miembro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error: " + errorBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    textViewWelcome.setText("Bienvenido");
                    Toast.makeText(HomeActivity.this, "Error al cargar datos del miembro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MiembroResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                textViewWelcome.setText("Bienvenido");
                Toast.makeText(HomeActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        RetrofitClient.getApiService().logout(token).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.d(TAG, "Logout exitoso, código: " + response.code());

                preferences.edit().remove("token").apply();

                // Volver a la pantalla de login
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Log.e(TAG, "Error en logout: " + t.getMessage(), t);

                preferences.edit().remove("token").apply();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                Toast.makeText(HomeActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}