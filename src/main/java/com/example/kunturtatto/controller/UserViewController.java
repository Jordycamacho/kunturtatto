package com.example.kunturtatto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;

import java.util.List;

@Controller
@RequestMapping("/KunturTattoo")
public class UserViewController {

    private final ICategoryDesignService categoryDesignService;
    private final IDesignService designService;

    public UserViewController(ICategoryDesignService categoryDesignService, IDesignService designService) {
        this.categoryDesignService = categoryDesignService;
        this.designService = designService;
    }

    @Operation(summary = "Mostrar la página principal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página principal mostrada con éxito")
    })
    @GetMapping("")
    public String showHomePage(Model model) {
        addCategoriesToModel(model);
        return "user/index";
    }

    @Operation(summary = "Mostrar diseños con filtro opcional por categoría")
    @Parameter(name = "categoryId", description = "ID opcional de la categoría para filtrar los diseños")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de diseños mostrada con éxito")
    })
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

    @Operation(summary = "Mostrar página de contacto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de contacto mostrada con éxito")
    })
    @GetMapping("/contacto")
    public String showContactPage(Model model) {
        addCategoriesToModel(model);
        return "user/contact";
    }

    private void addCategoriesToModel(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
    }
}
