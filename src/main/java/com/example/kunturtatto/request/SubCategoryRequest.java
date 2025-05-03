package com.example.kunturtatto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubCategoryRequest {
    private String name;
    private String image;
    private Long categoryId;
}