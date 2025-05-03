package com.example.kunturtatto.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DesignDto {
    private Long id;
    private String title;
    private String description;
    private String image;
    private Long subCategoryId;
    private String subCategoryName;
    private Long categoryId;
    private String categoryName;
}
