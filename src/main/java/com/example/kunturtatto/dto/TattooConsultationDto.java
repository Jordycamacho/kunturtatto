package com.example.kunturtatto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TattooConsultationDto {
    private Long id;
    private String nombre;
    private String email;
    private String parteCuerpo;
    private String tamano;
    private String tipoTatuaje;
    private String abiertoSugerencias;
    private String expresion;
    private String elementosObligatorios;
    private String elementosEvitar;
    private String tipoTatuajeGusto;
    private String referentes;
    private String linksReferencia;
    private String cargaSimbolica;
    private String comoConociste;
    private String informacionAdicional;
    private LocalDateTime fechaCreacion;
    private Boolean leido;
    private String estado;

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCreacion.format(formatter);
    }
    
    public String getResumen() {
        return String.format("%s - %s - %s", nombre, email, parteCuerpo);
    }
    
    public boolean isResaltado() {
        return leido != null && !leido;
    }
}