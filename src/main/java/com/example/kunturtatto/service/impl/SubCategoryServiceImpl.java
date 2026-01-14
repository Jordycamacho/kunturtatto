package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.SubCategoryMapper;
import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import com.example.kunturtatto.repository.CategoryRepository;
import com.example.kunturtatto.repository.SubCategoryRepository;
import com.example.kunturtatto.request.SubCategoryRequest;
import com.example.kunturtatto.service.ImageService;
import com.example.kunturtatto.service.SubCategoryService;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

/**
 * Implementación del servicio para la gestión de subcategorías.
 * Proporciona operaciones CRUD completas para subcategorías.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { "subcategories" })
public class SubCategoryServiceImpl implements SubCategoryService {

    private static final String ENTITY_NAME = "SubCategory";

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final EntityManager entityManager;
    private final ImageService imageService;

    /**
     * Crea una nueva subcategoría.
     * 
     * @param request   Datos de la subcategoría a crear
     * @param imageFile Archivo de imagen (opcional)
     * @return SubCategoryDto creada
     * @throws ResourceNotFoundException Si la categoría padre no existe
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subcategoriesAll", allEntries = true),
            @CacheEvict(value = "subcategoriesByCategory", allEntries = true),
            @CacheEvict(value = "categoriesList", allEntries = true)
    })
    public SubCategoryDto createSubCategory(SubCategoryRequest request, MultipartFile imageFile) {
        log.info(" INICIO: Creación de subcategoría. Nombre: {}, Categoría ID: {}",
                request.getName(), request.getCategoryId());

        try {
            auditLog("CREATE_START", null,
                    String.format("Iniciando creación de subcategoría '%s'", request.getName()));

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        log.error(" ERROR: Categoría no encontrada con ID: {}", request.getCategoryId());
                        return new ResourceNotFoundException("Category", "id", request.getCategoryId());
                    });

            log.debug(" Categoría encontrada: {} (ID: {})", category.getName(), category.getId());

            String imageName = processImageFile(imageFile);
            log.debug(" Imagen procesada: {}", imageName);

            SubCategory subCategory = SubCategory.builder()
                    .name(request.getName())
                    .image(imageName)
                    .category(category)
                    .build();

            SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
            log.info(" EXITO: Subcategoría creada - ID: {}, Nombre: {}, Categoría: {}",
                    savedSubCategory.getId(), savedSubCategory.getName(), category.getName());

            auditLog("CREATE_SUCCESS", savedSubCategory.getId(),
                    String.format("Subcategoría '%s' creada bajo categoría '%s'",
                            savedSubCategory.getName(), category.getName()));

            return subCategoryMapper.toSubCategoryDto(savedSubCategory);

        } catch (Exception e) {
            log.error("ERROR CRÍTICO: Fallo en creación de subcategoría. Error: {}", e.getMessage(), e);
            auditLog("CREATE_ERROR", null,
                    String.format("Error creando subcategoría '%s': %s",
                            request.getName(), e.getMessage()));
            throw e;
        }
    }

    /**
     * Actualiza una subcategoría existente.
     * 
     * @param id        ID de la subcategoría a actualizar
     * @param request   Nuevos datos de la subcategoría
     * @param imageFile Nueva imagen (opcional)
     * @return SubCategoryDto actualizada
     * @throws ResourceNotFoundException Si la subcategoría no existe
     */
    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "subcategoryById", key = "#id")
    }, evict = {
            @CacheEvict(value = "subcategoriesAll", allEntries = true),
            @CacheEvict(value = "subcategoriesByCategory", allEntries = true)
    })
    public SubCategoryDto updateSubCategory(Long id, SubCategoryRequest request, MultipartFile imageFile) {
        log.info("INICIO: Actualización de subcategoría ID: {}", id);

        try {
            auditLog("UPDATE_START", id, "Iniciando actualización de subcategoría");

            SubCategory subCategory = subCategoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("ERROR: Subcategoría no encontrada con ID: {}", id);
                        return new ResourceNotFoundException(ENTITY_NAME, "id", id);
                    });

            log.debug("Subcategoría encontrada: {} (ID: {})", subCategory.getName(), id);

            StringBuilder changeLog = new StringBuilder();

            if (imageFile != null && !imageFile.isEmpty()) {
                String oldImage = subCategory.getImage();
                String newImage = imageService.saveImageNormal(imageFile);
                subCategory.setImage(newImage);

                if (!oldImage.equals(imageService.getDefaultImage())) {
                    imageService.deleteImageNormal(oldImage);
                }

                log.info("Imagen actualizada: {} → {}", oldImage, newImage);
                changeLog.append(String.format("Imagen cambiada de '%s' a '%s'. ", oldImage, newImage));
            }

            if (!subCategory.getCategory().getId().equals(request.getCategoryId())) {
                Category oldCategory = subCategory.getCategory();
                Category newCategory = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> {
                            log.error("ERROR: Nueva categoría no encontrada con ID: {}", request.getCategoryId());
                            return new ResourceNotFoundException("Category", "id", request.getCategoryId());
                        });

                subCategory.setCategory(newCategory);
                log.info("Categoría cambiada: {} → {}",
                        oldCategory.getName(), newCategory.getName());
                changeLog.append(String.format("Categoría cambiada de '%s' a '%s'. ",
                        oldCategory.getName(), newCategory.getName()));
            }

            if (!subCategory.getName().equals(request.getName())) {
                String oldName = subCategory.getName();
                subCategory.setName(request.getName());
                log.info("Nombre actualizado: {} → {}", oldName, request.getName());
                changeLog.append(String.format("Nombre cambiado de '%s' a '%s'. ",
                        oldName, request.getName()));
            }

            SubCategory updatedSubCategory = subCategoryRepository.save(subCategory);
            log.info("EXITO: Subcategoría ID: {} actualizada correctamente", id);

            if (changeLog.length() > 0) {
                auditLog("UPDATE_SUCCESS", id, changeLog.toString());
            }

            return subCategoryMapper.toSubCategoryDto(updatedSubCategory);

        } catch (Exception e) {
            log.error("ERROR CRÍTICO: Fallo en actualización de subcategoría ID: {}. Error: {}",
                    id, e.getMessage(), e);
            auditLog("UPDATE_ERROR", id,
                    String.format("Error actualizando subcategoría: %s", e.getMessage()));
            throw e;
        }
    }

    /**
     * Obtiene una subcategoría por su ID.
     * 
     * @param id ID de la subcategoría
     * @return SubCategoryDto encontrada
     * @throws ResourceNotFoundException Si la subcategoría no existe
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "subcategoryById", key = "#id", unless = "#result == null")
    public SubCategoryDto getSubCategoryById(Long id) {
        log.debug(" INICIO: Consultando subcategoría ID: {}", id);

        try {
            SubCategory subCategory = subCategoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn(" ADVERTENCIA: Subcategoría no encontrada con ID: {}", id);
                        return new ResourceNotFoundException(ENTITY_NAME, "id", id);
                    });

            log.debug(" EXITO: Subcategoría encontrada - ID: {}, Nombre: {}",
                    id, subCategory.getName());

            auditLog("READ_SUCCESS", id,
                    String.format("Consulta de subcategoría '%s'", subCategory.getName()));

            return subCategoryMapper.toSubCategoryDto(subCategory);

        } catch (Exception e) {
            log.error(" ERROR: Fallo al consultar subcategoría ID: {}. Error: {}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Obtiene todas las subcategorías.
     * 
     * @return Lista de todas las SubCategoryDto
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "subcategoriesAll", key = "'all'", unless = "#result.isEmpty()")
    public List<SubCategoryDto> getAllSubCategories() {
        log.debug(" INICIO: Consultando todas las subcategorías");

        try {
            List<SubCategoryDto> subCategories = subCategoryRepository.findAll().stream()
                    .map(subCategoryMapper::toSubCategoryDto)
                    .collect(Collectors.toList());

            log.info(" EXITO: Se encontraron {} subcategorías", subCategories.size());

            auditLog("LIST_ALL", null,
                    String.format("Listado de %d subcategorías", subCategories.size()));

            return subCategories;

        } catch (Exception e) {
            log.error(" ERROR: Fallo al obtener todas las subcategorías. Error: {}",
                    e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Obtiene subcategorías por ID de categoría.
     * 
     * @param categoryId ID de la categoría padre
     * @return Lista de SubCategoryDto de la categoría especificada
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "subcategoriesByCategory", key = "#categoryId", condition = "#categoryId != null", unless = "#result == null or #result.isEmpty()")
    public List<SubCategoryDto> getSubCategoriesByCategory(Long categoryId) {
        log.debug(" INICIO: Consultando subcategorías para categoría ID: {}", categoryId);

        try {
            if (!categoryRepository.existsById(categoryId)) {
                log.warn(" ADVERTENCIA: Categoría ID: {} no encontrada", categoryId);
                throw new ResourceNotFoundException("Category", "id", categoryId);
            }

            List<SubCategoryDto> subCategories = subCategoryRepository.findByCategoryId(categoryId)
                    .stream()
                    .map(subCategoryMapper::toSubCategoryDto)
                    .collect(Collectors.toList());

            log.debug(" EXITO: Se encontraron {} subcategorías para categoría ID: {}",
                    subCategories.size(), categoryId);

            auditLog("LIST_BY_CATEGORY", null,
                    String.format("%d subcategorías para categoría ID: %d",
                            subCategories.size(), categoryId));

            return subCategories;

        } catch (Exception e) {
            log.error(" ERROR: Fallo al obtener subcategorías para categoría ID: {}. Error: {}",
                    categoryId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Elimina una subcategoría por su ID.
     * 
     * @param id ID de la subcategoría a eliminar
     * @throws ResourceNotFoundException Si la subcategoría no existe
     * @throws IllegalStateException     Si la subcategoría tiene diseños asociados
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subcategoryById", key = "#id"),
            @CacheEvict(value = "subcategoriesAll", allEntries = true),
            @CacheEvict(value = "subcategoriesByCategory", allEntries = true),
            @CacheEvict(value = "categoriesList", allEntries = true)
    })
    public void deleteSubCategory(Long id) {
        log.info("INICIO: Eliminación de subcategoría ID: {}", id);

        try {
            auditLog("DELETE_START", id, "Iniciando eliminación de subcategoría");

            SubCategory subCategory = subCategoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("ERROR: Subcategoría no encontrada con ID: {}", id);
                        return new ResourceNotFoundException(ENTITY_NAME, "id", id);
                    });

            Hibernate.initialize(subCategory.getDesigns());

            log.info("Subcategoría encontrada: '{}' con {} diseños asociados",
                    subCategory.getName(), subCategory.getDesigns().size());

            if (!subCategory.getDesigns().isEmpty()) {
                log.warn(
                        "ADVERTENCIA: No se puede eliminar subcategoría con diseños asociados. Cantidad: {}, Subcategoría: {}",
                        subCategory.getDesigns().size(), subCategory.getName());
                auditLog("DELETE_REJECTED", id,
                        String.format("Eliminación rechazada - Tiene %d diseños asociados",
                                subCategory.getDesigns().size()));
                throw new IllegalStateException(
                        "No se puede eliminar la subcategoría '" + subCategory.getName() +
                                "' porque tiene " + subCategory.getDesigns().size() + " diseños asociados");
            }

            if (!subCategory.getImage().equals(imageService.getDefaultImage())) {
                log.info("Eliminando imagen asociada: {}", subCategory.getImage());
                try {
                    imageService.deleteImageNormal(subCategory.getImage());
                    log.debug("Imagen eliminada exitosamente: {}", subCategory.getImage());
                } catch (Exception e) {
                    log.warn("No se pudo eliminar la imagen {}, continuando con eliminación de subcategoría. Error: {}",
                            subCategory.getImage(), e.getMessage());
                }
            }

            int deletedCount = subCategoryRepository.deleteNativeById(id);

            if (deletedCount == 0) {
                log.error("ERROR: No se eliminó ninguna fila al ejecutar deleteNativeById para ID: {}", id);
                throw new RuntimeException("No se pudo eliminar la subcategoría");
            }

            entityManager.flush();
            entityManager.clear();

            log.info("EXITO: Subcategoría ID: {} eliminada correctamente. Filas afectadas: {}", id, deletedCount);

            boolean stillExists = subCategoryRepository.existsById(id);
            if (stillExists) {
                log.error("CRITICAL ERROR: Subcategoría con ID: {} aún existe después de eliminación nativa!", id);
                throw new RuntimeException("La subcategoría no se eliminó completamente del sistema");
            }

            auditLog("DELETE_SUCCESS", id,
                    String.format("Subcategoría '%s' eliminada", subCategory.getName()));

        } catch (Exception e) {
            log.error("ERROR CRITICO: Fallo en eliminación de subcategoría ID: {}. Error: {}",
                    id, e.getMessage(), e);
            auditLog("DELETE_ERROR", id,
                    String.format("Error eliminando subcategoría: %s", e.getMessage()));
            throw e;
        }
    }

    /**
     * Método alternativo de eliminación para debugging
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subcategoryById", key = "#id"),
            @CacheEvict(value = "subcategoriesAll", allEntries = true),
            @CacheEvict(value = "subcategoriesByCategory", allEntries = true),
            @CacheEvict(value = "categoriesList", allEntries = true)
    })
    public void deleteSubCategoryWithCascade(Long id) {
        log.info("ELIMINACION CASCADE: Subcategoría ID: {}", id);

        try {
            SubCategory subCategory = subCategoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "id", id));

            log.info("Eliminando subcategoría: {} (ID: {})", subCategory.getName(), id);

            if (!subCategory.getDesigns().isEmpty()) {
                throw new IllegalStateException(
                        "No se puede eliminar la subcategoría porque tiene " +
                                subCategory.getDesigns().size() + " diseños asociados");
            }

            if (!subCategory.getImage().equals(imageService.getDefaultImage())) {
                log.info("Eliminando imagen: {}", subCategory.getImage());
                try {
                    imageService.deleteImageNormal(subCategory.getImage());
                } catch (Exception e) {
                    log.warn("Error eliminando imagen: {}", e.getMessage());
                }
            }

            subCategory.setCategory(null);

            subCategoryRepository.deleteById(id);

            subCategoryRepository.flush();

            boolean exists = subCategoryRepository.existsById(id);
            if (exists) {
                log.error("La subcategoría sigue existiendo después de deleteById!");

                int nativeDeleted = subCategoryRepository.deleteNativeById(id);
                log.info("Resultado eliminación nativa: {} filas", nativeDeleted);

                if (nativeDeleted == 0) {
                    throw new RuntimeException("No se pudo eliminar la subcategoría con ningún método");
                }
            }

            log.info("ELIMINACION CASCADE EXITOSA: Subcategoría ID: {} eliminada", id);

        } catch (Exception e) {
            log.error("ERROR en eliminación cascade: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Actualiza solo la imagen de una subcategoría.
     * 
     * @param id        ID de la subcategoría
     * @param imageFile Nuevo archivo de imagen
     * @return SubCategoryDto actualizada
     * @throws ResourceNotFoundException Si la subcategoría no existe
     * @throws IllegalArgumentException  Si el archivo de imagen está vacío
     */
    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "subcategoryById", key = "#id")
    }, evict = {
            @CacheEvict(value = "subcategoriesAll", allEntries = true),
            @CacheEvict(value = "subcategoriesByCategory", allEntries = true)
    })
    public SubCategoryDto updateSubCategoryImage(Long id, MultipartFile imageFile) {
        log.info("INICIO: Actualización de imagen para subcategoría ID: {}", id);

        try {
            auditLog("UPDATE_IMAGE_START", id, "Iniciando actualización de imagen");

            if (imageFile == null || imageFile.isEmpty()) {
                log.warn(" ADVERTENCIA: Archivo de imagen vacío para subcategoría ID: {}", id);
                throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
            }

            SubCategory subCategory = subCategoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error(" ERROR: Subcategoría no encontrada con ID: {}", id);
                        return new ResourceNotFoundException(ENTITY_NAME, "id", id);
                    });

            log.debug(" Subcategoría encontrada: {} (ID: {})", subCategory.getName(), id);

            String oldImage = subCategory.getImage();
            String newImage = imageService.saveImageNormal(imageFile);

            subCategory.setImage(newImage);
            log.info(" Imagen actualizada: {} → {}", oldImage, newImage);

            if (!oldImage.equals(imageService.getDefaultImage())) {
                imageService.deleteImageNormal(oldImage);
                log.debug("Imagen anterior eliminada: {}", oldImage);
            }

            SubCategory updatedSubCategory = subCategoryRepository.save(subCategory);
            log.info(" EXITO: Imagen actualizada para subcategoría ID: {}", id);

            auditLog("UPDATE_IMAGE_SUCCESS", id,
                    String.format("Imagen actualizada de '%s' a '%s'", oldImage, newImage));

            return subCategoryMapper.toSubCategoryDto(updatedSubCategory);

        } catch (Exception e) {
            log.error(" ERROR CRÍTICO: Fallo al actualizar imagen de subcategoría ID: {}. Error: {}",
                    id, e.getMessage(), e);
            auditLog("UPDATE_IMAGE_ERROR", id,
                    String.format("Error actualizando imagen: %s", e.getMessage()));
            throw e;
        }
    }

    /**
     * Procesa el archivo de imagen.
     * 
     * @param imageFile Archivo de imagen a procesar
     * @return Nombre del archivo procesado o imagen por defecto
     */
    private String processImageFile(MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            log.debug("Procesando imagen: {} ({} bytes)",
                    fileName, imageFile.getSize());
            return imageService.saveImageNormal(imageFile);
        }
        String defaultImage = imageService.getDefaultImage();
        log.debug("Usando imagen por defecto: {}", defaultImage);
        return defaultImage;
    }

    /**
     * Registra un evento de auditoría.
     * 
     * @param action   Acción realizada (CREATE, UPDATE, DELETE, etc.)
     * @param entityId ID de la entidad (null si no aplica)
     * @param details  Detalles de la acción
     */
    private void auditLog(String action, Long entityId, String details) {
        String timestamp = LocalDateTime.now().toString();
        String logMessage = String.format("[AUDITORÍA] %s | Entidad: %s | ID: %s | Detalles: %s | Timestamp: %s",
                action, ENTITY_NAME, entityId != null ? entityId.toString() : "N/A",
                details, timestamp);

        log.info(logMessage);

    }
}