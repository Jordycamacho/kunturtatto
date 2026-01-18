package com.example.kunturtatto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_tatuaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TattooConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "La parte del cuerpo es obligatoria")
    @Size(max = 200, message = "La parte del cuerpo no puede exceder 200 caracteres")
    @Column(name = "parte_cuerpo", nullable = false)
    private String parteCuerpo;

    @NotBlank(message = "El tamaño es obligatorio")
    @Size(max = 100, message = "El tamaño no puede exceder 100 caracteres")
    @Column(name = "tamano", nullable = false)
    private String tamano;

    @NotBlank(message = "El tipo de tatuaje es obligatorio")
    @Size(max = 50, message = "El tipo de tatuaje no puede exceder 50 caracteres")
    @Column(name = "tipo_tatuaje", nullable = false)
    private String tipoTatuaje;

    @NotBlank(message = "Debe indicar si está abierto a sugerencias")
    @Size(max = 20, message = "No puede exceder 20 caracteres")
    @Column(name = "abierto_sugerencias", nullable = false)
    private String abiertoSugerencias;

    @Size(max = 2000, message = "La expresión no puede exceder 2000 caracteres")
    @Column(name = "expresion", columnDefinition = "TEXT")
    private String expresion;

    @Size(max = 1000, message = "Los elementos obligatorios no pueden exceder 1000 caracteres")
    @Column(name = "elementos_obligatorios", columnDefinition = "TEXT")
    private String elementosObligatorios;

    @Size(max = 1000, message = "Los elementos a evitar no pueden exceder 1000 caracteres")
    @Column(name = "elementos_evitar", columnDefinition = "TEXT")
    private String elementosEvitar;

    @Size(max = 500, message = "El tipo de tatuaje favorito no puede exceder 500 caracteres")
    @Column(name = "tipo_tatuaje_gusto", columnDefinition = "TEXT")
    private String tipoTatuajeGusto;

    @Size(max = 1000, message = "Los referentes no pueden exceder 1000 caracteres")
    @Column(name = "referentes", columnDefinition = "TEXT")
    private String referentes;

    @Size(max = 2000, message = "Los links de referencia no pueden exceder 2000 caracteres")
    @Column(name = "links_referencia", columnDefinition = "TEXT")
    private String linksReferencia;

    @Size(max = 1000, message = "La carga simbólica no puede exceder 1000 caracteres")
    @Column(name = "carga_simbolica", columnDefinition = "TEXT")
    private String cargaSimbolica;

    @Size(max = 500, message = "Cómo nos conoció no puede exceder 500 caracteres")
    @Column(name = "como_conociste", columnDefinition = "TEXT")
    private String comoConociste;

    @Size(max = 2000, message = "La información adicional no puede exceder 2000 caracteres")
    @Column(name = "informacion_adicional", columnDefinition = "TEXT")
    private String informacionAdicional;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "leido", nullable = false)
    private Boolean leido;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (leido == null) {
            leido = false;
        }
    }
}