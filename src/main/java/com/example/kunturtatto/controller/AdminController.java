package com.example.kunturtatto.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.dto.UserDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import com.example.kunturtatto.service.SubCategoryService;
import com.example.kunturtatto.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Administración", description = "Operaciones administrativas para el estudio de tatuajes")
public class AdminController {

    private final SubCategoryService subCategoryService;
    private final CategoryService categoryService;
    private final DesignService designService;
    private final UserService userService;

    @ModelAttribute("categories")
    public List<CategoryDto> categories() {
        log.debug("[MODEL] Cargando lista de categorías para el modelo");
        return categoryService.getAllCategories();
    }

    /* Create Designs */
    @GetMapping("/disenos")
    public String showDesigns(Model model) {
        List<DesignDto> designs = designService.getAllDesigns();
        model.addAttribute("designs", designs);
        return "admin/design/showDesign";
    }

    @GetMapping("/disenos/crear")
    public String createDesignForm(Model model) {
        model.addAttribute("designRequest", DesignRequest.builder().build());
        model.addAttribute("categories", categoryService.getCategoriesWithSubcategories());
        return "admin/design/createDesign";
    }

    @GetMapping("/disenos/editar/{id}")
    public String editDesignForm(@PathVariable Long id, Model model) {
        try {
            DesignDto design = designService.getDesignById(id);
            model.addAttribute("design", design);
            model.addAttribute("designRequest", DesignRequest.builder().build());
            model.addAttribute("categories", categoryService.getCategoriesWithSubcategories());
            return "admin/design/editDesign";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/disenos";
        }
    }

