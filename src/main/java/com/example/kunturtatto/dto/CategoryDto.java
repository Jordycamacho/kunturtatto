package com.example.kunturtatto.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String image;
    private List<SubCategoryDto> subCategories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCategoryDto {
        private Long id;
        private String name;
        private String image;
    }
}