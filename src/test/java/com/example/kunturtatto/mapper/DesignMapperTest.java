package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.request.DesignRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DesignMapperTest {

    private DesignMapper designMapper;

    @BeforeEach
    void setUp() {
        designMapper = Mappers.getMapper(DesignMapper.class);
    }

    @Test
    @DisplayName("toDesignDto - Debe mapear correctamente Design a DesignDto")
    void toDesignDto_ShouldMapDesignToDesignDto() {
        Category category = Category.builder()
                .id(1L)
                .name("Tatuajes Tradicionales")
                .build();

        SubCategory subCategory = SubCategory.builder()
                .id(2L)
                .name("Tradicional Americano")
                .category(category)
                .build();

        Design design = Design.builder()
                .id(3L)
                .title("Águila Tradicional")
                .description("Diseño de águila tradicional")
                .image("eagle.jpg")
                .subCategory(subCategory)
                .build();

        DesignDto designDto = designMapper.toDesignDto(design);

        assertNotNull(designDto);
        assertEquals(3L, designDto.getId());
        assertEquals("Águila Tradicional", designDto.getTitle());
        assertEquals("Diseño de águila tradicional", designDto.getDescription());
        assertEquals("eagle.jpg", designDto.getImage());
        assertEquals(2L, designDto.getSubCategoryId());
        assertEquals("Tradicional Americano", designDto.getSubCategoryName());
        assertEquals(1L, designDto.getCategoryId());
        assertEquals("Tatuajes Tradicionales", designDto.getCategoryName());
    }

    @Test
    @DisplayName("toDesignDto - Debe manejar Design sin subcategoría")
    void toDesignDto_ShouldHandleDesignWithoutSubCategory() {
        Design design = Design.builder()
                .id(1L)
                .title("Diseño Simple")
                .description("Descripción simple")
                .image("simple.jpg")
                .subCategory(null)
                .build();

        DesignDto designDto = designMapper.toDesignDto(design);

        assertNotNull(designDto);
        assertEquals(1L, designDto.getId());
        assertEquals("Diseño Simple", designDto.getTitle());
        assertEquals("Descripción simple", designDto.getDescription());
        assertEquals("simple.jpg", designDto.getImage());
        assertNull(designDto.getSubCategoryId());
        assertNull(designDto.getSubCategoryName());
        assertNull(designDto.getCategoryId());
        assertNull(designDto.getCategoryName());
    }

    @Test
    @DisplayName("toDesign - Debe mapear correctamente DesignRequest a Design")
    void toDesign_ShouldMapDesignRequestToDesign() {
        DesignRequest request = DesignRequest.builder()
                .title("Nuevo Diseño")
                .description("Descripción del nuevo diseño")
                .subCategoryId(1L)
                .build();

        Design design = designMapper.toDesign(request);

        assertNotNull(design);
        assertNull(design.getId()); 
        assertEquals("Nuevo Diseño", design.getTitle());
        assertEquals("Descripción del nuevo diseño", design.getDescription());
        assertNull(design.getImage());
        assertNull(design.getSubCategory());
    }

    @Test
    @DisplayName("toDesign - Debe manejar DesignRequest sin descripción")
    void toDesign_ShouldHandleDesignRequestWithoutDescription() {
        DesignRequest request = DesignRequest.builder()
                .title("Diseño Sin Descripción")
                .subCategoryId(1L)
                .build();

        Design design = designMapper.toDesign(request);

        assertNotNull(design);
        assertEquals("Diseño Sin Descripción", design.getTitle());
        assertNull(design.getDescription());
    }
}