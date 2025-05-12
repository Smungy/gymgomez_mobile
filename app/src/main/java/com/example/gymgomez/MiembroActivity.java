package com.example.gymgomez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    private ImageView imgFotoMiembro;
    private Button btnSubirFoto;

    private static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miembro);

        // Inicialización de vistas
        tvId = findViewById(R.id.tvId);
        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);
        imgFotoMiembro = findViewById(R.id.imgFotoMiembro); // Asegúrate que este ID exista en el XML
        btnSubirFoto = findViewById(R.id.btnSubirFoto);

        // Acción del botón para abrir galería
        btnSubirFoto.setOnClickListener(v -> abrirGaleria());

        // Cargar datos del miembro desde la API
        loadMiembroData();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imagenSeleccionada = data.getData();
            imgFotoMiembro.setImageURI(imagenSeleccionada);
        }
    }

    private void loadMiembroData() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", "");

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
                        tvId.setText("" + miembro.getId());
                        tvNombre.setText("" + miembro.getNombre());
                        tvApellido.setText("" + miembro.getApellido());
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
