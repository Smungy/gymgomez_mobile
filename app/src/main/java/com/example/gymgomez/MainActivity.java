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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText etId;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprobar si ya hay una sesión activa
        if (isUserLoggedIn()) {
            navigateToHome();
            return;
        }

        initializeViews();
        setListeners();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        String token = preferences.getString("token", null);
        return token != null && !token.isEmpty();
    }

    private void initializeViews() {
        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String idStr = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (idStr.isEmpty() || password.isEmpty()) {
            showMessage("Por favor, completa todos los campos");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            showMessage("ID debe ser un número");
            return;
        }

        showLoading(true);
        LoginRequest loginRequest = new LoginRequest(id, password, "android_app");

        RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulLogin(response.body());
                } else {
                    handleLoginError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Error de conexión", t);
                showMessage("No se pudo conectar al servidor. Verifica tu conexión a internet.");
            }
        });
    }

    private void handleSuccessfulLogin(LoginResponse loginResponse) {
        // Guardar token en SharedPreferences
        saveAuthToken(loginResponse.getToken());

        // Verificar si requiere cambio de contraseña o es primer login
        if (loginResponse.isFirstLogin() || loginResponse.isRequiresPasswordChange()) {
            navigateToChangePassword();
        } else {
            navigateToHome();
        }
    }

    private void handleLoginError(Response<LoginResponse> response) {
        // El backend Laravel usa código 422 para ValidationException (credenciales incorrectas)
        if (response.code() == 422) {
            showMessage("ID o contraseña incorrectos. Intenta de nuevo.");
            return;
        }

        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.d(TAG, "Error body: " + errorBody);

                // Intentar parsear el error JSON para obtener el mensaje de error específico
                // Laravel envía típicamente {"message":"...", "errors":{...}}
                try {
                    JSONObject errorJson = new JSONObject(errorBody);

                    // Intentar obtener mensaje de error específico de credentials
                    if (errorJson.has("errors") && errorJson.getJSONObject("errors").has("credentials")) {
                        String credentialsError = errorJson.getJSONObject("errors")
                                .getJSONArray("credentials").getString(0);
                        showMessage(credentialsError);
                        return;
                    }

                    // Intentar obtener mensaje general
                    if (errorJson.has("message")) {
                        String message = errorJson.getString("message");
                        if (!message.isEmpty()) {
                            showMessage(message);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error al parsear JSON de error", e);
                }
            }

            // Mensaje predeterminado para credenciales incorrectas
            showMessage("ID o contraseña incorrectos. Intenta de nuevo.");

        } catch (IOException e) {
            showMessage("Error en el inicio de sesión. Por favor intenta más tarde.");
            Log.e(TAG, "Error al procesar respuesta de error", e);
        }
    }

    private void saveAuthToken(String token) {
        SharedPreferences preferences = getSharedPreferences("gym_app", MODE_PRIVATE);
        preferences.edit().putString("token", "Bearer " + token).apply();
    }

    private void navigateToChangePassword() {
        Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        etId.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}