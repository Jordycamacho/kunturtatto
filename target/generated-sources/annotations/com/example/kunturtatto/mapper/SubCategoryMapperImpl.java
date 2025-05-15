package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.request.SubCategoryRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T16:08:09+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class SubCategoryMapperImpl implements SubCategoryMapper {

    @Override
    public SubCategoryDto toSubCategoryDto(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }

        SubCategoryDto.SubCategoryDtoBuilder subCategoryDto = SubCategoryDto.builder();

        subCategoryDto.categoryId( subCategoryCategoryId( subCategory ) );
        subCategoryDto.categoryName( subCategoryCategoryName( subCategory ) );
        subCategoryDto.id( subCategory.getId() );
        subCategoryDto.name( subCategory.getName() );
        subCategoryDto.image( subCategory.getImage() );

        return subCategoryDto.build();
    }

    @Override
    public SubCategory toSubCategory(SubCategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        SubCategory.SubCategoryBuilder subCategory = SubCategory.builder();

        subCategory.name( request.getName() );
        subCategory.image( request.getImage() );

        return subCategory.build();
    }

    private Long subCategoryCategoryId(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }
        Category category = subCategory.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String subCategoryCategoryName(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }
        Category category = subCategory.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
