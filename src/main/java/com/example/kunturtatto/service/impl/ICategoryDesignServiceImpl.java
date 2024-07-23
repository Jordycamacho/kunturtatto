package com.example.kunturtatto.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.repository.CategoryDesignRepository;
import com.example.kunturtatto.service.ICategoryDesignService;

@Service
public class ICategoryDesignServiceImpl implements ICategoryDesignService{

    @Autowired
    private CategoryDesignRepository categoryDesignRepository;

    @Override
    public List<CategoryDesign> findAll() {
        return categoryDesignRepository.findAll(); 
    }

    @Override
    public Optional<CategoryDesign> findById(Long idCategoryDesign) {
        return categoryDesignRepository.findById(idCategoryDesign);
    }

    @Override
    public CategoryDesign save(CategoryDesign categoryDesign) {
        return categoryDesignRepository.save(categoryDesign);
    }

    @Override
    public void deleteById(Long idCategoryDesign) {
        categoryDesignRepository.deleteById(idCategoryDesign);
    }

    
}