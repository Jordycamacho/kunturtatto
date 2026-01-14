package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.SubCategoryMapper;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.repository.CategoryRepository;
import com.example.kunturtatto.repository.SubCategoryRepository;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.impl.SubCategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SubCategoryServiceImplTest {

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubCategoryMapper subCategoryMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private SubCategoryServiceImpl subCategoryService;

    private Category testCategory;
    private SubCategory testSubCategory;
    private SubCategoryDto testSubCategoryDto;
    private SubCategoryRequest testRequest;
    private MultipartFile testImageFile;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Tatuajes")
                .build();

        testSubCategory = SubCategory.builder()
                .id(1L)
                .name("Tradicional")
                .image("test.jpg")
                .category(testCategory)
                .build();

        testSubCategoryDto = SubCategoryDto.builder()
                .id(1L)
                .name("Tradicional")
                .image("test.jpg")
                .categoryId(1L)
                .categoryName("Tatuajes")
                .build();

        testRequest = SubCategoryRequest.builder()
                .name("Tradicional")
                .categoryId(1L)
                .build();

        testImageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("Crear subcategoría exitosamente con imagen")
    void createSubCategory_WithImage_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("saved-image.jpg");
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(testSubCategory);
        when(subCategoryMapper.toSubCategoryDto(any(SubCategory.class))).thenReturn(testSubCategoryDto);

        SubCategoryDto result = subCategoryService.createSubCategory(testRequest, testImageFile);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, times(1)).saveImageNormal(any(MultipartFile.class));
        verify(subCategoryRepository, times(1)).save(any(SubCategory.class));
        verify(subCategoryMapper, times(1)).toSubCategoryDto(any(SubCategory.class));
    }

    @Test
    @DisplayName("Crear subcategoría exitosamente sin imagen")
    void createSubCategory_WithoutImage_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(testSubCategory);
        when(subCategoryMapper.toSubCategoryDto(any(SubCategory.class))).thenReturn(testSubCategoryDto);

        SubCategoryDto result = subCategoryService.createSubCategory(testRequest, null);

        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(imageService, times(0)).saveImageNormal(any(MultipartFile.class));
        verify(subCategoryRepository, times(1)).save(any(SubCategory.class));
    }

    @Test
    @DisplayName("Crear subcategoría con categoría inexistente - debe lanzar excepción")
    void createSubCategory_CategoryNotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> subCategoryService.createSubCategory(testRequest, null));
        
        verify(categoryRepository, times(1)).findById(1L);
        verify(subCategoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener subcategoría por ID exitosamente")
    void getSubCategoryById_Success() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(subCategoryMapper.toSubCategoryDto(testSubCategory)).thenReturn(testSubCategoryDto);

        SubCategoryDto result = subCategoryService.getSubCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tradicional", result.getName());
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(subCategoryMapper, times(1)).toSubCategoryDto(testSubCategory);
    }

    @Test
    @DisplayName("Obtener subcategoría por ID inexistente - debe lanzar excepción")
    void getSubCategoryById_NotFound_ThrowsException() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> subCategoryService.getSubCategoryById(1L));
        
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(subCategoryMapper, never()).toSubCategoryDto(any());
    }

    @Test
    @DisplayName("Obtener todas las subcategorías exitosamente")
    void getAllSubCategories_Success() {
        List<SubCategory> subCategories = List.of(testSubCategory);
        when(subCategoryRepository.findAll()).thenReturn(subCategories);
        when(subCategoryMapper.toSubCategoryDto(testSubCategory)).thenReturn(testSubCategoryDto);

        List<SubCategoryDto> result = subCategoryService.getAllSubCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tradicional", result.get(0).getName());
        verify(subCategoryRepository, times(1)).findAll();
        verify(subCategoryMapper, times(1)).toSubCategoryDto(testSubCategory);
    }

    @Test
    @DisplayName("Obtener subcategorías por categoría exitosamente")
    void getSubCategoriesByCategory_Success() {
        List<SubCategory> subCategories = List.of(testSubCategory);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(subCategoryRepository.findByCategoryId(1L)).thenReturn(subCategories);
        when(subCategoryMapper.toSubCategoryDto(testSubCategory)).thenReturn(testSubCategoryDto);

        List<SubCategoryDto> result = subCategoryService.getSubCategoriesByCategory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).existsById(1L);
        verify(subCategoryRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    @DisplayName("Obtener subcategorías por categoría inexistente - debe lanzar excepción")
    void getSubCategoriesByCategory_CategoryNotFound_ThrowsException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> subCategoryService.getSubCategoriesByCategory(1L));
        
        verify(categoryRepository, times(1)).existsById(1L);
        verify(subCategoryRepository, never()).findByCategoryId(any());
    }

    @Test
    @DisplayName("Actualizar subcategoría exitosamente")
    void updateSubCategory_Success() {
        SubCategoryRequest updateRequest = SubCategoryRequest.builder()
                .name("Tradicional Actualizado")
                .categoryId(1L)
                .build();

        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(testSubCategory);
        when(subCategoryMapper.toSubCategoryDto(any(SubCategory.class))).thenReturn(testSubCategoryDto);

        SubCategoryDto result = subCategoryService.updateSubCategory(1L, updateRequest, null);

        assertNotNull(result);
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(subCategoryRepository, times(1)).save(any(SubCategory.class));
    }

    @Test
    @DisplayName("Actualizar subcategoría con imagen")
    void updateSubCategory_WithImage_Success() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("new-image.jpg");
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(testSubCategory);
        when(subCategoryMapper.toSubCategoryDto(any(SubCategory.class))).thenReturn(testSubCategoryDto);

        SubCategoryDto result = subCategoryService.updateSubCategory(1L, testRequest, testImageFile);

        assertNotNull(result);
        verify(imageService, times(1)).saveImageNormal(any(MultipartFile.class));
    }

    @Test
    @DisplayName("Eliminar subcategoría exitosamente")
    void deleteSubCategory_Success() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(subCategoryRepository.deleteNativeById(1L)).thenReturn(1);
        when(subCategoryRepository.existsById(1L)).thenReturn(false);

        subCategoryService.deleteSubCategory(1L);

        verify(subCategoryRepository, times(1)).findById(1L);
        verify(subCategoryRepository, times(1)).deleteNativeById(1L);
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }

    @Test
    @DisplayName("Eliminar subcategoría con diseños asociados - debe lanzar excepción")
    void deleteSubCategory_WithDesigns_ThrowsException() {
        testSubCategory.getDesigns().add(null);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> subCategoryService.deleteSubCategory(1L));
        
        assertTrue(exception.getMessage().contains("diseños asociados"));
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(subCategoryRepository, never()).deleteNativeById(any());
    }

    @Test
    @DisplayName("Actualizar imagen de subcategoría exitosamente")
    void updateSubCategoryImage_Success() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(imageService.saveImageNormal(any(MultipartFile.class))).thenReturn("new-image.jpg");
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(testSubCategory);
        when(subCategoryMapper.toSubCategoryDto(any(SubCategory.class))).thenReturn(testSubCategoryDto);

        SubCategoryDto result = subCategoryService.updateSubCategoryImage(1L, testImageFile);

        assertNotNull(result);
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(imageService, times(1)).saveImageNormal(any(MultipartFile.class));
        verify(subCategoryRepository, times(1)).save(any(SubCategory.class));
    }

    @Test
    @DisplayName("Actualizar imagen con archivo vacío - debe lanzar excepción")
    void updateSubCategoryImage_EmptyFile_ThrowsException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class,
                () -> subCategoryService.updateSubCategoryImage(1L, emptyFile));
        
        verify(subCategoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Eliminación en cascada exitosa")
    void deleteSubCategoryWithCascade_Success() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(subCategoryRepository.existsById(1L)).thenReturn(false);

        subCategoryService.deleteSubCategoryWithCascade(1L);

        verify(subCategoryRepository, times(1)).findById(1L);
        verify(subCategoryRepository, times(1)).deleteById(1L);
        verify(subCategoryRepository, times(1)).flush();
    }
}