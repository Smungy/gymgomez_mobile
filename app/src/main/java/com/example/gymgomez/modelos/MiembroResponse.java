package com.example.gymgomez.modelos;

public class MiembroResponse {
    private Miembro miembro;

    public Miembro getMiembro() {
        return miembro;
    }

    public static class Miembro {
        private int id;
        private String nombre;
        private String apellido;

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
    }
}
