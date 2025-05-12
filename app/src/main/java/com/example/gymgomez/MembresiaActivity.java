package com.example.gymgomez;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymgomez.modelos.MembresiaResponse;
import com.example.gymgomez.networking.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembresiaActivity extends AppCompatActivity {
    private static final String TAG = "MembresiaActivity";
    private TextView tvTipo;
    private TextView tvFechaInicio;
    private TextView tvFechaFin;
    private TextView tvEstado;
    private TextView tvDiasRestantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membresia);

        tvTipo = findViewById(R.id.tvTipo);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFin = findViewById(R.id.tvFechaFin);
        tvEstado = findViewById(R.id.tvEstado);
        tvDiasRestantes = findViewById(R.id.tvDiasRestantes);

        loadMembresiaData();
    }

    private void loadMembresiaData() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        // Asegurarse de que el token tenga el prefijo "Bearer "
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        Log.d(TAG, "Intentando cargar datos de membresía con token: " + token);

        RetrofitClient.getApiService().getMembresia(token).enqueue(new Callback<MembresiaResponse>() {
            @Override
            public void onResponse(Call<MembresiaResponse> call, Response<MembresiaResponse> response) {
                Log.d(TAG, "Código de respuesta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    MembresiaResponse membresia = response.body();
                    Log.d(TAG, "Respuesta recibida: " + membresia);

                    // Verificamos que los datos no sean nulos
                    String tipoText = membresia.getTipoMembresia() != null ? membresia.getTipoMembresia() : "No disponible";
                    String fechaInicio = membresia.getFechaInscripcion() != null ? membresia.getFechaInscripcion() : "No disponible";
                    String fechaFin = membresia.getFechaVencimiento() != null ? membresia.getFechaVencimiento() : "No disponible";

                    tvTipo.setText("" + tipoText);
                    tvFechaInicio.setText("" + fechaInicio);
                    tvFechaFin.setText("Fecha de vencimiento: " + fechaFin);


                    if (tvEstado != null && membresia.getEstado() != null) {
                        tvEstado.setText("" + membresia.getEstado());
                    }

                    if (tvDiasRestantes != null) {
                        tvDiasRestantes.setText("" + membresia.getDiasRestantes());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorMsg = response.errorBody().string();
                            Log.e(TAG, "Error en la respuesta: " + errorMsg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al leer el cuerpo del error", e);
                    }
                    Toast.makeText(MembresiaActivity.this, "Error al cargar datos de la membresía: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MembresiaResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
                Toast.makeText(MembresiaActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}