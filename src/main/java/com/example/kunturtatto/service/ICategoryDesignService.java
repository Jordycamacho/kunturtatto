package com.example.kunturtatto.service;

import java.util.List;
import java.util.Optional;

import com.example.kunturtatto.model.CategoryDesign;

/**
 * Servicio para manejar operaciones relacionadas con categorías de diseño.
 */
public interface ICategoryDesignService {

    /**
     * Obtiene todas las categorías de diseño.
     * 
     * @return Lista de todas las categorías de diseño
     */
    List<CategoryDesign> findAll();

    /**
     * Encuentra una categoría de diseño por su ID.
     * 
     * @param idCategoryDesign ID de la categoría de diseño a buscar
     * @return Optional que puede contener la categoría encontrada
     */
    Optional<CategoryDesign> findById(Long idCategoryDesign);

    /**
     * Guarda una nueva categoría de diseño.
     * 
     * @param categoryDesign Objeto CategoryDesign a guardar
     * @return Categoría de diseño guardada
     */
    CategoryDesign save(CategoryDesign categoryDesign);

    /**
     * Elimina una categoría de diseño por su ID.
     * 
     * @param id ID de la categoría a eliminar
     */
    void deleteById(Long id);
}
