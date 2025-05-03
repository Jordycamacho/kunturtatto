package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.request.DesignRequest;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.DesignMapper;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.repository.DesignRepository;
import com.example.kunturtatto.repository.SubCategoryRepository;
import com.example.kunturtatto.service.DesignService;
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
public class DesignServiceImpl implements DesignService {

    private final DesignRepository designRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final DesignMapper designMapper;
    private final ImageService imageService;

    @Override
    @Transactional
    public DesignDto createDesign(DesignRequest request, MultipartFile imageFile) {
        log.info("Creating new design with title: {}", request.getTitle());
        
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> {
                    log.error("SubCategory not found with ID: {}", request.getSubCategoryId());
                    return new ResourceNotFoundException("SubCategory not found with ID: " + request.getSubCategoryId());
                });

        String imageName = imageFile != null && !imageFile.isEmpty() ? 
                imageService.saveImageNormal(imageFile) : 
                imageService.getDefaultImage();

        Design design = Design.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .image(imageName)
                .subCategory(subCategory)
                .build();

        Design savedDesign = designRepository.save(design);
        log.debug("Design created successfully with ID: {}", savedDesign.getId());
        
        return designMapper.toDesignDto(savedDesign);
    }

    @Override
    @Transactional
    public DesignDto updateDesign(Long id, DesignRequest request, MultipartFile imageFile) {
        log.info("Updating design with ID: {}", id);
        
        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Design not found with ID: {}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id);
                });

        if (imageFile != null && !imageFile.isEmpty()) {
            log.debug("Updating design image for ID: {}", id);
            String oldImage = design.getImage();
            String newImage = imageService.saveImageNormal(imageFile);
            design.setImage(newImage);
            imageService.deleteImageNormal(oldImage);
        }

        if (!design.getSubCategory().getId().equals(request.getSubCategoryId())) {
            SubCategory newSubCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> {
                        log.error("SubCategory not found with ID: {}", request.getSubCategoryId());
                        return new ResourceNotFoundException("SubCategory not found with ID: " + request.getSubCategoryId());
                    });
            design.setSubCategory(newSubCategory);
        }

        design.setTitle(request.getTitle());
        design.setDescription(request.getDescription());
        
        Design updatedDesign = designRepository.save(design);
        log.info("Design with ID: {} updated successfully", id);
        
        return designMapper.toDesignDto(updatedDesign);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignDto getDesignById(Long id) {
        log.debug("Fetching design with ID: {}", id);
        
        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Design not found with ID: {}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id);
                });
        
        return designMapper.toDesignDto(design);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignDto> getAllDesigns() {
        log.debug("Fetching all designs");
        
        return designRepository.findAllWithRelations().stream()
                .map(designMapper::toDesignDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignDto> getDesignsBySubCategory(Long subCategoryId) {
        log.debug("Fetching designs for subcategory ID: {}", subCategoryId);
        
        return designRepository.findBySubCategoryId(subCategoryId).stream()
                .map(designMapper::toDesignDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignDto> getDesignsByCategory(Long categoryId) {
        log.debug("Fetching designs for category ID: {}", categoryId);
        
        return designRepository.findBySubCategoryCategoryId(categoryId).stream()
                .map(designMapper::toDesignDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDesign(Long id) {
        log.info("Deleting design with ID: {}", id);
        
        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Design not found for deletion with ID: {}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id);
                });

        // Delete associated image
        if (!design.getImage().equals(imageService.getDefaultImage())) {
            imageService.deleteImageNormal(design.getImage());
        }

        designRepository.delete(design);
        log.info("Design with ID: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public DesignDto updateDesignImage(Long id, MultipartFile imageFile) {
        log.info("Updating image for design with ID: {}", id);
        
        if (imageFile == null || imageFile.isEmpty()) {
            log.warn("Empty image file provided for design ID: {}", id);
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Design not found with ID: {}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id);
                });

        String oldImage = design.getImage();
        String newImage = imageService.saveImageNormal(imageFile);
        design.setImage(newImage);
        
        if (!oldImage.equals(imageService.getDefaultImage())) {
            imageService.deleteImageNormal(oldImage);
        }

        Design updatedDesign = designRepository.save(design);
        log.info("Image updated successfully for design ID: {}", id);
        
        return designMapper.toDesignDto(updatedDesign);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignDto> searchDesigns(String query) {
        log.debug("Searching designs with query: {}", query);
        
        return designRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query).stream()
                .map(designMapper::toDesignDto)
                .collect(Collectors.toList());
    }
}