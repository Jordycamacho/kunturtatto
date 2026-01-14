package com.example.kunturtatto.repository;

import com.example.kunturtatto.model.SubCategory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    
    @Cacheable(value = "subcategoriesByCategoryRepo", key = "#categoryId")
    List<SubCategory> findByCategoryId(Long categoryId);
    
    // Método para cargar con diseños (para validación)
    @Cacheable(value = "subcategoryWithDesigns", key = "#id")
    @Query("SELECT s FROM SubCategory s LEFT JOIN FETCH s.designs WHERE s.id = :id")
    Optional<SubCategory> findByIdWithDesigns(@Param("id") Long id);
    
    // Eliminación nativa
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM sub_categories WHERE id = :id", nativeQuery = true)
    int deleteNativeById(@Param("id") Long id);
    
    // Eliminación JPQL
    @Transactional
    @Modifying
    @Query("DELETE FROM SubCategory s WHERE s.id = :id")
    int deleteDirectlyById(@Param("id") Long id);
    
    // Verificar si tiene diseños
    @Query("SELECT COUNT(d) > 0 FROM Design d WHERE d.subCategory.id = :id")
    boolean hasDesigns(@Param("id") Long id);
    
    // Contar diseños
    @Query("SELECT COUNT(d) FROM Design d WHERE d.subCategory.id = :id")
    int countDesigns(@Param("id") Long id);
}