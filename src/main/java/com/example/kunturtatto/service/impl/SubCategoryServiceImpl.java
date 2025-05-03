package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.mapper.SubCategoryMapper;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.repository.CategoryRepository;
import com.example.kunturtatto.repository.SubCategoryRepository;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.ImageService;
import com.example.kunturtatto.service.SubCategoryService;
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
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final ImageService imageService;

    @Override
    @Transactional
    public SubCategoryDto createSubCategory(SubCategoryRequest request, MultipartFile imageFile) {
        log.info("Creating new subcategory with name: {} for category ID: {}", 
                request.getName(), request.getCategoryId());
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow();

        String imageName = imageFile != null && !imageFile.isEmpty() ? 
                imageService.saveImageNormal(imageFile) : 
                imageService.getDefaultImage();

        SubCategory subCategory = SubCategory.builder()
                .name(request.getName())
                .image(imageName)
                .category(category)
                .build();

        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        log.debug("SubCategory created successfully with ID: {}", savedSubCategory.getId());
        
        return subCategoryMapper.toSubCategoryDto(savedSubCategory);
    }

    @Override
    @Transactional
    public SubCategoryDto updateSubCategory(Long id, SubCategoryRequest request, MultipartFile imageFile) {
        log.info("Updating subcategory with ID: {}", id);
        
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow();

        if (imageFile != null && !imageFile.isEmpty()) {
            log.debug("Updating subcategory image for ID: {}", id);
            String oldImage = subCategory.getImage();
            String newImage = imageService.saveImageNormal(imageFile);
            subCategory.setImage(newImage);
            imageService.deleteImageNormal(oldImage);
        }

        if (!subCategory.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow();
            subCategory.setCategory(newCategory);
        }

        subCategory.setName(request.getName());
        SubCategory updatedSubCategory = subCategoryRepository.save(subCategory);
        log.info("SubCategory with ID: {} updated successfully", id);
        
        return subCategoryMapper.toSubCategoryDto(updatedSubCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryDto getSubCategoryById(Long id) {
        log.debug("Fetching subcategory with ID: {}", id);
        
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow();
        
        return subCategoryMapper.toSubCategoryDto(subCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryDto> getAllSubCategories() {
        log.debug("Fetching all subcategories");
        
        return subCategoryRepository.findAll().stream()
                .map(subCategoryMapper::toSubCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryDto> getSubCategoriesByCategory(Long categoryId) {
        log.debug("Fetching subcategories for category ID: {}", categoryId);
        
        return subCategoryRepository.findByCategoryId(categoryId).stream()
                .map(subCategoryMapper::toSubCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSubCategory(Long id) {
        log.info("Deleting subcategory with ID: {}", id);
        
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow();

        // Delete associated image
        if (!subCategory.getImage().equals(imageService.getDefaultImage())) {
            imageService.deleteImageNormal(subCategory.getImage());
        }

        subCategoryRepository.delete(subCategory);
        log.info("SubCategory with ID: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public SubCategoryDto updateSubCategoryImage(Long id, MultipartFile imageFile) {
        log.info("Updating image for subcategory with ID: {}", id);
        
        if (imageFile == null || imageFile.isEmpty()) {
            log.warn("Empty image file provided for subcategory ID: {}", id);
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow();

        String oldImage = subCategory.getImage();
        String newImage = imageService.saveImageNormal(imageFile);
        subCategory.setImage(newImage);
        
        if (!oldImage.equals(imageService.getDefaultImage())) {
            imageService.deleteImageNormal(oldImage);
        }

        SubCategory updatedSubCategory = subCategoryRepository.save(subCategory);
        log.info("Image updated successfully for subcategory ID: {}", id);
        
        return subCategoryMapper.toSubCategoryDto(updatedSubCategory);
    }
}