package com.example.kunturtatto.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.repository.DesignRepository;
import com.example.kunturtatto.service.IDesignService;

@Service
public class IDesignServiceImpl implements IDesignService {

    @Autowired
    private DesignRepository designRepository;
    
    @Override
    public List<Design> findAll() {
        return designRepository.findAll();
    }

    @Override
    public Optional<Design> findById(Long idDesign) {
        return designRepository.findById(idDesign);
    }

    @Override
    public Design save(Design design) {
        return designRepository.save(design);
    }

    @Override
    public void delete(Long idDesign) {
        if (designRepository.existsById(idDesign)) {
            designRepository.deleteById(idDesign);
        }else{
            throw new IllegalArgumentException("No se puede actualizar: Diseño no encontrado.");
        }
    }

    @Override
    public Design update(Design design) {
        if (designRepository.existsById(design.getIdDesign())) {
            return designRepository.save(design);        
        }else{
            throw new IllegalArgumentException("No se puede actualizar: Diseño no encontrado.");
        }
    }

    @Override
    public List<Design> findByCategoryDesign(Long idCategory) {
        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setIdCategoryDesign(idCategory);
        return designRepository.findByCategoryDesign(categoryDesign);
    }
}
