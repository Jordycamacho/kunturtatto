package com.example.kunturtatto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TattooConsultationRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @NotBlank(message = "La parte del cuerpo es obligatoria")
    @Size(max = 200, message = "La parte del cuerpo no puede exceder 200 caracteres")
    private String parteCuerpo;

    @NotBlank(message = "El tamaño es obligatorio")
    @Size(max = 100, message = "El tamaño no puede exceder 100 caracteres")
    private String tamano;

    @NotBlank(message = "El tipo de tatuaje es obligatorio")
    @Size(max = 50, message = "El tipo de tatuaje no puede exceder 50 caracteres")
    private String tipoTatuaje;

    @Size(max = 20, message = "No puede exceder 20 caracteres")
    private String abiertoSugerencias;

    @Size(max = 2000, message = "La expresión no puede exceder 2000 caracteres")
    private String expresion;

    @Size(max = 1000, message = "Los elementos obligatorios no pueden exceder 1000 caracteres")
    private String elementosObligatorios;

    @Size(max = 1000, message = "Los elementos a evitar no pueden exceder 1000 caracteres")
    private String elementosEvitar;

    @Size(max = 500, message = "El tipo de tatuaje favorito no puede exceder 500 caracteres")
    private String tipoTatuajeGusto;

    @Size(max = 1000, message = "Los referentes no pueden exceder 1000 caracteres")
    private String referentes;

    @Size(max = 2000, message = "Los links de referencia no pueden exceder 2000 caracteres")
    private String linksReferencia;

    @Size(max = 1000, message = "La carga simbólica no puede exceder 1000 caracteres")
    private String cargaSimbolica;

    @Size(max = 500, message = "Cómo nos conoció no puede exceder 500 caracteres")
    private String comoConociste;

    @Size(max = 2000, message = "La información adicional no puede exceder 2000 caracteres")
    private String informacionAdicional;
}