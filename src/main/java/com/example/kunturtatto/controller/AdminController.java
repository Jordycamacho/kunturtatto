package com.example.kunturtatto.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.dto.UserDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import com.example.kunturtatto.service.SubCategoryService;
import com.example.kunturtatto.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final SubCategoryService subCategoryService;
    private final CategoryService categoryService;
    private final DesignService designService;
    private final UserService userService;

    @ModelAttribute("categories")
    public List<CategoryDto> categories() {
        return categoryService.getAllCategories();
    }

    /* Create Designs */
    @GetMapping("/diseños")
    public String showDesigns(Model model) {
        List<DesignDto> designs = designService.getAllDesigns();
        model.addAttribute("designs", designs);
        return "admin/design/showDesign";
    }

    @GetMapping("/diseños/crear")
    public String createDesignForm(Model model) {
        model.addAttribute("designRequest", DesignRequest.builder().build());
        model.addAttribute("categories", categoryService.getCategoriesWithSubcategories());
        return "admin/design/createDesign";
    }

    @GetMapping("/diseños/editar/{id}")
    public String editDesignForm(@PathVariable Long id, Model model) {
        try {
            DesignDto design = designService.getDesignById(id);
            model.addAttribute("design", design);
            model.addAttribute("designRequest", DesignRequest.builder().build());
            model.addAttribute("categories", categoryService.getCategoriesWithSubcategories());
            return "admin/design/editDesign";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/diseños";
        }
    }

    @PostMapping("/diseños/crear/guardar")
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
        return "redirect:/admin/diseños";
    }

    @PostMapping("/diseños/editar/guardar/{id}")
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
        return "redirect:/admin/diseños";
    }

    @PostMapping("/diseños/eliminar/{id}")
    public String deleteDesign(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            designService.deleteDesign(id);
            redirectAttributes.addFlashAttribute("success", "Diseño eliminado exitosamente");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/diseños";
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
    @GetMapping("/subcategorias")
    public String showSubCategories(Model model) {
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
        return "admin/subcategory/showSubCategory";
    }

    @GetMapping("/subcategorias/crear")
    public String createSubCategoryForm(Model model) {
        model.addAttribute("subCategoryRequest", SubCategoryRequest.builder().build());
        return "admin/subcategory/createSubCategory";
    }

    @PostMapping("/subcategorias/crear/guardar")
    public String saveSubCategory(@ModelAttribute SubCategoryRequest request,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            subCategoryService.createSubCategory(request, imageFile);
            redirectAttributes.addFlashAttribute("success", "Subcategoría creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear subcategoría: " + e.getMessage());
        }
        return "redirect:/admin/subcategorias";
    }

    @PostMapping("/subcategorias/eliminar/{id}")
    public String deleteSubCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            subCategoryService.deleteSubCategory(id);
            redirectAttributes.addFlashAttribute("success", "Subcategoría eliminada exitosamente");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/subcategorias";
    }

    /* Administracion usuarios */
    @GetMapping("/usuarios")
    public String showUsers(Model model) {

        List<UserDto> user = userService.getAllUsers();
        model.addAttribute("users", user);

        return "/admin/users/showUser";
    }

}
