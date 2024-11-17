package com.example.kunturtatto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "design")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "idDesign")
@ToString(exclude = "categoryDesign")
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDesign;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    private String title;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotBlank(message = "La imagen no puede estar vacía")
    private String image = "default.jpg";

    // Relación ManyToOne con carga perezosa para optimizar el rendimiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category_design") 
    private CategoryDesign categoryDesign;

    public Design(String title, String description, String image, CategoryDesign categoryDesign) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.categoryDesign = categoryDesign;
    }

    
}
