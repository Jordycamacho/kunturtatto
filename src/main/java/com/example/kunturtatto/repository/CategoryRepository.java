package com.example.kunturtatto.repository;

import com.example.kunturtatto.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories")
    List<Category> findAllWithSubcategories();
}