package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.request.SubCategoryRequest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubCategoryService {
    SubCategoryDto createSubCategory(SubCategoryRequest request, MultipartFile imageFile);
    SubCategoryDto updateSubCategory(Long id, SubCategoryRequest request, MultipartFile imageFile);
    SubCategoryDto getSubCategoryById(Long id);
    List<SubCategoryDto> getAllSubCategories();
    List<SubCategoryDto> getSubCategoriesByCategory(Long categoryId);
    void deleteSubCategory(Long id);
    SubCategoryDto updateSubCategoryImage(Long id, MultipartFile imageFile);
}
