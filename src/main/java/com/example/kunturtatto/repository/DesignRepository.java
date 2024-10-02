package com.example.kunturtatto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;

public interface DesignRepository extends JpaRepository<Design, Long> {

    /**
     * Encuentra una lista de diseños filtrados por categoría de diseño.
     * 
     * @param categoryDesign Objeto de la categoría de diseño
     * @return Lista de diseños que pertenecen a la categoría especificada
     */
    List<Design> findByCategoryDesign(CategoryDesign categoryDesign);
}
