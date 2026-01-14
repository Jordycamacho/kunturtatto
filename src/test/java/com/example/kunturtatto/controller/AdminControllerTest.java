package com.example.kunturtatto.controller;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import com.example.kunturtatto.service.SubCategoryService;
import com.example.kunturtatto.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private SubCategoryService subCategoryService;

    @SuppressWarnings("removal")
    @MockBean
    private CategoryService categoryService;

    @SuppressWarnings("removal")
    @MockBean
    private DesignService designService;

    @SuppressWarnings("removal")
    @MockBean
    private UserService userService;

    private SubCategoryDto testSubCategoryDto;
    private MockMultipartFile testImageFile;

    @BeforeEach
    void setUp() {
        testSubCategoryDto = SubCategoryDto.builder()
                .id(1L)
                .name("Tradicional")
                .image("test.jpg")
                .categoryId(1L)
                .categoryName("Tatuajes")
                .build();

        testImageFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("GET /admin/subcategorias - Debe mostrar lista de subcategorías")
    void showSubCategories_ShouldReturnViewWithSubCategories() throws Exception {
        when(subCategoryService.getAllSubCategories()).thenReturn(List.of(testSubCategoryDto));

        mockMvc.perform(get("/admin/subcategorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subcategory/showSubCategory"))
                .andExpect(model().attributeExists("subCategories"));

        verify(subCategoryService, times(1)).getAllSubCategories();
    }

    @Test
    @DisplayName("GET /admin/subcategorias/crear - Debe mostrar formulario de creación")
    void createSubCategoryForm_ShouldReturnCreateForm() throws Exception {
        mockMvc.perform(get("/admin/subcategorias/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subcategory/createSubCategory"))
                .andExpect(model().attributeExists("subCategoryRequest"));
    }

    @Test
    @DisplayName("GET /admin/subcategorias/editar/{id} - Debe mostrar formulario de edición")
    void editSubCategoryForm_ShouldReturnEditForm() throws Exception {
        when(subCategoryService.getSubCategoryById(1L)).thenReturn(testSubCategoryDto);

        mockMvc.perform(get("/admin/subcategorias/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subcategory/editSubCategory"))
                .andExpect(model().attributeExists("subCategory", "subCategoryRequest"));

        verify(subCategoryService, times(1)).getSubCategoryById(1L);
    }

    @Test
    @DisplayName("POST /admin/subcategorias/crear/guardar - Debe crear subcategoría exitosamente")
    void saveSubCategory_ShouldCreateAndRedirect() throws Exception {
        doNothing().when(subCategoryService).createSubCategory(any(SubCategoryRequest.class), any());

        mockMvc.perform(multipart("/admin/subcategorias/crear/guardar")
                .file(testImageFile)
                .param("name", "Nueva Subcategoría")
                .param("categoryId", "1")
                .with(request -> {
                    request.setMethod("POST");
                    return request;
                }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subcategorias"))
                .andExpect(flash().attributeExists("success"));

        verify(subCategoryService, times(1)).createSubCategory(any(), any());
    }

    @Test
    @DisplayName("POST /admin/subcategorias/editar/guardar/{id} - Debe actualizar subcategoría exitosamente")
    void updateSubCategory_ShouldUpdateAndRedirect() throws Exception {
        when(subCategoryService.updateSubCategory(anyLong(), any(), any()))
                .thenReturn(testSubCategoryDto);

        mockMvc.perform(multipart("/admin/subcategorias/editar/guardar/1")
                .file(testImageFile)
                .param("name", "Subcategoría Actualizada")
                .param("categoryId", "1")
                .with(request -> {
                    request.setMethod("POST");
                    return request;
                }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subcategorias"))
                .andExpect(flash().attributeExists("success"));

        verify(subCategoryService, times(1)).updateSubCategory(anyLong(), any(), any());
    }

    @Test
    @DisplayName("POST /admin/subcategorias/eliminar/{id} - Debe eliminar subcategoría exitosamente")
    void deleteSubCategory_ShouldDeleteAndRedirect() throws Exception {
        doNothing().when(subCategoryService).deleteSubCategory(1L);

        mockMvc.perform(post("/admin/subcategorias/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subcategorias"))
                .andExpect(flash().attributeExists("success"));

        verify(subCategoryService, times(1)).deleteSubCategory(1L);
    }

    @Test
    @DisplayName("POST /admin/subcategorias/eliminar/{id} - Debe manejar error al eliminar")
    void deleteSubCategory_ShouldHandleError() throws Exception {
        doThrow(new RuntimeException("Error al eliminar"))
                .when(subCategoryService).deleteSubCategory(1L);

        mockMvc.perform(post("/admin/subcategorias/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subcategorias"))
                .andExpect(flash().attributeExists("error"));

        verify(subCategoryService, times(1)).deleteSubCategory(1L);
    }
}