package com.example.kunturtatto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kunturtatto.model.CategoryDesign;


@Repository
public interface CategoryDesignRepository extends JpaRepository<CategoryDesign, Long> {
}
