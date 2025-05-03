package com.example.kunturtatto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.kunturtatto.model.Design;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {
    List<Design> findBySubCategoryId(Long subCategoryId);
    
    List<Design> findBySubCategoryCategoryId(Long categoryId);
    
    List<Design> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleQuery, String descriptionQuery);
    
    @Query("SELECT d FROM Design d JOIN FETCH d.subCategory WHERE d.id = :id")
    Design findByIdWithSubCategory(Long id);

    @Query("SELECT d FROM Design d JOIN FETCH d.subCategory sc JOIN FETCH sc.category")
    List<Design> findAllWithRelations();
}