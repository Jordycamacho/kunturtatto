package com.example.kunturtatto.service;

import java.util.List;
import java.util.Optional;

import com.example.kunturtatto.model.CategoryDesign;

public interface ICategoryDesignService {
    
    List<CategoryDesign> findAll();
    Optional<CategoryDesign> findById(Long idCategoryDesign);
    CategoryDesign save(CategoryDesign categoryDesign);
    void deleteById(Long id);

}
