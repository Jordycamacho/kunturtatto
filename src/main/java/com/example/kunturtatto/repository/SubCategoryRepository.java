package com.example.kunturtatto.repository;

import com.example.kunturtatto.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategoryId(Long categoryId);
    
    @Modifying
    @Query(value = "DELETE FROM sub_categories WHERE id = :id", nativeQuery = true)
    int deleteNativeById(@Param("id") Long id);
    
    @Modifying
    @Query("DELETE FROM SubCategory s WHERE s.id = :id")
    int deleteDirectlyById(@Param("id") Long id);
}