package com.example.kunturtatto.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import com.example.kunturtatto.service.SubCategoryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/KunturTattoo")
@PreAuthorize("permitAll()")
public class UserController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;
    @Autowired
    private DesignService designService;

    @ModelAttribute("categories")
    public List<CategoryDto> categories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("")
    public String home(Model model) {

        List<SubCategoryDto> tattooSubcategories = subCategoryService.getSubCategoriesByCategory(1L);
        List<SubCategoryDto> tattooCategories = subCategoryService.getSubCategoriesByCategory(2L);

        model.addAttribute("tattooSubcategories", tattooSubcategories);
        model.addAttribute("designSubcategories", tattooCategories);

        return "user/index";
    }

    @GetMapping("/ingresar")
    public String showLogin(Model model) {

        return "user/login";
    }

    @GetMapping("/registro")
    public String showSingUp(Model model) {
        return "user/singup";
    }

    @GetMapping("/diseños")
    public String showDesigns(@RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            Model model) {

        List<DesignDto> designs;
        Long effectiveCategoryId = categoryId;

        if (subCategoryId != null && categoryId == null) {
            SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
            effectiveCategoryId = subCategory.getCategoryId();
        }

        if (subCategoryId != null) {
            designs = designService.getDesignsBySubCategory(subCategoryId);
            SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
            model.addAttribute("pageTitle", subCategory.getName());
        } else if (effectiveCategoryId != null) {
            designs = designService.getDesignsByCategory(effectiveCategoryId);
            CategoryDto category = categoryService.getCategoryById(effectiveCategoryId);
            model.addAttribute("pageTitle", category.getName());
        } else {
            designs = designService.getAllDesigns();
            model.addAttribute("pageTitle", "Todos los diseños");
        }

        model.addAttribute("selectedCategoryId", effectiveCategoryId);
        model.addAttribute("selectedSubCategoryId", subCategoryId);
        model.addAttribute("designs", designs);
        model.addAttribute("categories", categoryService.getAllCategories());

        // Agregar subcategorías de la categoría seleccionada
        if (effectiveCategoryId != null) {
            model.addAttribute("currentSubCategories",
                    subCategoryService.getSubCategoriesByCategory(effectiveCategoryId));
        }

        return "user/designs";
    }

    @GetMapping("/contacto")
    public String showContact(Model model) {
        return "user/contact";
    }

    @PostMapping("/contacto/guardar")
    public String saveContact() {

        return "redirect:/KunturTattoo/contacto";
    }

    @PostMapping("/login/guardar")
    public String saveLogIn() {

        return "redirect:KunturTattoo";
    }

    @PostMapping("/registro/guardar")
    public String saveSignup(@RequestParam("email") String email, @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        String formattedDate = formatter.format(dateNow);

        // Crear un nuevo usuario
        User user = new User();
        user.setEmail(email);

        user.setPassword(password);

        user.setRegistrationDate(dateNow);

        // Guardar el usuario en el servicio

        redirectAttributes.addFlashAttribute("message", "Usuario registrado exitosamente.");
        return "redirect:/KunturTattoo/ingresar";
    }

}