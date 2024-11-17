package com.example.kunturtatto.model;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "category_design")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "idCategoryDesign")
@ToString(exclude = "designs")
public class CategoryDesign {

    public CategoryDesign(Long idCategoryDesign, String nameCategoryDesign, String image) {
        this.idCategoryDesign = idCategoryDesign;
        this.nameCategoryDesign = nameCategoryDesign;
        this.image = image;
 
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoryDesign;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    private String nameCategoryDesign;

    @NotBlank(message = "La imagen no puede estar vacía")
    private String image;

    @OneToMany(mappedBy = "categoryDesign", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Design> designs;
}

