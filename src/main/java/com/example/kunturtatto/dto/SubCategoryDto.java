package com.example.kunturtatto.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryDto {
    private Long id;
    private String name;
    private String image;
    private Long categoryId;
    private String categoryName;
}