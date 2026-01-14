package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.mapper.CategoryMapper;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.repository.CategoryRepository;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.service.ImageService;
import com.example.kunturtatto.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryDto testCategoryDto;
    private CategoryRequest testCategoryRequest;
    private MultipartFile testImageFile;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Tatuajes Tradicionales")
                .image("tradicional.jpg")
                .build();

        testCategoryDto = CategoryDto.builder()
                .id(1L)
                .name("Tatuajes Tradicionales")
                .image("tradicional.jpg")
                .build();

        testCategoryRequest = CategoryRequest.builder()
                .name("Tatuajes Tradicionales")
                .build();

        testImageFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("createCategory - Debe crear categoría con imagen personalizada exitosamente")
    void createCategory_WithCustomImage_ShouldCreateCategory() {
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("saved_image.jpg");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.createCategory(testCategoryRequest, testImageFile);

        assertNotNull(result);
        assertEquals(testCategoryDto.getId(), result.getId());
        assertEquals(testCategoryDto.getName(), result.getName());
        assertEquals(testCategoryDto.getImage(), result.getImage());

        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(imageService, never()).getDefaultImage();
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toCategoryDto(any(Category.class));
    }

    @Test
    @DisplayName("createCategory - Debe crear categoría con imagen por defecto cuando no se proporciona archivo")
    void createCategory_WithoutImageFile_ShouldUseDefaultImage() {
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.createCategory(testCategoryRequest, null);

        assertNotNull(result);
        verify(imageService, times(1)).getDefaultImage();
        verify(imageService, never()).saveImageNormal(any());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory - Debe actualizar categoría sin cambiar imagen")
    void updateCategory_WithoutNewImage_ShouldUpdateOnlyName() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.updateCategory(1L, testCategoryRequest, null);

        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, never()).saveImageNormal(any());
        verify(imageService, never()).deleteImageNormal(anyString());
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    @DisplayName("updateCategory - Debe actualizar categoría con nueva imagen")
    void updateCategory_WithNewImage_ShouldUpdateNameAndImage() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("new_image.jpg");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.updateCategory(1L, testCategoryRequest, testImageFile);

        assertNotNull(result);
        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(imageService, times(1)).deleteImageNormal("tradicional.jpg");
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    @DisplayName("updateCategory - Debe lanzar excepción cuando categoría no existe")
    void updateCategory_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(1L, testCategoryRequest, testImageFile));

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, never()).saveImageNormal(any());
    }

    @Test
    @DisplayName("getCategoryById - Debe retornar categoría cuando existe")
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toCategoryDto(testCategory)).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(testCategoryDto.getId(), result.getId());
        assertEquals(testCategoryDto.getName(), result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getCategoryById - Debe lanzar excepción cuando categoría no existe")
    void getCategoryById_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.getCategoryById(1L));

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getAllCategories - Debe retornar lista de categorías")
    void getAllCategories_ShouldReturnCategoryList() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        when(categoryMapper.toCategoryDto(testCategory)).thenReturn(testCategoryDto);

        List<CategoryDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategoryDto.getId(), result.get(0).getId());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCategories - Debe retornar lista vacía cuando no hay categorías")
    void getAllCategories_WhenNoCategories_ShouldReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<CategoryDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deleteCategory - Debe eliminar categoría con imagen personalizada")
    void deleteCategory_WithCustomImage_ShouldDeleteImageAndCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, times(1)).deleteImageNormal("tradicional.jpg");
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("deleteCategory - No debe eliminar imagen cuando es la imagen por defecto")
    void deleteCategory_WithDefaultImage_ShouldNotDeleteImage() {
        testCategory.setImage("default.jpg");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, never()).deleteImageNormal(anyString());
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("deleteCategory - Debe lanzar excepción cuando categoría no existe")
    void deleteCategory_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.deleteCategory(1L));

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, never()).deleteImageNormal(anyString());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getCategoriesWithSubcategories - Debe retornar categorías con subcategorías")
    void getCategoriesWithSubcategories_ShouldReturnCategoriesWithSubcategories() {
        when(categoryRepository.findAllWithSubcategories()).thenReturn(List.of(testCategory));
        when(categoryMapper.toCategoryDto(testCategory)).thenReturn(testCategoryDto);

        List<CategoryDto> result = categoryService.getCategoriesWithSubcategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAllWithSubcategories();
    }

    @Test
    @DisplayName("updateCategoryImage - Debe actualizar imagen de categoría exitosamente")
    void updateCategoryImage_ShouldUpdateImage() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("new_image.jpg");
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.updateCategoryImage(1L, testImageFile);

        assertNotNull(result);
        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(imageService, times(1)).deleteImageNormal("tradicional.jpg");
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    @DisplayName("updateCategoryImage - Debe lanzar excepción cuando archivo de imagen está vacío")
    void updateCategoryImage_WithEmptyFile_ShouldThrowException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategoryImage(1L, emptyFile));

        assertEquals("Image file cannot be empty", exception.getMessage());
        verify(categoryRepository, never()).findById(anyLong());
        verify(imageService, never()).saveImageNormal(any());
    }

    @Test
    @DisplayName("updateCategoryImage - Debe lanzar excepción cuando categoría no existe")
    void updateCategoryImage_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategoryImage(1L, testImageFile));

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, never()).saveImageNormal(any());
    }

    @Test
    @DisplayName("updateCategoryImage - No debe eliminar imagen anterior cuando es la imagen por defecto")
    void updateCategoryImage_WithDefaultImage_ShouldNotDeleteOldImage() {
        testCategory.setImage("default.jpg");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("new_image.jpg");
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(testCategoryDto);

        CategoryDto result = categoryService.updateCategoryImage(1L, testImageFile);

        assertNotNull(result);
        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(imageService, never()).deleteImageNormal("default.jpg");
        verify(categoryRepository, times(1)).save(testCategory);
    }
}