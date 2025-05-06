package com.example.kunturtatto.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.kunturtatto.model.Design;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {


    @EntityGraph(attributePaths = {"subCategory", "subCategory.category"})
    @Query("SELECT d FROM Design d WHERE d.subCategory.id = :subCategoryId")
    Page<Design> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"subCategory", "subCategory.category"})
    @Query("SELECT d FROM Design d WHERE d.subCategory.category.id = :categoryId")
    Page<Design> findBySubCategoryCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    List<Design> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleQuery, String descriptionQuery);
    
    @Query("SELECT d FROM Design d JOIN FETCH d.subCategory WHERE d.id = :id")
    Design findByIdWithSubCategory(Long id);

    @Query("SELECT d FROM Design d JOIN FETCH d.subCategory sc JOIN FETCH sc.category")
    List<Design> findAllWithRelations();

    @EntityGraph(attributePaths = {"subCategory", "subCategory.category"})
    @Query("SELECT d FROM Design d")
    Page<Design> findAllWithRelationsPageable(Pageable pageable);
}