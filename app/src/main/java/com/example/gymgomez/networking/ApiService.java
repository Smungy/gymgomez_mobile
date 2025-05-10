package com.example.gymgomez.networking;

import com.example.gymgomez.modelos.ChangePasswordRequest;
import com.example.gymgomez.modelos.ChangePasswordResponse;
import com.example.gymgomez.modelos.LoginRequest;
import com.example.gymgomez.modelos.LoginResponse;
import com.example.gymgomez.modelos.LogoutResponse;
import com.example.gymgomez.modelos.MembresiaResponse;
import com.example.gymgomez.modelos.MiembroResponse;
import com.example.gymgomez.modelos.QrResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("change-password")
    Call<ChangePasswordResponse> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest changePasswordRequest);

    @POST("logout")
    Call<LogoutResponse> logout(@Header("Authorization") String token);

    @GET("miembro")
    Call<MiembroResponse> getMiembro(@Header("Authorization") String token);

    @GET("miembro/membresia")
    Call<MembresiaResponse> getMembresia(@Header("Authorization") String token);

    @GET("miembro/qr")
    Call<QrResponse> getQr(@Header("Authorization") String token);
}