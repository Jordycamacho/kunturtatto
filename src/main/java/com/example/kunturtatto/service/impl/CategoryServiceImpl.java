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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = { "categoriesList", "categoriesWithSubcategories" }, allEntries = true)
    public CategoryDto createCategory(CategoryRequest request, MultipartFile imageFile) {
        log.info("[CATEGORY_SERVICE] Iniciando creación de categoría. Datos recibidos: nombre={}, archivoImagen={}",
                request.getName(), imageFile != null ? imageFile.getOriginalFilename() : "null");

        long startTime = System.currentTimeMillis();

        String imageName = imageFile != null && !imageFile.isEmpty() ? imageService.saveImageNormal(imageFile)
                : imageService.getDefaultImage();

        log.debug("[CATEGORY_SERVICE] Nombre de imagen generado: {}", imageName);

        Category category = Category.builder()
                .name(request.getName())
                .image(imageName)
                .build();

        log.debug("[CATEGORY_SERVICE] Entidad Category construida: id=null, nombre={}, imagen={}",
                request.getName(), imageName);

        Category savedCategory = categoryRepository.save(category);

        long endTime = System.currentTimeMillis();
        log.info("[CATEGORY_SERVICE] Categoría creada exitosamente. ID={}, nombre={}, tiempoTotal={}ms",
                savedCategory.getId(), savedCategory.getName(), (endTime - startTime));

        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categoriesList", "categoriesWithSubcategories", "categoryById" }, key = "#id")
    public CategoryDto updateCategory(Long id, CategoryRequest request, MultipartFile imageFile) {
        log.info(
                "[CATEGORY_SERVICE] Iniciando actualización de categoría. ID={}, datosRecibidos: nombre={}, archivoImagen={}",
                id, request.getName(), imageFile != null && !imageFile.isEmpty() ? "presente" : "ausente");

        long startTime = System.currentTimeMillis();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[CATEGORY_SERVICE] Categoría no encontrada para actualización. ID={}", id);
                    return new IllegalArgumentException("Category not found");
                });

        log.debug("[CATEGORY_SERVICE] Categoría encontrada para actualización: id={}, nombreActual={}, imagenActual={}",
                category.getId(), category.getName(), category.getImage());

        if (imageFile != null && !imageFile.isEmpty()) {
            log.debug("[CATEGORY_SERVICE] Actualizando imagen de categoría. ID={}", id);
            String oldImage = category.getImage();
            String newImage = imageService.saveImageNormal(imageFile);
            category.setImage(newImage);
            imageService.deleteImageNormal(oldImage);

            log.debug("[CATEGORY_SERVICE] Imagen actualizada. ImagenAnterior={}, imagenNueva={}", oldImage, newImage);
        }

        category.setName(request.getName());
        Category updatedCategory = categoryRepository.save(category);

        long endTime = System.currentTimeMillis();
        log.info("[CATEGORY_SERVICE] Categoría actualizada exitosamente. ID={}, nombreNuevo={}, tiempoTotal={}ms",
                id, updatedCategory.getName(), (endTime - startTime));

        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryById", key = "#id")
    public CategoryDto getCategoryById(Long id) {
        log.debug("[CATEGORY_SERVICE] Obteniendo categoría por ID. ID={}", id);

        long startTime = System.currentTimeMillis();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[CATEGORY_SERVICE] Categoría no encontrada. ID={}", id);
                    return new IllegalArgumentException("Category not found");
                });

        long endTime = System.currentTimeMillis();
        log.debug("[CATEGORY_SERVICE] Categoría obtenida. ID={}, nombre={}, tiempoConsulta={}ms",
                id, category.getName(), (endTime - startTime));

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoriesList")
    public List<CategoryDto> getAllCategories() {
        log.debug("[CATEGORY_SERVICE] Obteniendo todas las categorías");

        long startTime = System.currentTimeMillis();

        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("[CATEGORY_SERVICE] Total de categorías obtenidas: {}, tiempoTotal={}ms",
                categories.size(), (endTime - startTime));

        return categories;
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categoriesList", "categoriesWithSubcategories", "categoryById" }, allEntries = true)
    public void deleteCategory(Long id) {
        log.info("[CATEGORY_SERVICE] Iniciando eliminación de categoría. ID={}", id);

        long startTime = System.currentTimeMillis();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[CATEGORY_SERVICE] Categoría no encontrada para eliminación. ID={}", id);
                    return new IllegalArgumentException("Category not found");
                });

        log.debug("[CATEGORY_SERVICE] Categoría encontrada para eliminación: id={}, nombre={}, imagen={}",
                category.getId(), category.getName(), category.getImage());

        if (!category.getImage().equals(imageService.getDefaultImage())) {
            log.debug("[CATEGORY_SERVICE] Eliminando imagen asociada: {}", category.getImage());
            imageService.deleteImageNormal(category.getImage());
        }

        categoryRepository.delete(category);

        long endTime = System.currentTimeMillis();
        log.info("[CATEGORY_SERVICE] Categoría eliminada exitosamente. ID={}, tiempoTotal={}ms",
                id, (endTime - startTime));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoriesWithSubcategories")
    public List<CategoryDto> getCategoriesWithSubcategories() {
        log.debug("[CATEGORY_SERVICE] Obteniendo todas las categorías con sus subcategorías");

        long startTime = System.currentTimeMillis();

        List<CategoryDto> categories = categoryRepository.findAllWithSubcategories().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("[CATEGORY_SERVICE] Categorías con subcategorías obtenidas: {}, tiempoTotal={}ms",
                categories.size(), (endTime - startTime));

        return categories;
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categoriesList", "categoriesWithSubcategories", "categoryById" }, key = "#id")
    public CategoryDto updateCategoryImage(Long id, MultipartFile imageFile) {
        log.info("[CATEGORY_SERVICE] Actualizando imagen de categoría. ID={}, archivoImagen={}",
                id, imageFile != null ? imageFile.getOriginalFilename() : "null");

        long startTime = System.currentTimeMillis();

        if (imageFile == null || imageFile.isEmpty()) {
            log.warn("[CATEGORY_SERVICE] Archivo de imagen vacío proporcionado. ID={}", id);
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[CATEGORY_SERVICE] Categoría no encontrada para actualización de imagen. ID={}", id);
                    return new IllegalArgumentException("Category not found");
                });

        log.debug("[CATEGORY_SERVICE] Categoría encontrada: id={}, imagenActual={}",
                category.getId(), category.getImage());

        String oldImage = category.getImage();
        String newImage = imageService.saveImageNormal(imageFile);
        category.setImage(newImage);

        if (!oldImage.equals(imageService.getDefaultImage())) {
            log.debug("[CATEGORY_SERVICE] Eliminando imagen anterior: {}", oldImage);
            imageService.deleteImageNormal(oldImage);
        }

        Category updatedCategory = categoryRepository.save(category);

        long endTime = System.currentTimeMillis();
        log.info("[CATEGORY_SERVICE] Imagen de categoría actualizada. ID={}, imagenNueva={}, tiempoTotal={}ms",
                id, newImage, (endTime - startTime));

        return categoryMapper.toCategoryDto(updatedCategory);
    }
}