    @PostMapping("/disenos/crear/guardar")
    public String saveDesign(@Valid @ModelAttribute("designRequest") DesignRequest request,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            designService.createDesign(request, imageFile);
            redirectAttributes.addFlashAttribute("success", "Diseño creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear diseño: " + e.getMessage());
            redirectAttributes.addFlashAttribute("designRequest", request);
        }
        return "redirect:/admin/disenos";
    }

    @PostMapping("/disenos/editar/guardar/{id}")
    public String updateDesign(@PathVariable Long id,
            @ModelAttribute DesignRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            designService.updateDesign(id, request, imageFile);
            redirectAttributes.addFlashAttribute("success", "Diseño actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar diseño: " + e.getMessage());
        }
        return "redirect:/admin/disenos";
    }

    @PostMapping("/disenos/eliminar/{id}")
    public String deleteDesign(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            designService.deleteDesign(id);
            redirectAttributes.addFlashAttribute("success", "Diseño eliminado exitosamente");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/disenos";
    }

    /* Create Categories */
    @GetMapping("/categorias")
    public String showCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/showCategory";
    }

    @GetMapping("/categorias/crear")
    public String createCategoryForm(Model model) {
        model.addAttribute("categoryRequest", CategoryRequest.builder().build());
        return "admin/category/createCategory";
    }

    @PostMapping("/categorias/crear/guardar")
    public String saveCategory(@ModelAttribute CategoryRequest request,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(request, imageFile);
            redirectAttributes.addFlashAttribute("success", "Categoría creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear categoría: " + e.getMessage());
        }
        return "redirect:/admin/categorias";
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada exitosamente");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/categorias";
    }

    /* Subcategorías */
    @Operation(summary = "Mostrar subcategorías", description = "Muestra la página de administración con todas las subcategorías del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página cargada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/subcategorias")
    public String showSubCategories(Model model) {
        log.info("[GET /admin/subcategorias] Mostrando página de gestión de subcategorías");

        try {
            long startTime = System.currentTimeMillis();
            List<SubCategoryDto> subCategories = subCategoryService.getAllSubCategories();
            long endTime = System.currentTimeMillis();

            log.info("[GET /admin/subcategorias] Se cargaron {} subcategorías en {} ms",
                    subCategories.size(), (endTime - startTime));

            model.addAttribute("subCategories", subCategories);
        } catch (Exception e) {
            log.error("[GET /admin/subcategorias] Error al cargar subcategorías: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar las subcategorías");
        }

        return "admin/subcategory/showSubCategory";
    }

    @Operation(summary = "Formulario de creación de subcategoría", description = "Muestra el formulario para crear una nueva subcategoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario cargado exitosamente")
    })
    @GetMapping("/subcategorias/crear")
    public String createSubCategoryForm(Model model) {
        log.info("[GET /admin/subcategorias/crear] Mostrando formulario para crear nueva subcategoría");
        model.addAttribute("subCategoryRequest", SubCategoryRequest.builder().build());
        return "admin/subcategory/createSubCategory";
    }

    @Operation(summary = "Crear nueva subcategoría", description = "Procesa el formulario y crea una nueva subcategoría en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de crear"),
            @ApiResponse(responseCode = "400", description = "Datos del formulario inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoría padre no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/subcategorias/crear/guardar")
    public String saveSubCategory(
            @Parameter(description = "Datos de la subcategoría a crear", required = true) @ModelAttribute SubCategoryRequest request,

            @Parameter(description = "Archivo de imagen para la subcategoría", required = true) @RequestParam("imageFile") MultipartFile imageFile,

            RedirectAttributes redirectAttributes) {

        log.info("💾 [POST /admin/subcategorias/crear/guardar] Procesando creación de subcategoría: {}",
                request.getName());

        log.debug("📝 [POST] Datos recibidos - Nombre: {}, Categoría ID: {}, Imagen: {} bytes",
                request.getName(), request.getCategoryId(),
                imageFile != null ? imageFile.getSize() : 0);

        try {
            long startTime = System.currentTimeMillis();
            subCategoryService.createSubCategory(request, imageFile);
            long endTime = System.currentTimeMillis();

            log.info("✅ [POST] Subcategoría '{}' creada exitosamente en {} ms",
                    request.getName(), (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Subcategoría creada exitosamente");

            logAudit("CREATE_SUBCATEGORY",
                    String.format("Subcategoría '%s' creada bajo categoría ID: %d",
                            request.getName(), request.getCategoryId()));

        } catch (ResourceNotFoundException e) {
            log.error("❌ [POST] Categoría no encontrada ID: {}", request.getCategoryId());
            logAudit("CREATE_SUBCATEGORY_ERROR",
                    String.format("Categoría no encontrada ID: %d", request.getCategoryId()));
            redirectAttributes.addFlashAttribute("error", "Error: La categoría seleccionada no existe");

        } catch (Exception e) {
            log.error("❌ [POST] Error al crear subcategoría '{}': {}",
                    request.getName(), e.getMessage(), e);
            logAudit("CREATE_SUBCATEGORY_ERROR",
                    String.format("Error creando subcategoría '%s': %s",
                            request.getName(), e.getMessage()));
            redirectAttributes.addFlashAttribute("error",
                    "Error al crear subcategoría: " + e.getMessage());
        }

        return "redirect:/admin/subcategorias";
    }

    @Operation(summary = "Eliminar subcategoría", description = "Elimina una subcategoría específica del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de eliminar"),
            @ApiResponse(responseCode = "404", description = "Subcategoría no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar porque tiene diseños asociados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/subcategorias/eliminar/{id}")
    public String deleteSubCategory(
            @Parameter(description = "ID de la subcategoría a eliminar", required = true, example = "1") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("Iniciando eliminación de subcategoría ID: {}", id);

        try {
            try {
                subCategoryService.getSubCategoryById(id);
            } catch (ResourceNotFoundException e) {
                log.warn("Subcategoría no encontrada ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "La subcategoría no existe");
                return "redirect:/admin/subcategorias";
            }

            long startTime = System.currentTimeMillis();
            subCategoryService.deleteSubCategory(id);
            long endTime = System.currentTimeMillis();

            log.info("Subcategoría ID: {} eliminada exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Subcategoría eliminada exitosamente");

            logAudit("DELETE_SUBCATEGORY",
                    String.format("Subcategoría ID: %d eliminada", id));

        } catch (ResourceNotFoundException e) {
            log.warn("Subcategoría no encontrada para eliminar ID: {}", id);
            logAudit("DELETE_SUBCATEGORY_ERROR",
                    String.format("Subcategoría no encontrada ID: %d", id));
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());

        } catch (IllegalStateException e) {
            log.warn("No se puede eliminar subcategoría ID: {} - {}", id, e.getMessage());
            logAudit("DELETE_SUBCATEGORY_REJECTED",
                    String.format("No se puede eliminar subcategoría ID: %d - %s", id, e.getMessage()));
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado al eliminar subcategoría ID: {}: {}",
                    id, e.getMessage(), e);
            logAudit("DELETE_SUBCATEGORY_ERROR",
                    String.format("Error inesperado eliminando subcategoría ID: %d - %s",
                            id, e.getMessage()));
            redirectAttributes.addFlashAttribute("error",
                    "Error inesperado al eliminar subcategoría: " + e.getMessage());
        }

        return "redirect:/admin/subcategorias";
    }

    @Operation(summary = "Formulario de edición de subcategoría", description = "Muestra el formulario para editar una subcategoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario cargado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Subcategoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/subcategorias/editar/{id}")
    public String editSubCategoryForm(
            @Parameter(description = "ID de la subcategoría a editar", required = true, example = "1") @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("GET /admin/subcategorias/editar/{} - Mostrando formulario de edición", id);

        try {
            SubCategoryDto subCategory = subCategoryService.getSubCategoryById(id);
            model.addAttribute("subCategory", subCategory);

            SubCategoryRequest request = SubCategoryRequest.builder()
                    .name(subCategory.getName())
                    .categoryId(subCategory.getCategoryId())
                    .build();

            model.addAttribute("subCategoryRequest", request);

            log.info("Formulario de edición cargado para subcategoría: {} (ID: {})",
                    subCategory.getName(), id);

            return "admin/subcategory/editSubCategory";

        } catch (ResourceNotFoundException e) {
            log.warn("Subcategoría no encontrada para editar ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "La subcategoría no existe");
            return "redirect:/admin/subcategorias";

        } catch (Exception e) {
            log.error("Error al cargar formulario de edición para ID: {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al cargar el formulario de edición");
            return "redirect:/admin/subcategorias";
        }
    }

    @Operation(summary = "Actualizar subcategoría", description = "Actualiza una subcategoría existente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de actualizar"),
            @ApiResponse(responseCode = "400", description = "Datos del formulario inválidos"),
            @ApiResponse(responseCode = "404", description = "Subcategoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/subcategorias/editar/guardar/{id}")
    public String updateSubCategory(
            @Parameter(description = "ID de la subcategoría a actualizar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la subcategoría", required = true) @ModelAttribute SubCategoryRequest request,
            @Parameter(description = "Nueva imagen (opcional)") @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,

            RedirectAttributes redirectAttributes) {

        log.info("POST /admin/subcategorias/editar/guardar/{} - Procesando actualización de subcategoría", id);
        log.debug("Datos recibidos para actualización - Nombre: {}, Categoría ID: {}, Imagen proporcionada: {}",
                request.getName(), request.getCategoryId(), imageFile != null && !imageFile.isEmpty());
        try {
            long startTime = System.currentTimeMillis();
            SubCategoryDto updatedSubCategory = subCategoryService.updateSubCategory(id, request, imageFile);
            long endTime = System.currentTimeMillis();

            log.info("Subcategoría ID: {} actualizada exitosamente en {} ms. Nuevo nombre: {}",
                    id, (endTime - startTime), updatedSubCategory.getName());

            redirectAttributes.addFlashAttribute("success", "Subcategoría actualizada exitosamente");
            
            logAudit("UPDATE_SUBCATEGORY",
                    String.format("Subcategoría ID: %d actualizada a nombre '%s'",
                            id, updatedSubCategory.getName()));

        } catch (ResourceNotFoundException e) {
            log.error("Subcategoría no encontrada para actualizar ID: {}", id);
            logAudit("UPDATE_SUBCATEGORY_ERROR",
                    String.format("Subcategoría no encontrada ID: %d", id));
            redirectAttributes.addFlashAttribute("error", "Error: La subcategoría no existe");

        } catch (Exception e) {
            log.error("Error al actualizar subcategoría ID: {}: {}", id, e.getMessage(), e);
            logAudit("UPDATE_SUBCATEGORY_ERROR",
                    String.format("Error actualizando subcategoría ID: %d - %s", id, e.getMessage()));
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar subcategoría: " + e.getMessage());
        }

        return "redirect:/admin/subcategorias";
    }

    /* Administracion usuarios */
    @GetMapping("/usuarios")
    public String showUsers(Model model) {
        log.info("[GET /admin/usuarios] Mostrando página de gestión de usuarios");

        List<UserDto> user = userService.getAllUsers();
        model.addAttribute("users", user);

        return "/admin/users/showUser";
    }

    private void logAudit(String action, String details) {
        String auditMessage = String.format("[AUDITORÍA] %s | Timestamp: %d | Detalles: %s",
                action, System.currentTimeMillis(), details);

        log.info(auditMessage);

        Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");
        auditLogger.info(auditMessage);
    }
}
