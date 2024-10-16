package com.example.kunturtatto.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.repository.CategoryDesignRepository;
import com.example.kunturtatto.service.ICategoryDesignService;

/**
 * Servicio para gestionar categorías de diseño.
 */
@Service
public class ICategoryDesignServiceImpl implements ICategoryDesignService{

    @Autowired
    private CategoryDesignRepository categoryDesignRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDesign> findAll() {
        return categoryDesignRepository.findAll(); 
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDesign> findById(Long idCategoryDesign) {
        return categoryDesignRepository.findById(idCategoryDesign);
    }

    @Override
    @Transactional
    public CategoryDesign save(CategoryDesign categoryDesign) {
        return categoryDesignRepository.save(categoryDesign);
    }

    @Override
    @Transactional
    public void deleteById(Long idCategoryDesign) {
        if (!categoryDesignRepository.existsById(idCategoryDesign)) {
            throw new IllegalArgumentException("La categoría de diseño con ID " + idCategoryDesign + " no existe.");
        }
        categoryDesignRepository.deleteById(idCategoryDesign);
    }
}
