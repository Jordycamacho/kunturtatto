package com.example.kunturtatto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;

@Controller
@RequestMapping("/KunturTattoo")
public class UserController {

    @Autowired
    private ICategoryDesignService categoryDesignService;

    @Autowired
    private IDesignService designService;

    /**
     * Método para mostrar la página principal.
     * @param model objeto para pasar datos a la vista
     * @return la plantilla de la página de inicio
     */
    @GetMapping("")
    public String showHomePage(Model model) {
        addCategoriesToModel(model);
        return "user/index";
    }

    /**
     * Método para mostrar los diseños, filtrando opcionalmente por categoría.
     * @param categoryId ID opcional de la categoría para filtrar los diseños
     * @param model objeto para pasar datos a la vista
     * @return la plantilla de la página de diseños
     */
    @GetMapping("/diseños")
    public String showDesigns(@RequestParam(required = false) Long categoryId, Model model) {
        List<Design> designs = (categoryId != null) ? 
            designService.findByCategoryDesign(categoryId) : 
            designService.findAll();

        addCategoriesToModel(model);
        model.addAttribute("designs", designs);
        model.addAttribute("selectedCategoryId", categoryId);
        
        return "user/designs";
    }
    
    /**
     * Método para mostrar la página de contacto.
     * @param model objeto para pasar datos a la vista
     * @return la plantilla de la página de contacto
     */
    @GetMapping("/contacto")
    public String showContactPage(Model model) {
        addCategoriesToModel(model);
        return "user/contact";
    }

    /**
     * Método de utilidad para añadir la lista de categorías al modelo.
     * @param model objeto para pasar datos a la vista
     */
    private void addCategoriesToModel(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
    }
}
