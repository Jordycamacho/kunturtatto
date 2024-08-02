package com.example.kunturtatto.service;

import java.util.List;
import java.util.Optional;

import com.example.kunturtatto.model.Design;

public interface IDesignService {

    List<Design> findAll();
    Optional<Design> findById(Long idDesign);
    Design save(Design design);
    void delete(Long idDesign);
    Design update(Design design);
    List<Design> findByCategoryDesign(Long idCategory);
}
