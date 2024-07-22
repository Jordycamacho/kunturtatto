package com.example.kunturtatto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="design")
@Getter
@Setter
@NoArgsConstructor
public class Design {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDesign;
    private String title;
    private String description;
    private String image;

    @ManyToOne
    @JoinColumn(name = "idCategoryDesign")
    private CategoryDesign categoryDesign;
}
