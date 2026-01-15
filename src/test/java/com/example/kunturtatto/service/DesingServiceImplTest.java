package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.DesignMapper;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.repository.DesignRepository;
import com.example.kunturtatto.repository.SubCategoryRepository;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.service.impl.DesignServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DesignServiceImplTest {

    @Mock
    private DesignRepository designRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private DesignMapper designMapper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private DesignServiceImpl designService;

    private Design testDesign;
    private DesignDto testDesignDto;
    private DesignRequest testDesignRequest;
    private SubCategory testSubCategory;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setUp() {
        Category testCategory = Category.builder()
                .id(1L)
                .name("Tatuajes Tradicionales")
                .build();

        testSubCategory = SubCategory.builder()
                .id(1L)
                .name("Tradicional Americano")
                .category(testCategory)
                .build();

        testDesign = Design.builder()
                .id(1L)
                .title("Águila Tradicional")
                .description("Diseño de águila en estilo tradicional americano")
                .image("eagle_traditional.jpg")
                .subCategory(testSubCategory)
                .build();

        testDesignDto = DesignDto.builder()
                .id(1L)
                .title("Águila Tradicional")
                .description("Diseño de águila en estilo tradicional americano")
                .image("eagle_traditional.jpg")
                .subCategoryId(1L)
                .subCategoryName("Tradicional Americano")
                .categoryId(1L)
                .categoryName("Tatuajes Tradicionales")
                .build();

        testDesignRequest = DesignRequest.builder()
                .title("Águila Tradicional")
                .description("Diseño de águila en estilo tradicional americano")
                .subCategoryId(1L)
                .build();

        testImageFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("createDesign - Debe crear diseño con imagen personalizada exitosamente")
    void createDesign_WithCustomImage_ShouldCreateDesign() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(imageService.saveImageNormal(any())).thenReturn("saved_image.jpg");
        when(designRepository.save(any(Design.class))).thenReturn(testDesign);
        when(designMapper.toDesignDto(any(Design.class))).thenReturn(testDesignDto);

        DesignDto result = designService.createDesign(testDesignRequest, testImageFile);

        assertNotNull(result);
        assertEquals(testDesignDto.getId(), result.getId());
        assertEquals(testDesignDto.getTitle(), result.getTitle());
        assertEquals(testDesignDto.getSubCategoryId(), result.getSubCategoryId());

        verify(subCategoryRepository, times(1)).findById(1L);
        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(designRepository, times(1)).save(any(Design.class));
        verify(designMapper, times(1)).toDesignDto(any(Design.class));
    }

    @Test
    @DisplayName("createDesign - Debe crear diseño con imagen por defecto cuando no se proporciona archivo")
    void createDesign_WithoutImageFile_ShouldUseDefaultImage() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(testSubCategory));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(designRepository.save(any(Design.class))).thenReturn(testDesign);
        when(designMapper.toDesignDto(any(Design.class))).thenReturn(testDesignDto);

        DesignDto result = designService.createDesign(testDesignRequest, null);

        assertNotNull(result);
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(imageService, times(1)).getDefaultImage();
        verify(imageService, never()).saveImageNormal(any());
        verify(designRepository, times(1)).save(any(Design.class));
    }

    @Test
    @DisplayName("createDesign - Debe lanzar excepción cuando subcategoría no existe")
    void createDesign_WhenSubCategoryNotFound_ShouldThrowException() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> designService.createDesign(testDesignRequest, testImageFile));

        assertTrue(exception.getMessage().contains("SubCategory not found"));
        verify(subCategoryRepository, times(1)).findById(1L);
        verify(imageService, never()).saveImageNormal(any());
        verify(designRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateDesign - Debe actualizar diseño sin cambiar imagen")
    void updateDesign_WithoutNewImage_ShouldUpdateOnlyFields() {
        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(designRepository.save(any(Design.class))).thenReturn(testDesign);
        when(designMapper.toDesignDto(any(Design.class))).thenReturn(testDesignDto);

        DesignDto result = designService.updateDesign(1L, testDesignRequest, null);

        assertNotNull(result);
        verify(designRepository, times(1)).findById(1L);
        verify(imageService, never()).saveImageNormal(any());
        verify(imageService, never()).deleteImageNormal(anyString());
        verify(designRepository, times(1)).save(testDesign);
    }

    @Test
    @DisplayName("updateDesign - Debe actualizar diseño con nueva imagen")
    void updateDesign_WithNewImage_ShouldUpdateImageAndFields() {
        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(imageService.saveImageNormal(any())).thenReturn("new_image.jpg");
        when(designRepository.save(any(Design.class))).thenReturn(testDesign);
        when(designMapper.toDesignDto(any(Design.class))).thenReturn(testDesignDto);

        DesignDto result = designService.updateDesign(1L, testDesignRequest, testImageFile);

        assertNotNull(result);
        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(imageService, times(1)).deleteImageNormal("eagle_traditional.jpg");
        verify(designRepository, times(1)).save(testDesign);
    }

    @Test
    @DisplayName("updateDesign - Debe cambiar subcategoría cuando se proporciona nueva subcategoría")
    void updateDesign_WithNewSubCategory_ShouldUpdateSubCategory() {
        SubCategory newSubCategory = SubCategory.builder()
                .id(2L)
                .name("Nueva Subcategoría")
                .build();

        DesignRequest newRequest = DesignRequest.builder()
                .title("Nuevo Título")
                .description("Nueva Descripción")
                .subCategoryId(2L)
                .build();

        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.of(newSubCategory));
        when(designRepository.save(any(Design.class))).thenReturn(testDesign);
        when(designMapper.toDesignDto(any(Design.class))).thenReturn(testDesignDto);

        DesignDto result = designService.updateDesign(1L, newRequest, null);

        assertNotNull(result);
        verify(subCategoryRepository, times(1)).findById(2L);
        verify(designRepository, times(1)).save(testDesign);
    }

    @Test
    @DisplayName("updateDesign - Debe lanzar excepción cuando diseño no existe")
    void updateDesign_WhenDesignNotFound_ShouldThrowException() {
        when(designRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> designService.updateDesign(1L, testDesignRequest, testImageFile));

        assertTrue(exception.getMessage().contains("Design not found"));
        verify(designRepository, times(1)).findById(1L);
        verify(imageService, never()).saveImageNormal(any());
    }

    @Test
    @DisplayName("getDesignById - Debe retornar diseño cuando existe")
    void getDesignById_WhenDesignExists_ShouldReturnDesign() {
        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(designMapper.toDesignDto(testDesign)).thenReturn(testDesignDto);

        DesignDto result = designService.getDesignById(1L);

        assertNotNull(result);
        assertEquals(testDesignDto.getId(), result.getId());
        assertEquals(testDesignDto.getTitle(), result.getTitle());
        verify(designRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getDesignById - Debe lanzar excepción cuando diseño no existe")
    void getDesignById_WhenDesignNotFound_ShouldThrowException() {
        when(designRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> designService.getDesignById(1L));

        assertTrue(exception.getMessage().contains("Design not found"));
        verify(designRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getAllDesigns - Debe retornar lista de diseños")
    void getAllDesigns_ShouldReturnDesignList() {
        when(designRepository.findAllWithRelations()).thenReturn(List.of(testDesign));
        when(designMapper.toDesignDto(testDesign)).thenReturn(testDesignDto);

        List<DesignDto> result = designService.getAllDesigns();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDesignDto.getId(), result.get(0).getId());
        verify(designRepository, times(1)).findAllWithRelations();
    }

    @Test
    @DisplayName("getAllDesigns - Debe retornar lista vacía cuando no hay diseños")
    void getAllDesigns_WhenNoDesigns_ShouldReturnEmptyList() {
        when(designRepository.findAllWithRelations()).thenReturn(List.of());

        List<DesignDto> result = designService.getAllDesigns();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(designRepository, times(1)).findAllWithRelations();
    }

    @Test
    @DisplayName("getAllDesignspPageable - Debe retornar página de diseños")
    void getAllDesignspPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Design> designPage = new PageImpl<>(List.of(testDesign), pageable, 1);
        
        when(designRepository.findAllWithRelationsPageable(pageable)).thenReturn(designPage);
        when(designMapper.toDesignDto(testDesign)).thenReturn(testDesignDto);

        Page<DesignDto> result = designService.getAllDesignspPageable(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(designRepository, times(1)).findAllWithRelationsPageable(pageable);
    }

    @Test
    @DisplayName("getDesignsBySubCategory - Debe retornar diseños por subcategoría")
    void getDesignsBySubCategory_ShouldReturnDesignsBySubCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Design> designPage = new PageImpl<>(List.of(testDesign), pageable, 1);
        
        when(designRepository.findBySubCategoryId(1L, pageable)).thenReturn(designPage);
        when(designMapper.toDesignDto(testDesign)).thenReturn(testDesignDto);

        Page<DesignDto> result = designService.getDesignsBySubCategory(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(designRepository, times(1)).findBySubCategoryId(1L, pageable);
    }

    @Test
    @DisplayName("getDesignsByCategory - Debe retornar diseños por categoría")
    void getDesignsByCategory_ShouldReturnDesignsByCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Design> designPage = new PageImpl<>(List.of(testDesign), pageable, 1);
        
        when(designRepository.findBySubCategoryCategoryId(1L, pageable)).thenReturn(designPage);
        when(designMapper.toDesignDto(testDesign)).thenReturn(testDesignDto);

        Page<DesignDto> result = designService.getDesignsByCategory(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(designRepository, times(1)).findBySubCategoryCategoryId(1L, pageable);
    }

    @Test
    @DisplayName("deleteDesign - Debe eliminar diseño con imagen personalizada")
    void deleteDesign_WithCustomImage_ShouldDeleteImageAndDesign() {
        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");

        designService.deleteDesign(1L);

        verify(designRepository, times(1)).findById(1L);
        verify(imageService, times(1)).deleteImageNormal("eagle_traditional.jpg");
        verify(designRepository, times(1)).delete(testDesign);
    }

    @Test
    @DisplayName("deleteDesign - No debe eliminar imagen cuando es la imagen por defecto")
    void deleteDesign_WithDefaultImage_ShouldNotDeleteImage() {
        testDesign.setImage("default.jpg");
        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(imageService.getDefaultImage()).thenReturn("default.jpg");

        designService.deleteDesign(1L);

        verify(designRepository, times(1)).findById(1L);
        verify(imageService, never()).deleteImageNormal(anyString());
        verify(designRepository, times(1)).delete(testDesign);
    }

    @Test
    @DisplayName("deleteDesign - Debe lanzar excepción cuando diseño no existe")
    void deleteDesign_WhenDesignNotFound_ShouldThrowException() {
        when(designRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> designService.deleteDesign(1L));

        assertTrue(exception.getMessage().contains("Design not found"));
        verify(designRepository, times(1)).findById(1L);
        verify(imageService, never()).deleteImageNormal(anyString());
        verify(designRepository, never()).delete(any());
    }

    @Test
    @DisplayName("updateDesignImage - Debe actualizar imagen de diseño")
    void updateDesignImage_ShouldUpdateImage() {
        when(designRepository.findById(1L)).thenReturn(Optional.of(testDesign));
        when(imageService.saveImageNormal(any())).thenReturn("new_image.jpg");
        when(imageService.getDefaultImage()).thenReturn("default.jpg");
        when(designRepository.save(any(Design.class))).thenReturn(testDesign);
        when(designMapper.toDesignDto(any(Design.class))).thenReturn(testDesignDto);

        DesignDto result = designService.updateDesignImage(1L, testImageFile);

        assertNotNull(result);
        verify(imageService, times(1)).saveImageNormal(testImageFile);
        verify(imageService, times(1)).deleteImageNormal("eagle_traditional.jpg");
        verify(designRepository, times(1)).save(testDesign);
    }

    @Test
    @DisplayName("updateDesignImage - Debe lanzar excepción cuando archivo de imagen está vacío")
    void updateDesignImage_WithEmptyFile_ShouldThrowException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> designService.updateDesignImage(1L, emptyFile));

        assertEquals("Image file cannot be empty", exception.getMessage());
        verify(designRepository, never()).findById(anyLong());
        verify(imageService, never()).saveImageNormal(any());
    }

    @Test
    @DisplayName("searchDesigns - Debe retornar diseños que coincidan con la búsqueda")
    void searchDesigns_ShouldReturnMatchingDesigns() {
        when(designRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("águila", "águila"))
                .thenReturn(List.of(testDesign));
        when(designMapper.toDesignDto(testDesign)).thenReturn(testDesignDto);

        List<DesignDto> result = designService.searchDesigns("águila");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDesignDto.getTitle(), result.get(0).getTitle());
        verify(designRepository, times(1))
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("águila", "águila");
    }

    @Test
    @DisplayName("searchDesigns - Debe retornar lista vacía cuando no hay coincidencias")
    void searchDesigns_WhenNoMatches_ShouldReturnEmptyList() {
        when(designRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("noexiste", "noexiste"))
                .thenReturn(List.of());

        List<DesignDto> result = designService.searchDesigns("noexiste");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(designRepository, times(1))
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("noexiste", "noexiste");
    }
}