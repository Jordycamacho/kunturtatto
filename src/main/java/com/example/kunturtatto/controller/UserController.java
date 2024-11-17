package com.example.kunturtatto.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/KunturTattoo")
public class UserController {

    private final ICategoryDesignService categoryDesignService;
    private final IDesignService designService;

    public UserController(ICategoryDesignService categoryDesignService, IDesignService designService) {
        this.categoryDesignService = categoryDesignService;
        this.designService = designService;
    }

    @Operation(summary = "Obtener lista de diseños con filtro opcional por categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de diseños obtenida correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/designs")
    public ResponseEntity<List<Design>> getDesigns(
            @Parameter(description = "ID opcional de la categoría para filtrar los diseños") 
            @RequestParam(required = false) Long categoryId) {

        List<Design> designs = (categoryId != null) ?
            designService.findByCategoryDesign(categoryId) :
            designService.findAll();

        return ResponseEntity.ok(designs);
    }

    @Operation(summary = "Obtener lista de categorías")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente")
    })
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDesign>> getCategories() {
        List<CategoryDesign> categories = categoryDesignService.findAll();
        return ResponseEntity.ok(categories);
    }
}
