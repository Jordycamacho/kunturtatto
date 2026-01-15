package com.example.kunturtatto.controller;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.request.DesignRequest;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
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
        private CategoryDto testCategoryDto;
        private DesignDto testDesignDto;
        private MockMultipartFile testImageFile;

        @BeforeEach
        void setUp() {
                testDesignDto = DesignDto.builder()
                                .id(1L)
                                .title("Águila Tradicional")
                                .description("Diseño de águila tradicional")
                                .image("eagle.jpg")
                                .subCategoryId(1L)
                                .subCategoryName("Tradicional")
                                .categoryId(1L)
                                .categoryName("Tatuajes")
                                .build();

                testSubCategoryDto = SubCategoryDto.builder()
                                .id(1L)
                                .name("Tradicional")
                                .image("test.jpg")
                                .categoryId(1L)
                                .categoryName("Tatuajes")
                                .build();

                testCategoryDto = CategoryDto.builder()
                                .id(1L)
                                .name("Tatuajes")
                                .image("category.jpg")
                                .subCategories(new ArrayList<>())
                                .build();

                testImageFile = new MockMultipartFile(
                                "imageFile",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "test image content".getBytes());
        }

        // ========== TESTS PARA DISEÑOS ==========

        @Test
        @DisplayName("GET /admin/disenos - Debe mostrar página de diseños exitosamente")
        void showDesigns_ShouldReturnViewWithDesigns() throws Exception {
                when(designService.getAllDesigns()).thenReturn(List.of(testDesignDto));

                mockMvc.perform(get("/admin/disenos"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/showDesign"))
                                .andExpect(model().attributeExists("designs"))
                                .andExpect(model().attribute("designs", List.of(testDesignDto)));

                verify(designService, times(1)).getAllDesigns();
        }

        @Test
        @DisplayName("GET /admin/disenos - Debe manejar error al cargar diseños")
        void showDesigns_ShouldHandleError() throws Exception {
                when(designService.getAllDesigns()).thenThrow(new RuntimeException("Error de base de datos"));

                mockMvc.perform(get("/admin/disenos"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/showDesign"))
                                .andExpect(model().attributeExists("error"));

                verify(designService, times(1)).getAllDesigns();
        }

        @Test
        @DisplayName("GET /admin/disenos/crear - Debe mostrar formulario de creación de diseño")
        void createDesignForm_ShouldReturnCreateForm() throws Exception {
                when(categoryService.getCategoriesWithSubcategories()).thenReturn(List.of(testCategoryDto));

                mockMvc.perform(get("/admin/disenos/crear"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/createDesign"))
                                .andExpect(model().attributeExists("designRequest", "categories"));

                verify(categoryService, times(1)).getCategoriesWithSubcategories();
        }

        @Test
        @DisplayName("GET /admin/disenos/crear - Debe manejar error al cargar formulario")
        void createDesignForm_ShouldHandleError() throws Exception {
                when(categoryService.getCategoriesWithSubcategories())
                                .thenThrow(new RuntimeException("Error al cargar categorías"));

                mockMvc.perform(get("/admin/disenos/crear"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/createDesign"))
                                .andExpect(model().attributeExists("error"));

                verify(categoryService, times(1)).getCategoriesWithSubcategories();
        }

        @Test
        @DisplayName("GET /admin/disenos/editar/{id} - Debe mostrar formulario de edición exitosamente")
        void editDesignForm_ShouldReturnEditForm() throws Exception {
                when(designService.getDesignById(1L)).thenReturn(testDesignDto);
                when(categoryService.getCategoriesWithSubcategories()).thenReturn(List.of(testCategoryDto));

                mockMvc.perform(get("/admin/disenos/editar/1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/editDesign"))
                                .andExpect(model().attributeExists("design", "designRequest", "categories"));

                verify(designService, times(1)).getDesignById(1L);
                verify(categoryService, times(1)).getCategoriesWithSubcategories();
        }

        @Test
        @DisplayName("GET /admin/disenos/editar/{id} - Debe manejar diseño no encontrado")
        void editDesignForm_ShouldHandleNotFound() throws Exception {
                when(designService.getDesignById(1L))
                                .thenThrow(new ResourceNotFoundException("Design not found", "id", 1L));

                mockMvc.perform(get("/admin/disenos/editar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("error"));

                verify(designService, times(1)).getDesignById(1L);
        }

        @Test
        @DisplayName("POST /admin/disenos/crear/guardar - Debe crear diseño exitosamente")
        void saveDesign_ShouldCreateAndRedirect() throws Exception {
                when(designService.createDesign(any(DesignRequest.class), any())).thenReturn(testDesignDto);

                mockMvc.perform(multipart("/admin/disenos/crear/guardar")
                                .file(testImageFile)
                                .param("title", "Nuevo Diseño")
                                .param("description", "Descripción del diseño")
                                .param("subCategoryId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("success"));

                verify(designService, times(1)).createDesign(any(), any());
        }

        @Test
        @DisplayName("POST /admin/disenos/crear/guardar - Debe manejar error de validación con imagen vacía")
        void saveDesign_WithEmptyImage_ShouldReturnToForm() throws Exception {
                MockMultipartFile emptyFile = new MockMultipartFile(
                                "imageFile",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                new byte[0]);

                mockMvc.perform(multipart("/admin/disenos/crear/guardar")
                                .file(emptyFile)
                                .param("title", "Nuevo Diseño")
                                .param("subCategoryId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/createDesign"));

                verify(designService, never()).createDesign(any(), any());
        }

        @Test
        @DisplayName("POST /admin/disenos/crear/guardar - Debe manejar error al crear diseño")
        void saveDesign_ShouldHandleError() throws Exception {
                when(designService.createDesign(any(DesignRequest.class), any()))
                                .thenThrow(new RuntimeException("Error al crear diseño"));

                mockMvc.perform(multipart("/admin/disenos/crear/guardar")
                                .file(testImageFile)
                                .param("title", "Nuevo Diseño")
                                .param("description", "Descripción")
                                .param("subCategoryId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/createDesign"));

                verify(designService, times(1)).createDesign(any(), any());
        }

        @Test
        @DisplayName("POST /admin/disenos/crear/guardar - Debe manejar subcategoría no encontrada")
        void saveDesign_ShouldHandleSubCategoryNotFound() throws Exception {
                when(designService.createDesign(any(DesignRequest.class), any()))
                                .thenThrow(new ResourceNotFoundException("SubCategory not found", "id", 1L));

                mockMvc.perform(multipart("/admin/disenos/crear/guardar")
                                .file(testImageFile)
                                .param("title", "Nuevo Diseño")
                                .param("description", "Descripción")
                                .param("subCategoryId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/design/createDesign"));

                verify(designService, times(1)).createDesign(any(), any());
        }

        @Test
        @DisplayName("POST /admin/disenos/editar/guardar/{id} - Debe actualizar diseño exitosamente")
        void updateDesign_ShouldUpdateAndRedirect() throws Exception {
                when(designService.updateDesign(anyLong(), any(), any())).thenReturn(testDesignDto);

                mockMvc.perform(multipart("/admin/disenos/editar/guardar/1")
                                .file(testImageFile)
                                .param("title", "Diseño Actualizado")
                                .param("description", "Descripción actualizada")
                                .param("subCategoryId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("success"));

                verify(designService, times(1)).updateDesign(anyLong(), any(), any());
        }

        @Test
        @DisplayName("POST /admin/disenos/editar/guardar/{id} - Debe actualizar diseño sin nueva imagen")
        void updateDesign_WithoutNewImage_ShouldUpdateWithoutImage() throws Exception {
                when(designService.updateDesign(anyLong(), any(), any())).thenReturn(testDesignDto);

                mockMvc.perform(multipart("/admin/disenos/editar/guardar/1")
                                .param("title", "Diseño Actualizado")
                                .param("description", "Descripción actualizada")
                                .param("subCategoryId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("success"));

                verify(designService, times(1)).updateDesign(anyLong(), any(), isNull());
        }

        @Test
        @DisplayName("POST /admin/disenos/editar/guardar/{id} - Debe manejar diseño no encontrado")
        void updateDesign_ShouldHandleNotFound() throws Exception {
                when(designService.updateDesign(anyLong(), any(), any()))
                                .thenThrow(new ResourceNotFoundException("Design not found", "id", 1L));

                mockMvc.perform(multipart("/admin/disenos/editar/guardar/1")
                                .file(testImageFile)
                                .param("title", "Diseño Actualizado")
                                .param("subCategoryId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("error"));

                verify(designService, times(1)).updateDesign(anyLong(), any(), any());
        }

        @Test
        @DisplayName("POST /admin/disenos/eliminar/{id} - Debe eliminar diseño exitosamente")
        void deleteDesign_ShouldDeleteAndRedirect() throws Exception {
                doNothing().when(designService).deleteDesign(1L);

                mockMvc.perform(post("/admin/disenos/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("success"));

                verify(designService, times(1)).deleteDesign(1L);
        }

        @Test
        @DisplayName("POST /admin/disenos/eliminar/{id} - Debe manejar diseño no encontrado al eliminar")
        void deleteDesign_ShouldHandleNotFound() throws Exception {
                doThrow(new ResourceNotFoundException("Design not found", "id", 1L))
                                .when(designService).deleteDesign(1L);

                mockMvc.perform(post("/admin/disenos/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("error"));

                verify(designService, times(1)).deleteDesign(1L);
        }

        @Test
        @DisplayName("POST /admin/disenos/eliminar/{id} - Debe manejar error inesperado al eliminar")
        void deleteDesign_ShouldHandleUnexpectedError() throws Exception {
                doThrow(new RuntimeException("Error inesperado"))
                                .when(designService).deleteDesign(1L);

                mockMvc.perform(post("/admin/disenos/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/disenos"))
                                .andExpect(flash().attributeExists("error"));

                verify(designService, times(1)).deleteDesign(1L);
        }

        // ========== TESTS PARA SUBCATEGORÍAS ==========

        @Test
        @DisplayName("GET /admin/subcategorias - Debe mostrar lista de subcategorías exitosamente")
        void showSubCategories_ShouldReturnViewWithSubCategories() throws Exception {
                when(subCategoryService.getAllSubCategories()).thenReturn(List.of(testSubCategoryDto));

                mockMvc.perform(get("/admin/subcategorias"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/subcategory/showSubCategory"))
                                .andExpect(model().attributeExists("subCategories"))
                                .andExpect(model().attribute("subCategories", List.of(testSubCategoryDto)));

                verify(subCategoryService, times(1)).getAllSubCategories();
        }

        @Test
        @DisplayName("GET /admin/subcategorias - Debe manejar error al cargar subcategorías")
        void showSubCategories_ShouldHandleError() throws Exception {
                when(subCategoryService.getAllSubCategories())
                                .thenThrow(new RuntimeException("Error de base de datos"));

                mockMvc.perform(get("/admin/subcategorias"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/subcategory/showSubCategory"))
                                .andExpect(model().attributeExists("error"));

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
        @DisplayName("GET /admin/subcategorias/editar/{id} - Debe mostrar formulario de edición exitosamente")
        void editSubCategoryForm_ShouldReturnEditForm() throws Exception {
                when(subCategoryService.getSubCategoryById(1L)).thenReturn(testSubCategoryDto);

                mockMvc.perform(get("/admin/subcategorias/editar/1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/subcategory/editSubCategory"))
                                .andExpect(model().attributeExists("subCategory", "subCategoryRequest"));

                verify(subCategoryService, times(1)).getSubCategoryById(1L);
        }

        @Test
        @DisplayName("GET /admin/subcategorias/editar/{id} - Debe manejar subcategoría no encontrada")
        void editSubCategoryForm_ShouldHandleNotFound() throws Exception {
                when(subCategoryService.getSubCategoryById(1L))
                                .thenThrow(new ResourceNotFoundException("SubCategory not found", "id", 1L));

                mockMvc.perform(get("/admin/subcategorias/editar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/subcategorias"))
                                .andExpect(flash().attributeExists("error"));

                verify(subCategoryService, times(1)).getSubCategoryById(1L);
        }

        @Test
        @DisplayName("POST /admin/subcategorias/crear/guardar - Debe crear subcategoría exitosamente")
        void saveSubCategory_ShouldCreateAndRedirect() throws Exception {
                doNothing().when(subCategoryService).createSubCategory(any(SubCategoryRequest.class), any());

                mockMvc.perform(multipart("/admin/subcategorias/crear/guardar")
                                .file(testImageFile)
                                .param("name", "Nueva Subcategoría")
                                .param("categoryId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/subcategorias"))
                                .andExpect(flash().attributeExists("success"));

                verify(subCategoryService, times(1)).createSubCategory(any(), any());
        }

        @Test
        @DisplayName("POST /admin/subcategorias/crear/guardar - Debe manejar error al crear subcategoría")
        void saveSubCategory_ShouldHandleError() throws Exception {
                doThrow(new RuntimeException("Error de validación"))
                                .when(subCategoryService).createSubCategory(any(SubCategoryRequest.class), any());

                mockMvc.perform(multipart("/admin/subcategorias/crear/guardar")
                                .file(testImageFile)
                                .param("name", "Nueva Subcategoría")
                                .param("categoryId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/subcategorias"))
                                .andExpect(flash().attributeExists("error"));

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
                                .param("categoryId", "1"))
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

        // ========== TESTS PARA CATEGORÍAS ==========

        @Test
        @DisplayName("GET /admin/categorias - Debe mostrar lista de categorías exitosamente")
        void showCategories_ShouldReturnViewWithCategories() throws Exception {
                when(categoryService.getAllCategories()).thenReturn(List.of(testCategoryDto));

                mockMvc.perform(get("/admin/categorias"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/category/showCategory"))
                                .andExpect(model().attributeExists("categories"))
                                .andExpect(model().attribute("categories", List.of(testCategoryDto)));

                verify(categoryService, times(1)).getAllCategories();
        }

        @Test
        @DisplayName("GET /admin/categorias - Debe manejar error al cargar categorías")
        void showCategories_ShouldHandleError() throws Exception {
                when(categoryService.getAllCategories()).thenThrow(new RuntimeException("Error de base de datos"));

                mockMvc.perform(get("/admin/categorias"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/category/showCategory"))
                                .andExpect(model().attributeExists("categories"));

                verify(categoryService, times(1)).getAllCategories();
        }

        @Test
        @DisplayName("GET /admin/categorias/crear - Debe mostrar formulario de creación de categoría")
        void createCategoryForm_ShouldReturnCreateForm() throws Exception {
                mockMvc.perform(get("/admin/categorias/crear"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/category/createCategory"))
                                .andExpect(model().attributeExists("categoryRequest"));
        }

        @Test
        @DisplayName("POST /admin/categorias/crear/guardar - Debe crear categoría exitosamente")
        void saveCategory_ShouldCreateAndRedirect() throws Exception {
                when(categoryService.createCategory(any(CategoryRequest.class), any()))
                                .thenReturn(testCategoryDto);

                mockMvc.perform(multipart("/admin/categorias/crear/guardar")
                                .file(testImageFile)
                                .param("name", "Nueva Categoría"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/categorias"))
                                .andExpect(flash().attributeExists("success"));

                verify(categoryService, times(1)).createCategory(any(), any());
        }

        @Test
        @DisplayName("POST /admin/categorias/crear/guardar - Debe manejar error al crear categoría")
        void saveCategory_ShouldHandleError() throws Exception {
                when(categoryService.createCategory(any(CategoryRequest.class), any()))
                                .thenThrow(new RuntimeException("Error al crear categoría"));

                mockMvc.perform(multipart("/admin/categorias/crear/guardar")
                                .file(testImageFile)
                                .param("name", "Nueva Categoría"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/categorias"))
                                .andExpect(flash().attributeExists("error"));

                verify(categoryService, times(1)).createCategory(any(), any());
        }

        @Test
        @DisplayName("POST /admin/categorias/eliminar/{id} - Debe eliminar categoría exitosamente")
        void deleteCategory_ShouldDeleteAndRedirect() throws Exception {
                doNothing().when(categoryService).deleteCategory(1L);

                mockMvc.perform(post("/admin/categorias/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/categorias"))
                                .andExpect(flash().attributeExists("success"));

                verify(categoryService, times(1)).deleteCategory(1L);
        }

        @Test
        @DisplayName("POST /admin/categorias/eliminar/{id} - Debe manejar categoría no encontrada")
        void deleteCategory_ShouldHandleNotFound() throws Exception {
                doThrow(new ResourceNotFoundException("Category not found", "id", 1L))
                                .when(categoryService).deleteCategory(1L);

                mockMvc.perform(post("/admin/categorias/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/categorias"))
                                .andExpect(flash().attributeExists("error"));

                verify(categoryService, times(1)).deleteCategory(1L);
        }

        @Test
        @DisplayName("POST /admin/categorias/eliminar/{id} - Debe manejar error inesperado")
        void deleteCategory_ShouldHandleUnexpectedError() throws Exception {
                doThrow(new RuntimeException("Error inesperado"))
                                .when(categoryService).deleteCategory(1L);

                mockMvc.perform(post("/admin/categorias/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/categorias"))
                                .andExpect(flash().attributeExists("error"));

                verify(categoryService, times(1)).deleteCategory(1L);
        }

        // ========== TESTS PARA USUARIOS ==========

        @Test
        @DisplayName("GET /admin/usuarios - Debe mostrar página de usuarios")
        void showUsers_ShouldReturnView() throws Exception {
                mockMvc.perform(get("/admin/usuarios"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("/admin/users/showUser"));

                verify(userService, times(1)).getAllUsers();
        }

        // ========== TESTS PARA MODEL ATTRIBUTES ==========

        @Test
        @DisplayName("ModelAttribute categories - Debe cargar lista de categorías")
        void categoriesModelAttribute_ShouldLoadCategories() throws Exception {
                when(categoryService.getAllCategories()).thenReturn(List.of(testCategoryDto));

                mockMvc.perform(get("/admin/categorias"))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("categories"));

                verify(categoryService, atLeastOnce()).getAllCategories();
        }
}