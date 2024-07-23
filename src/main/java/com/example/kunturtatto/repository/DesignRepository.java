package com.example.kunturtatto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;

public interface DesignRepository extends JpaRepository<Design, Long>{

    List<Design>findByCategoryDesign(CategoryDesign categoryDesign);

}
