package com.example.kunturtatto.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para gestionar la carga y eliminación de archivos de imagen.
 */
@Service
public class IUploadFileService {

    private String folder = "images/";

    /**
     * Guarda una imagen en el sistema de archivos.
     * 
     * @param file Archivo de imagen a guardar
     * @return Nombre de la imagen guardada
     * @throws IOException Si ocurre un error al guardar el archivo
     */
    public String saveImages(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folder + file.getOriginalFilename());
            Files.write(path, bytes);
            return file.getOriginalFilename();
        }
        return "default.jpg";
    }

    /**
     * Elimina una imagen del sistema de archivos.
     * 
     * @param imageName Nombre de la imagen a eliminar
     */
    public void deleteImage(String imageName) {
        String path = "images/";
        File file = new File(path + imageName);
        if (file.exists()) {
            file.delete();
        }
    }
}