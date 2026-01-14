package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para SubCategoryMapper.
 * Prueba el mapeo entre entidades y DTOs.
 */
class SubCategoryMapperTest {

    private SubCategoryMapper mapper = SubCategoryMapper.INSTANCE;

    private Category testCategory;
    private SubCategory testSubCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        testSubCategory = SubCategory.builder()
                .id(1L)
                .name("Test SubCategory")
                .image("test.jpg")
                .category(testCategory)
                .build();
    }

    @Test
    @DisplayName("Mapear SubCategory a SubCategoryDto - Debe mapear correctamente")
    void toSubCategoryDto_ShouldMapCorrectly() {
        SubCategoryDto dto = mapper.toSubCategoryDto(testSubCategory);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test SubCategory", dto.getName());
        assertEquals("test.jpg", dto.getImage());
        assertEquals(1L, dto.getCategoryId());
        assertEquals("Test Category", dto.getCategoryName());
    }

    @Test
    @DisplayName("Mapear SubCategory nula a SubCategoryDto - Debe retornar nulo")
    void toSubCategoryDto_NullInput_ShouldReturnNull() {
        SubCategoryDto dto = mapper.toSubCategoryDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("Mapear SubCategory sin categoría a SubCategoryDto - Debe manejar nulo")
    void toSubCategoryDto_SubCategoryWithoutCategory_ShouldHandleNull() {
        SubCategory subCategory = SubCategory.builder()
                .id(1L)
                .name("Test")
                .image("test.jpg")
                .category(null)
                .build();

        SubCategoryDto dto = mapper.toSubCategoryDto(subCategory);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test", dto.getName());
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryName());
    }
}