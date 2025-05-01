package com.example.gymgomez;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymgomez.modelos.MiembroResponse;
import com.example.gymgomez.networking.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiembroActivity extends AppCompatActivity {
    private TextView tvId;
    private TextView tvNombre;
    private TextView tvApellido;
    // Más TextViews según la respuesta de la API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miembro);

        tvId = findViewById(R.id.tvId);
        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);

        loadMiembroData();
    }

    private void loadMiembroData() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        // Aseguramos que el token tenga el prefijo "Bearer "
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        RetrofitClient.getApiService().getMiembro(token).enqueue(new Callback<MiembroResponse>() {
            @Override
            public void onResponse(Call<MiembroResponse> call, Response<MiembroResponse> response) {
                Log.d("API_RESPONSE", "Código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    MiembroResponse responseData = response.body();
                    MiembroResponse.Miembro miembro = responseData.getMiembro();

                    if (miembro != null) {
                        tvId.setText("ID: " + miembro.getId());
                        tvNombre.setText("Nombre: " + miembro.getNombre());
                        tvApellido.setText("Apellido: " + miembro.getApellido());
                        // Mostrar más datos según la respuesta
                    } else {
                        Log.e("API_RESPONSE", "El objeto miembro es nulo");
                        Toast.makeText(MiembroActivity.this, "Datos de miembro no encontrados", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Log.e("API_RESPONSE", "Error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MiembroActivity.this, "Error al cargar datos del miembro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MiembroResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error de conexión: " + t.getMessage(), t);
                Toast.makeText(MiembroActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}