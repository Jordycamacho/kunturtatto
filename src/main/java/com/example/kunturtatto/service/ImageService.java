package com.example.kunturtatto.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    List<String> saveImages(List<MultipartFile> files);

    void deleteImages(List<String> fileNames);

    String saveImageNormal(MultipartFile file);

    public void deleteImageNormal(String fileName);

    String getDefaultImage();
}
