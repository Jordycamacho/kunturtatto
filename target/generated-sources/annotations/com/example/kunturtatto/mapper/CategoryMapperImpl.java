package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.request.CategoryRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T16:08:09+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto.CategoryDtoBuilder categoryDto = CategoryDto.builder();

        categoryDto.subCategories( mapSubCategories( category.getSubCategories() ) );
        categoryDto.id( category.getId() );
        categoryDto.name( category.getName() );
        categoryDto.image( category.getImage() );

        return categoryDto.build();
    }

    @Override
    public Category toCategory(CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( request.getName() );
        category.image( request.getImage() );

        return category.build();
    }

    @Override
    public CategoryDto.SubCategoryDto toSubCategoryDto(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }

        CategoryDto.SubCategoryDto.SubCategoryDtoBuilder subCategoryDto = CategoryDto.SubCategoryDto.builder();

        subCategoryDto.id( subCategory.getId() );
        subCategoryDto.name( subCategory.getName() );
        subCategoryDto.image( subCategory.getImage() );

        return subCategoryDto.build();
    }
}
