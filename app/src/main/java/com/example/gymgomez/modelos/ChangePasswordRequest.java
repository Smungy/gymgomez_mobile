package com.example.gymgomez.modelos;

public class ChangePasswordRequest {
    private String current_password;
    private String new_password;
    private String new_password_confirmation;

    public ChangePasswordRequest(String current_password, String new_password, String new_password_confirmation) {
        this.current_password = current_password;
        this.new_password = new_password;
        this.new_password_confirmation = new_password_confirmation;
    }
}
