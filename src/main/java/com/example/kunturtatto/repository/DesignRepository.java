package com.example.kunturtatto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {

    /**
     * Encuentra una lista de diseños asociados a una categoría específica.
     * 
     * @param categoryDesign Objeto CategoryDesign para filtrar diseños
     * @return Lista de diseños que pertenecen a la categoría especificada
     */
    List<Design> findByCategoryDesign(CategoryDesign categoryDesign);
}
