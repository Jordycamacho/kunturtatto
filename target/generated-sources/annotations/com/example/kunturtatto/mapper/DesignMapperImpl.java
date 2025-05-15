package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.request.SubCategoryRequest;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T16:08:09+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class DesignMapperImpl implements DesignMapper {

    @Override
    public CategoryDto toCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto.CategoryDtoBuilder categoryDto = CategoryDto.builder();

        categoryDto.id( category.getId() );
        categoryDto.name( category.getName() );
        categoryDto.image( category.getImage() );
        categoryDto.subCategories( subCategoryListToSubCategoryDtoList( category.getSubCategories() ) );

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
    public SubCategoryDto toSubCategoryDto(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }

        SubCategoryDto.SubCategoryDtoBuilder subCategoryDto = SubCategoryDto.builder();

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

    @Override
    public DesignDto toDesignDto(Design design) {
        if ( design == null ) {
            return null;
        }

        DesignDto.DesignDtoBuilder designDto = DesignDto.builder();

        designDto.subCategoryId( designSubCategoryId( design ) );
        designDto.subCategoryName( designSubCategoryName( design ) );
        designDto.categoryId( designSubCategoryCategoryId( design ) );
        designDto.categoryName( designSubCategoryCategoryName( design ) );
        designDto.id( design.getId() );
        designDto.title( design.getTitle() );
        designDto.description( design.getDescription() );
        designDto.image( design.getImage() );

        return designDto.build();
    }

    @Override
    public Design toDesign(DesignRequest request) {
        if ( request == null ) {
            return null;
        }

        Design.DesignBuilder design = Design.builder();

        design.title( request.getTitle() );
        design.description( request.getDescription() );
        design.image( request.getImage() );

        return design.build();
    }

    protected CategoryDto.SubCategoryDto subCategoryToSubCategoryDto(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }

        CategoryDto.SubCategoryDto.SubCategoryDtoBuilder subCategoryDto = CategoryDto.SubCategoryDto.builder();

        subCategoryDto.id( subCategory.getId() );
        subCategoryDto.name( subCategory.getName() );
        subCategoryDto.image( subCategory.getImage() );

        return subCategoryDto.build();
    }

    protected List<CategoryDto.SubCategoryDto> subCategoryListToSubCategoryDtoList(List<SubCategory> list) {
        if ( list == null ) {
            return null;
        }

        List<CategoryDto.SubCategoryDto> list1 = new ArrayList<CategoryDto.SubCategoryDto>( list.size() );
        for ( SubCategory subCategory : list ) {
            list1.add( subCategoryToSubCategoryDto( subCategory ) );
        }

        return list1;
    }

    private Long designSubCategoryId(Design design) {
        if ( design == null ) {
            return null;
        }
        SubCategory subCategory = design.getSubCategory();
        if ( subCategory == null ) {
            return null;
        }
        Long id = subCategory.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String designSubCategoryName(Design design) {
        if ( design == null ) {
            return null;
        }
        SubCategory subCategory = design.getSubCategory();
        if ( subCategory == null ) {
            return null;
        }
        String name = subCategory.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long designSubCategoryCategoryId(Design design) {
        if ( design == null ) {
            return null;
        }
        SubCategory subCategory = design.getSubCategory();
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

    private String designSubCategoryCategoryName(Design design) {
        if ( design == null ) {
            return null;
        }
        SubCategory subCategory = design.getSubCategory();
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
