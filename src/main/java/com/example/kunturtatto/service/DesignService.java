package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.request.DesignRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignService {
    DesignDto createDesign(DesignRequest request, MultipartFile imageFile);
    DesignDto updateDesign(Long id, DesignRequest request, MultipartFile imageFile);
    DesignDto getDesignById(Long id);
    List<DesignDto> getAllDesigns();
    Page<DesignDto> getAllDesignspPageable(Pageable pageable);
    Page<DesignDto> getDesignsBySubCategory(Long subCategoryId, Pageable pageable);
    Page<DesignDto> getDesignsByCategory(Long categoryId, Pageable pageable);
    void deleteDesign(Long id);
    DesignDto updateDesignImage(Long id, MultipartFile imageFile);
    List<DesignDto> searchDesigns(String query);
}