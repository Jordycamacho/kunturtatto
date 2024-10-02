package com.example.kunturtatto.service;

import java.util.List;
import java.util.Optional;

import com.example.kunturtatto.model.Design;

public interface IDesignService {
    /**
     * Obtiene todos los diseños.
     * 
     * @return Lista de todos los diseños
     */
    List<Design> findAll();
    /**
     * Encuentra un diseño por su ID.
     * 
     * @param idDesign ID del diseño a buscar
     * @return Optional que puede contener el diseño si existe
     */
    Optional<Design> findById(Long idDesign);
    /**
     * Guarda un diseño nuevo o existente.
     * 
     * @param design Objeto del diseño a guardar
     * @return Diseño guardado
     */
    Design save(Design design);
    /**
     * Elimina un diseño por su ID.
     * 
     * @param idDesign ID del diseño a eliminar
     */
    void delete(Long idDesign);
     /**
     * Actualiza un diseño existente.
     * 
     * @param design Objeto del diseño a actualizar
     * @return Diseño actualizado
     */
    Design update(Design design);
     /**
     * Actualiza un diseño existente.
     * 
     * @param design Objeto del diseño a actualizar
     * @return Diseño actualizado
     */
    List<Design> findByCategoryDesign(Long idCategory);
}
