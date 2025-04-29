package com.example.gymgomez.modelos;

public class LoginRequest {
    private int id;
    private String password;
    private String device_name;

    public LoginRequest(int id, String password, String device_name) {
        this.id = id;
        this.password = password;
        this.device_name = device_name;
    }
}
