package com.example.kunturtatto.dtos;

import lombok.Data;

@Data
public class DesignDTO {
    private Long idDesign;
    private String title;
    private String description;
    private String image;
    private String categoryName;

    public DesignDTO(Long idDesign, String title, String description, String image, String categoryName) {
        this.idDesign = idDesign;
        this.title = title;
        this.description = description;
        this.image = image;
        this.categoryName = categoryName;
    }
}
