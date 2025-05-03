package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.request.CategoryRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "subCategories", source = "subCategories", qualifiedByName = "mapSubCategories")
    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryRequest request);

    @Named("mapSubCategories")
    default List<CategoryDto.SubCategoryDto> mapSubCategories(List<SubCategory> subCategories) {
        if (subCategories == null) {
            return null;
        }
        return subCategories.stream()
                .map(this::toSubCategoryDto)
                .collect(Collectors.toList());
    }

    CategoryDto.SubCategoryDto toSubCategoryDto(SubCategory subCategory);
}