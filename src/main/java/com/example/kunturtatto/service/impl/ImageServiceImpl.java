package com.example.kunturtatto.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.kunturtatto.service.ImageService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Directorio de imágenes creado en: {}", uploadDir);
        } catch (IOException e) {
            log.error("No se pudo crear el directorio de imágenes", e);
        }
    }

    @Override
    public List<String> saveImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.singletonList(getDefaultImage());
        }

        List<String> savedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    savedImages.add(fileName);
                } catch (IOException e) {
                    log.error("Error al guardar la imagen: {}", e.getMessage());

                }
            }
        }

        if (savedImages.isEmpty()) {
            savedImages.add(getDefaultImage());
        }

        return savedImages;
    }

    @Override
    public void deleteImages(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        for (String fileName : fileNames) {
            deleteImageFile(fileName);
        }
    }

    private void deleteImageFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            File file = filePath.toFile();

            Path uploadPath = Paths.get(uploadDir).normalize().toAbsolutePath();
            if (!filePath.toAbsolutePath().startsWith(uploadPath)) {
                log.warn("Intento de eliminar archivo fuera del directorio permitido: {}", filePath);
                return;
            }

            if (file.exists() && !fileName.equals(getDefaultImage())) {
                if (file.delete()) {
                    log.info("Imagen eliminada: {}", filePath);
                } else {
                    log.warn("No se pudo eliminar la imagen: {}", filePath);
                    System.gc();
                    if (file.delete()) {
                        log.info("Imagen eliminada en segundo intento: {}", filePath);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error al eliminar la imagen {}: {}", fileName, e.getMessage());
        }
    }

    @Override
    public String saveImageNormal(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return getDefaultImage();
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }

    @Override
    public void deleteImageNormal(String fileName) {
        if (fileName == null || fileName.isEmpty() || fileName.equals(getDefaultImage())) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Path uploadPath = Paths.get(uploadDir).normalize();

            if (!filePath.startsWith(uploadPath)) {
                log.warn("Intento de eliminar archivo fuera del directorio permitido: {}", filePath);
                return;
            }

            File file = filePath.toFile();
            if (file.exists()) {
                if (!file.delete()) {
                    log.warn("Primer intento fallido, liberando recursos...");
                    System.gc();
                    if (!file.delete()) {
                        log.error("No se pudo eliminar la imagen: {}", filePath);
                    }
                }
                log.info("Imagen eliminada: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error al eliminar la imagen {}: {}", fileName, e.getMessage());
        }
    }

    @Override
    public String getDefaultImage() {
        return "default.jpg";
    }
}