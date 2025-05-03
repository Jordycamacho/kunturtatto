package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.*;
import com.example.kunturtatto.model.*;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.request.SubCategoryRequest;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DesignMapper {

    // Category mappings
    CategoryDto toCategoryDto(Category category);
    Category toCategory(CategoryRequest request);

    // SubCategory mappings
    SubCategoryDto toSubCategoryDto(SubCategory subCategory);
    SubCategory toSubCategory(SubCategoryRequest request);

    // Design mappings
    @Mapping(target = "subCategoryId", source = "subCategory.id")
    @Mapping(target = "subCategoryName", source = "subCategory.name")
    @Mapping(target = "categoryId", source = "subCategory.category.id")
    @Mapping(target = "categoryName", source = "subCategory.category.name")
    DesignDto toDesignDto(Design design);

    Design toDesign(DesignRequest request);
}