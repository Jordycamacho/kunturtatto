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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @CacheEvict(value = { "designsAll", "designsBySubCategory", "designsByCategory",
            "searchDesigns" }, allEntries = true)
    public DesignDto createDesign(DesignRequest request, MultipartFile imageFile) {
        log.info(
                "[DESIGN_SERVICE] Iniciando creación de diseño. Datos recibidos: título={}, subcategoríaId={}, archivoImagen={}",
                request.getTitle(), request.getSubCategoryId(),
                imageFile != null ? imageFile.getOriginalFilename() : "null");

        long startTime = System.currentTimeMillis();

        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> {
                    log.error("[DESIGN_SERVICE] Subcategoría no encontrada. ID={}", request.getSubCategoryId());
                    return new ResourceNotFoundException("SubCategory not found with ID: " + request.getSubCategoryId(),
                            null, null);
                });

        log.debug("[DESIGN_SERVICE] Subcategoría encontrada: id={}, nombre={}, categoría={}",
                subCategory.getId(), subCategory.getName(),
                subCategory.getCategory() != null ? subCategory.getCategory().getName() : "null");

        String imageName = imageFile != null && !imageFile.isEmpty() ? imageService.saveImageNormal(imageFile)
                : imageService.getDefaultImage();

        log.debug("[DESIGN_SERVICE] Nombre de imagen generado: {}", imageName);

        Design design = Design.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .image(imageName)
                .subCategory(subCategory)
                .build();

        log.debug("[DESIGN_SERVICE] Entidad Design construida: id=null, título={}, subcategoríaId={}, imagen={}",
                request.getTitle(), subCategory.getId(), imageName);

        Design savedDesign = designRepository.save(design);

        long endTime = System.currentTimeMillis();
        log.info("[DESIGN_SERVICE] Diseño creado exitosamente. ID={}, título={}, subcategoría={}, tiempoTotal={}ms",
                savedDesign.getId(), savedDesign.getTitle(), subCategory.getName(), (endTime - startTime));

        return designMapper.toDesignDto(savedDesign);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "designsAll", "designById", "designsBySubCategory", "designsByCategory",
            "searchDesigns" }, allEntries = true)
    public DesignDto updateDesign(Long id, DesignRequest request, MultipartFile imageFile) {
        log.info(
                "[DESIGN_SERVICE] Iniciando actualización de diseño. ID={}, datosRecibidos: título={}, subcategoríaId={}, archivoImagen={}",
                id, request.getTitle(), request.getSubCategoryId(),
                imageFile != null && !imageFile.isEmpty() ? "presente" : "ausente");

        long startTime = System.currentTimeMillis();

        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[DESIGN_SERVICE] Diseño no encontrado para actualización. ID={}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id, null, id);
                });

        log.debug(
                "[DESIGN_SERVICE] Diseño encontrado: id={}, títuloActual={}, subcategoríaActualId={}, imagenActual={}",
                design.getId(), design.getTitle(),
                design.getSubCategory() != null ? design.getSubCategory().getId() : "null",
                design.getImage());

        if (imageFile != null && !imageFile.isEmpty()) {
            log.debug("[DESIGN_SERVICE] Actualizando imagen de diseño. ID={}", id);
            String oldImage = design.getImage();
            String newImage = imageService.saveImageNormal(imageFile);
            design.setImage(newImage);
            imageService.deleteImageNormal(oldImage);

            log.debug("[DESIGN_SERVICE] Imagen actualizada. ImagenAnterior={}, imagenNueva={}", oldImage, newImage);
        }

        if (!design.getSubCategory().getId().equals(request.getSubCategoryId())) {
            log.debug(
                    "[DESIGN_SERVICE] Cambiando subcategoría de diseño. ID={}, subcategoríaAnteriorId={}, subcategoríaNuevaId={}",
                    id, design.getSubCategory().getId(), request.getSubCategoryId());

            SubCategory newSubCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> {
                        log.error("[DESIGN_SERVICE] Subcategoría no encontrada. ID={}", request.getSubCategoryId());
                        return new ResourceNotFoundException(
                                "SubCategory not found with ID: " + request.getSubCategoryId(), null, id);
                    });
            design.setSubCategory(newSubCategory);
        }

        design.setTitle(request.getTitle());
        design.setDescription(request.getDescription());

        Design updatedDesign = designRepository.save(design);

        long endTime = System.currentTimeMillis();
        log.info("[DESIGN_SERVICE] Diseño actualizado exitosamente. ID={}, títuloNuevo={}, tiempoTotal={}ms",
                id, updatedDesign.getTitle(), (endTime - startTime));

        return designMapper.toDesignDto(updatedDesign);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "designById", key = "#id")
    public DesignDto getDesignById(Long id) {
        log.debug("[DESIGN_SERVICE] Obteniendo diseño por ID. ID={}", id);

        long startTime = System.currentTimeMillis();

        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[DESIGN_SERVICE] Diseño no encontrado. ID={}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id, null, id);
                });

        long endTime = System.currentTimeMillis();
        log.debug("[DESIGN_SERVICE] Diseño obtenido. ID={}, título={}, subcategoría={}, tiempoConsulta={}ms",
                id, design.getTitle(),
                design.getSubCategory() != null ? design.getSubCategory().getName() : "null",
                (endTime - startTime));

        return designMapper.toDesignDto(design);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "designsAll")
    public List<DesignDto> getAllDesigns() {
        log.debug("[DESIGN_SERVICE] Obteniendo todos los diseños");

        long startTime = System.currentTimeMillis();

        List<DesignDto> designs = designRepository.findAllWithRelations().stream()
                .map(designMapper::toDesignDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("[DESIGN_SERVICE] Total de diseños obtenidos: {}, tiempoTotal={}ms",
                designs.size(), (endTime - startTime));

        return designs;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DesignDto> getAllDesignspPageable(Pageable pageable) {
        log.debug("[DESIGN_SERVICE] Obteniendo diseños paginados. Pageable={}", pageable);

        long startTime = System.currentTimeMillis();

        Page<DesignDto> designsPage = designRepository.findAllWithRelationsPageable(pageable)
                .map(designMapper::toDesignDto);

        long endTime = System.currentTimeMillis();
        log.debug(
                "[DESIGN_SERVICE] Página de diseños obtenida: número={}, tamaño={}, totalElementos={}, tiempoConsulta={}ms",
                designsPage.getNumber(), designsPage.getSize(), designsPage.getTotalElements(),
                (endTime - startTime));

        return designsPage;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "designsBySubCategory", key = "#subCategoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<DesignDto> getDesignsBySubCategory(Long subCategoryId, Pageable pageable) {
        log.debug("[DESIGN_SERVICE] Obteniendo diseños por subcategoría. SubcategoríaID={}, Pageable={}",
                subCategoryId, pageable);

        long startTime = System.currentTimeMillis();

        Page<DesignDto> designsPage = designRepository.findBySubCategoryId(subCategoryId, pageable)
                .map(designMapper::toDesignDto);

        long endTime = System.currentTimeMillis();
        log.debug(
                "[DESIGN_SERVICE] Diseños por subcategoría obtenidos: subcategoríaId={}, totalElementos={}, tiempoConsulta={}ms",
                subCategoryId, designsPage.getTotalElements(), (endTime - startTime));

        return designsPage;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "designsByCategory", key = "#categoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<DesignDto> getDesignsByCategory(Long categoryId, Pageable pageable) {
        log.debug("[DESIGN_SERVICE] Obteniendo diseños por categoría. CategoríaID={}, Pageable={}",
                categoryId, pageable);

        long startTime = System.currentTimeMillis();

        Page<DesignDto> designsPage = designRepository.findBySubCategoryCategoryId(categoryId, pageable)
                .map(designMapper::toDesignDto);

        long endTime = System.currentTimeMillis();
        log.debug(
                "[DESIGN_SERVICE] Diseños por categoría obtenidos: categoríaId={}, totalElementos={}, tiempoConsulta={}ms",
                categoryId, designsPage.getTotalElements(), (endTime - startTime));

        return designsPage;
    }

    @Override
    @Transactional
    @CacheEvict(value = { "designsAll", "designById", "designsBySubCategory", "designsByCategory",
            "searchDesigns" }, allEntries = true)
    public void deleteDesign(Long id) {
        log.info("[DESIGN_SERVICE] Iniciando eliminación de diseño. ID={}", id);

        long startTime = System.currentTimeMillis();

        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[DESIGN_SERVICE] Diseño no encontrado para eliminación. ID={}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id, null, id);
                });

        log.debug("[DESIGN_SERVICE] Diseño encontrado para eliminación: id={}, título={}, imagen={}",
                design.getId(), design.getTitle(), design.getImage());

        if (!design.getImage().equals(imageService.getDefaultImage())) {
            log.debug("[DESIGN_SERVICE] Eliminando imagen asociada: {}", design.getImage());
            imageService.deleteImageNormal(design.getImage());
        }

        designRepository.delete(design);

        long endTime = System.currentTimeMillis();
        log.info("[DESIGN_SERVICE] Diseño eliminado exitosamente. ID={}, tiempoTotal={}ms",
                id, (endTime - startTime));
    }

    @Override
    @Transactional
    @CacheEvict(value = { "designsAll", "designById", "designsBySubCategory", "designsByCategory",
            "searchDesigns" }, allEntries = true)
    public DesignDto updateDesignImage(Long id, MultipartFile imageFile) {
        log.info("[DESIGN_SERVICE] Actualizando imagen de diseño. ID={}, archivoImagen={}",
                id, imageFile != null ? imageFile.getOriginalFilename() : "null");

        long startTime = System.currentTimeMillis();

        if (imageFile == null || imageFile.isEmpty()) {
            log.warn("[DESIGN_SERVICE] Archivo de imagen vacío proporcionado. ID={}", id);
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Design design = designRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[DESIGN_SERVICE] Diseño no encontrado para actualización de imagen. ID={}", id);
                    return new ResourceNotFoundException("Design not found with ID: " + id, null, id);
                });

        log.debug("[DESIGN_SERVICE] Diseño encontrado: id={}, imagenActual={}",
                design.getId(), design.getImage());

        String oldImage = design.getImage();
        String newImage = imageService.saveImageNormal(imageFile);
        design.setImage(newImage);

        if (!oldImage.equals(imageService.getDefaultImage())) {
            log.debug("[DESIGN_SERVICE] Eliminando imagen anterior: {}", oldImage);
            imageService.deleteImageNormal(oldImage);
        }

        Design updatedDesign = designRepository.save(design);

        long endTime = System.currentTimeMillis();
        log.info("[DESIGN_SERVICE] Imagen de diseño actualizada. ID={}, imagenNueva={}, tiempoTotal={}ms",
                id, newImage, (endTime - startTime));

        return designMapper.toDesignDto(updatedDesign);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "searchDesigns", key = "#query")
    public List<DesignDto> searchDesigns(String query) {
        log.debug("[DESIGN_SERVICE] Buscando diseños con consulta: {}", query);

        long startTime = System.currentTimeMillis();

        List<DesignDto> designs = designRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query).stream()
                .map(designMapper::toDesignDto)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("[DESIGN_SERVICE] Búsqueda completada. Consulta={}, resultados={}, tiempoTotal={}ms",
                query, designs.size(), (endTime - startTime));

        return designs;
    }
}