package com.example.gymgomez.modelos;

public class MembresiaResponse {
    private String tipo_membresia;
    private String fecha_inscripcion;
    private String fecha_vencimiento;
    private String estado;
    private int dias_restantes;


    public String getTipoMembresia() {
        return tipo_membresia;
    }

    public String getFechaInscripcion() {
        return fecha_inscripcion;
    }

    public String getFechaVencimiento() {
        return fecha_vencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public int getDiasRestantes() {
        return dias_restantes;
    }
}