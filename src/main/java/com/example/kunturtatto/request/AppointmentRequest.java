package com.example.kunturtatto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String customerName;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String customerEmail;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", 
             message = "El teléfono debe tener un formato válido")
    private String customerPhone;
    
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro")
    private LocalDate date;
    
    @NotBlank(message = "La hora es obligatoria")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", 
             message = "La hora debe tener formato HH:mm")
    private String time;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double price;
    
    private Long designId;
    
    @Positive(message = "El tamaño del tatuaje debe ser mayor a 0")
    private Double tattooSize;
    
    @NotBlank(message = "La parte del cuerpo es obligatoria")
    private String bodyPart;
    
    @URL(message = "Los enlaces de referencia deben ser URLs válidas")
    private String referenceLinks;
    
    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String customDescription;
}