package com.example.gymgomez.modelos;

public class LoginResponse {
    private String token;
    private boolean first_login;
    private boolean requires_password_change;
    private String message;

    public String getToken() {
        return token;
    }

    public boolean isFirstLogin() {
        return first_login;
    }

    public boolean isRequiresPasswordChange() { // 👈 AGREGAR ESTE MÉTODO GETTER
        return requires_password_change;
    }

    public String getMessage() {
        return message;
    }
}
