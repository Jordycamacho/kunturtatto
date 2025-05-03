package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.request.DesignRequest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignService {
    DesignDto createDesign(DesignRequest request, MultipartFile imageFile);
    DesignDto updateDesign(Long id, DesignRequest request, MultipartFile imageFile);
    DesignDto getDesignById(Long id);
    List<DesignDto> getAllDesigns();
    List<DesignDto> getDesignsBySubCategory(Long subCategoryId);
    List<DesignDto> getDesignsByCategory(Long categoryId);
    void deleteDesign(Long id);
    DesignDto updateDesignImage(Long id, MultipartFile imageFile);
    List<DesignDto> searchDesigns(String query);
}