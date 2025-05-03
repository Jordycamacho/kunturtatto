package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.mapper.CategoryMapper;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.repository.CategoryRepository;
import com.example.kunturtatto.request.CategoryRequest;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequest request, MultipartFile imageFile) {
        log.info("Creating new category with name: {}", request.getName());
        
        String imageName = imageFile != null && !imageFile.isEmpty() ? 
            imageService.saveImageNormal(imageFile) : 
            imageService.getDefaultImage();

        Category category = Category.builder()
                .name(request.getName())
                .image(imageName)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.debug("Category created successfully with ID: {}", savedCategory.getId());
        
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryRequest request, MultipartFile imageFile) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (imageFile != null && !imageFile.isEmpty()) {
            log.debug("Updating category image for ID: {}", id);
            String oldImage = category.getImage();
            String newImage = imageService.saveImageNormal(imageFile);
            category.setImage(newImage);
            imageService.deleteImageNormal(oldImage);
        }

        category.setName(request.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with ID: {} updated successfully", id);
        
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.debug("Fetching category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow();
        
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        log.debug("Fetching all categories");
        
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow();

        // Delete associated image
        if (!category.getImage().equals(imageService.getDefaultImage())) {
            imageService.deleteImageNormal(category.getImage());
        }

        categoryRepository.delete(category);
        log.info("Category with ID: {} deleted successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesWithSubcategories() {
        log.debug("Fetching all categories with their subcategories");
        
        return categoryRepository.findAllWithSubcategories().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategoryImage(Long id, MultipartFile imageFile) {
        log.info("Updating image for category with ID: {}", id);
        
        if (imageFile == null || imageFile.isEmpty()) {
            log.warn("Empty image file provided for category ID: {}", id);
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow();

        String oldImage = category.getImage();
        String newImage = imageService.saveImageNormal(imageFile);
        category.setImage(newImage);
        
        if (!oldImage.equals(imageService.getDefaultImage())) {
            imageService.deleteImageNormal(oldImage);
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Image updated successfully for category ID: {}", id);
        
        return categoryMapper.toCategoryDto(updatedCategory);
    }


}