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
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import com.example.kunturtatto.service.SubCategoryService;

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

        return "/admin/users/showUser";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        return "redirect:/usuarios";
    }

    /* Administracion citas *//*
                               * @GetMapping("/citas")
                               * 
                               * @PreAuthorize("hasAuthority('CREATE')")
                               * public String showAppointments(Model model) {
                               * 
                               * List<Appointment> appointments = appointmentService.findAll().stream()
                               * .sorted((a1, a2) -> {
                               * int dateCompare = a1.getDate().compareTo(a2.getDate());
                               * if (dateCompare == 0) {
                               * return a1.getTime().compareTo(a2.getTime());
                               * }
                               * return dateCompare;
                               * })
                               * .collect(Collectors.toList());
                               * 
                               * model.addAttribute("appointments", appointments);
                               * 
                               * return "/admin/appointment/showAppointment";
                               * }
                               * 
                               * @GetMapping("/crear-citas")
                               * 
                               * @PreAuthorize("hasAuthority('CREATE')")
                               * public String createAppointments(Model model) {
                               * List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
                               * List<Design> designs = designService.findAll();
                               * 
                               * model.addAttribute("designs", designs);
                               * model.addAttribute("categories", categoryDesigns);
                               * 
                               * return "/admin/appointment/createAppointment";
                               * }
                               * 
                               * @GetMapping("/citas/editar/{id}")
                               * 
                               * @PreAuthorize("hasAuthority('UPDATE')")
                               * public String editAppointmentForm(@PathVariable Long id, Model model) {
                               * List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
                               * model.addAttribute("categories", categoryDesigns);
                               * 
                               * List<Design> designs = designService.findAll();
                               * model.addAttribute("design", designs);
                               * Appointment appointment = appointmentService.findById(id);
                               * model.addAttribute("appointment", appointment);
                               * 
                               * return "/admin/appointment/editAppointment";
                               * }
                               * 
                               * @GetMapping("/citas/verDetalles/{id}")
                               * 
                               * @PreAuthorize("hasAuthority('CREATE')")
                               * public String showAppointmentDetails(@PathVariable Long id, Model model) {
                               * List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
                               * model.addAttribute("categories", categoryDesigns);
                               * 
                               * Appointment appointment = appointmentService.findById(id);
                               * model.addAttribute("appointment", appointment);
                               * return "/admin/appointment/showAppointmentDetails";
                               * }
                               * 
                               * @PostMapping("/crear-citas/crear")
                               * 
                               * @PreAuthorize("hasAuthority('CREATE')")
                               * public String createAppointment(Appointment appointment) {
                               * appointmentService.save(appointment);
                               * return "redirect:/admin/citas";
                               * }
                               * 
                               * @PostMapping("/citas/editar/guardar/{id}")
                               * 
                               * @PreAuthorize("hasAuthority('UPDATE')")
                               * public String editAppointment(@PathVariable Long id, @ModelAttribute
                               * Appointment appointment,
                               * RedirectAttributes redirectAttributes) {
                               * appointment.setIdAppointment(id);
                               * appointmentService.save(appointment);
                               * redirectAttributes.addFlashAttribute("message",
                               * "Cita actualizada exitosamente.");
                               * return "redirect:/admin/citas";
                               * }
                               * 
                               * @PostMapping("/citas/eliminar/{id}")
                               * 
                               * @PreAuthorize("hasAuthority('DELETE')")
                               * public String deleteAppointment(@PathVariable Long id, RedirectAttributes
                               * redirectAttributes) {
                               * appointmentService.deleteById(id);
                               * redirectAttributes.addFlashAttribute("message",
                               * "Cita eliminada exitosamente.");
                               * return "redirect:/admin/citas";
                               * }
                               */
}
