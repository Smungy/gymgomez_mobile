package com.example.gymgomez;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymgomez.modelos.QrResponse;
import com.example.gymgomez.networking.RetrofitClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrActivity extends AppCompatActivity {
    private ImageView imgQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        imgQr = findViewById(R.id.imgQr);

        loadQrData();
    }

    private void loadQrData() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        RetrofitClient.getApiService().getQr(token).enqueue(new Callback<QrResponse>() {
            @Override
            public void onResponse(Call<QrResponse> call, Response<QrResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QrResponse qrResponse = response.body();

                    // Generar QR a partir de la respuesta
                    generateQR(qrResponse.getQrCode());
                } else {
                    Toast.makeText(QrActivity.this, "Error al cargar datos del QR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QrResponse> call, Throwable t) {
                Toast.makeText(QrActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateQR(String qrCode) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el código QR", Toast.LENGTH_SHORT).show();
        }
    }
}