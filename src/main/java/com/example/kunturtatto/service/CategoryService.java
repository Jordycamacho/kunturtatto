package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.request.CategoryRequest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryRequest request, MultipartFile imageFile);
    CategoryDto updateCategory(Long id, CategoryRequest request, MultipartFile imageFile);
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getAllCategories();
    void deleteCategory(Long id);
    List<CategoryDto> getCategoriesWithSubcategories();
    CategoryDto updateCategoryImage(Long id, MultipartFile imageFile);
}