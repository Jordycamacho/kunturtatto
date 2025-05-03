package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.request.SubCategoryRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {

    SubCategoryMapper INSTANCE = Mappers.getMapper(SubCategoryMapper.class);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name") 
    SubCategoryDto toSubCategoryDto(SubCategory subCategory);

    @Mapping(target = "category", ignore = true)
    SubCategory toSubCategory(SubCategoryRequest request);
}