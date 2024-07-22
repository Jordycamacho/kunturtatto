package com.example.kunturtatto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="category_design")
@Getter
@Setter
@NoArgsConstructor
public class CategoryDesign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoryDesign;
    private String nameCategoryDesign;

    @OneToMany(mappedBy = "categoryDesign")
    private List<Design> designs;
}
