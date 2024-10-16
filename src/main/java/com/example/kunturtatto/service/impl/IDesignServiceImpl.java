package com.example.kunturtatto.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.repository.DesignRepository;
import com.example.kunturtatto.service.IDesignService;

/**
 * Servicio para gestionar operaciones de diseño.
 */
@Service
public class IDesignServiceImpl implements IDesignService {

    @Autowired
    private DesignRepository designRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Design> findAll() {
        return designRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Design> findById(Long idDesign) {
        return designRepository.findById(idDesign);
    }

    @Override
    @Transactional
    public Design save(Design design) {
        return designRepository.save(design);
    }

    @Override
    @Transactional
    public void delete(Long idDesign) {
        if (!designRepository.existsById(idDesign)) {
            throw new IllegalArgumentException("El diseño con ID " + idDesign + " no existe.");
        }
        designRepository.deleteById(idDesign);
    }

    @Override
    @Transactional
    public Design update(Design design) {
        if (!designRepository.existsById(design.getIdDesign())) {
            throw new IllegalArgumentException("El diseño no existe: No se puede actualizar.");
        }
        return designRepository.save(design);        
    }

    @Override
    @Transactional(readOnly = true)
    public List<Design> findByCategoryDesign(Long idCategory) {
        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setIdCategoryDesign(idCategory);
        return designRepository.findByCategoryDesign(categoryDesign);
    }
}
