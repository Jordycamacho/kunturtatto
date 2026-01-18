package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.TattooConsultationDto;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.TattooConsultationMapper;
import com.example.kunturtatto.model.TattooConsultation;
import com.example.kunturtatto.repository.TattooConsultationRepository;
import com.example.kunturtatto.request.TattooConsultationRequest;
import com.example.kunturtatto.service.TattooConsultationService;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TattooConsultationServiceImpl implements TattooConsultationService {

    private final TattooConsultationRepository consultationRepository;
    private final TattooConsultationMapper consultationMapper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "consultationsAll", allEntries = true),
            @CacheEvict(value = "consultationsUnread", allEntries = true),
            @CacheEvict(value = "consultationsStats", allEntries = true),
            @CacheEvict(value = "consultationsRecent", allEntries = true)
    })
    public TattooConsultationDto createConsultation(TattooConsultationRequest request) {
        log.info("📝 [CONSULTATION_SERVICE] Creando nueva consulta para: {}", request.getEmail());

        try {
            // Validar campos obligatorios
            validateConsultationRequest(request);

            TattooConsultation consultation = consultationMapper.toEntity(request);
            consultation.setFechaCreacion(LocalDateTime.now());
            consultation.setLeido(false);

            // Limpiar y validar datos antes de guardar
            consultation.setNombre(cleanString(consultation.getNombre(), 100));
            consultation.setEmail(cleanString(consultation.getEmail(), 100).toLowerCase());
            consultation.setAbiertoSugerencias(cleanString(consultation.getAbiertoSugerencias(), 20));

            TattooConsultation savedConsultation = consultationRepository.save(consultation);

            log.info("✅ [CONSULTATION_SERVICE] Consulta creada ID: {} - Cliente: {}",
                    savedConsultation.getId(), savedConsultation.getNombre());

            auditLog("CONSULTATION_CREATED", savedConsultation.getId(),
                    String.format("Consulta creada por %s (%s)",
                            savedConsultation.getNombre(), savedConsultation.getEmail()));

            return consultationMapper.toDto(savedConsultation);

        } catch (ConstraintViolationException e) {
            log.error("❌ [CONSULTATION_SERVICE] Error de validación de datos: {}", e.getMessage());
            throw new IllegalArgumentException("Error en los datos enviados: " +
                    e.getConstraintViolations().stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .collect(Collectors.joining(", ")));

        } catch (Exception e) {
            log.error("❌ [CONSULTATION_SERVICE] Error al crear consulta: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Método para validar el request
    private void validateConsultationRequest(TattooConsultationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La consulta no puede estar vacía");
        }

        List<String> errores = new ArrayList<>();

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            errores.add("El nombre es obligatorio");
        } else if (request.getNombre().length() > 100) {
            errores.add("El nombre no puede exceder 100 caracteres");
        }

        if (request.getAbiertoSugerencias() == null || request.getAbiertoSugerencias().trim().isEmpty()) {
            errores.add("Debe indicar si está abierto a sugerencias");
        } else if (request.getAbiertoSugerencias().length() > 20) {
            errores.add("El campo 'abierto a sugerencias' no puede exceder 20 caracteres");
        }

        // Validar otros campos obligatorios...
        if (request.getParteCuerpo() == null || request.getParteCuerpo().trim().isEmpty()) {
            errores.add("La parte del cuerpo es obligatoria");
        }

        if (request.getTamano() == null || request.getTamano().trim().isEmpty()) {
            errores.add("El tamaño es obligatorio");
        }

        if (request.getTipoTatuaje() == null || request.getTipoTatuaje().trim().isEmpty()) {
            errores.add("El tipo de tatuaje es obligatorio");
        }

        if (request.getExpresion() == null || request.getExpresion().trim().isEmpty()) {
            errores.add("La expresión es obligatoria");
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errores));
        }
    }

    // Método para limpiar strings
    private String cleanString(String input, int maxLength) {
        if (input == null)
            return "";
        String cleaned = input.trim();
        if (cleaned.length() > maxLength) {
            return cleaned.substring(0, maxLength);
        }
        return cleaned;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationById", key = "#id", unless = "#result == null")
    public TattooConsultationDto getConsultationById(Long id) {
        log.debug("🔍 [CONSULTATION_SERVICE] Buscando consulta ID: {}", id);

        TattooConsultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("⚠️ [CONSULTATION_SERVICE] Consulta no encontrada ID: {}", id);
                    return new ResourceNotFoundException("Consulta de tatuaje", "id", id);
                });

        log.debug("✅ [CONSULTATION_SERVICE] Consulta encontrada ID: {} - Cliente: {}",
                id, consultation.getNombre());

        return consultationMapper.toDto(consultation);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationsAll", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort", unless = "#result == null or #result.isEmpty()")
    public Page<TattooConsultationDto> getAllConsultations(Pageable pageable) {
        log.debug("📋 [CONSULTATION_SERVICE] Obteniendo todas las consultas - Página: {}", pageable.getPageNumber());

        Page<TattooConsultationDto> consultations = consultationRepository.findAll(pageable)
                .map(consultationMapper::toDto);

        log.info("📊 [CONSULTATION_SERVICE] Total consultas: {} - Páginas: {}",
                consultations.getTotalElements(), consultations.getTotalPages());

        return consultations;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationsUnread", key = "#pageable.pageNumber + '-' + #pageable.pageSize", unless = "#result == null or #result.isEmpty()")
    public Page<TattooConsultationDto> getUnreadConsultations(Pageable pageable) {
        log.debug("📨 [CONSULTATION_SERVICE] Obteniendo consultas no leídas");

        Page<TattooConsultationDto> consultations = consultationRepository.findByLeidoFalse(pageable)
                .map(consultationMapper::toDto);

        log.info("📬 [CONSULTATION_SERVICE] Consultas no leídas: {}", consultations.getTotalElements());

        return consultations;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TattooConsultationDto> searchConsultations(String searchTerm, Pageable pageable) {
        log.debug("🔎 [CONSULTATION_SERVICE] Buscando consultas con término: {}", searchTerm);

        Page<TattooConsultationDto> consultations = consultationRepository
                .buscarPorNombreOEmail(searchTerm, pageable)
                .map(consultationMapper::toDto);

        log.info("📈 [CONSULTATION_SERVICE] Resultados de búsqueda: {}", consultations.getTotalElements());

        return consultations;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "consultationById", key = "#id"),
            @CacheEvict(value = "consultationsAll", allEntries = true),
            @CacheEvict(value = "consultationsUnread", allEntries = true),
            @CacheEvict(value = "consultationsStats", allEntries = true)
    })
    public TattooConsultationDto markAsRead(Long id) {
        log.info("👁️ [CONSULTATION_SERVICE] Marcando consulta como leída ID: {}", id);

        TattooConsultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta de tatuaje", "id", id));

        consultation.setLeido(true);
        TattooConsultation updated = consultationRepository.save(consultation);

        auditLog("CONSULTATION_MARKED_READ", id,
                String.format("Consulta marcada como leída - Cliente: %s", consultation.getNombre()));

        return consultationMapper.toDto(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "consultationById", key = "#id"),
            @CacheEvict(value = "consultationsAll", allEntries = true),
            @CacheEvict(value = "consultationsUnread", allEntries = true),
            @CacheEvict(value = "consultationsStats", allEntries = true)
    })
    public TattooConsultationDto markAsUnread(Long id) {
        log.info("👁️ [CONSULTATION_SERVICE] Marcando consulta como no leída ID: {}", id);

        TattooConsultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta de tatuaje", "id", id));

        consultation.setLeido(false);
        TattooConsultation updated = consultationRepository.save(consultation);

        auditLog("CONSULTATION_MARKED_UNREAD", id,
                String.format("Consulta marcada como no leída - Cliente: %s", consultation.getNombre()));

        return consultationMapper.toDto(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "consultationById", key = "#id"),
            @CacheEvict(value = "consultationsAll", allEntries = true),
            @CacheEvict(value = "consultationsUnread", allEntries = true),
            @CacheEvict(value = "consultationsStats", allEntries = true),
            @CacheEvict(value = "consultationsRecent", allEntries = true)
    })
    public void deleteConsultation(Long id) {
        log.info("🗑️ [CONSULTATION_SERVICE] Eliminando consulta ID: {}", id);

        TattooConsultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta de tatuaje", "id", id));

        consultationRepository.deleteById(id);

        auditLog("CONSULTATION_DELETED", id,
                String.format("Consulta eliminada - Cliente: %s (%s)",
                        consultation.getNombre(), consultation.getEmail()));

        log.info("✅ [CONSULTATION_SERVICE] Consulta eliminada ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationsStats", key = "'unreadCount'")
    public long countUnread() {
        log.debug("📊 [CONSULTATION_SERVICE] Contando consultas no leídas");
        return consultationRepository.findByLeidoFalse().size();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationsStats", key = "'todayCount'")
    public long countTodayConsultations() {
        log.debug("📊 [CONSULTATION_SERVICE] Contando consultas de hoy");

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return consultationRepository.findByFechaCreacionBetween(startOfDay, endOfDay).size();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationsStats", key = "'totalCount'")
    public long countTotalConsultations() {
        log.debug("📊 [CONSULTATION_SERVICE] Contando total de consultas");
        return consultationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consultationsRecent", key = "#limit")
    public List<TattooConsultationDto> getRecentConsultations(int limit) {
        log.debug("🔄 [CONSULTATION_SERVICE] Obteniendo {} consultas recientes", limit);

        return consultationRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c2.getFechaCreacion().compareTo(c1.getFechaCreacion()))
                .limit(limit)
                .map(consultationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Registro de auditoría
     */
    private void auditLog(String action, Long entityId, String details) {
        String timestamp = LocalDateTime.now().toString();
        String logMessage = String.format("[AUDITORÍA_CONSULTAS] %s | ID: %s | Detalles: %s | Timestamp: %s",
                action, entityId != null ? entityId.toString() : "N/A", details, timestamp);

        log.info(logMessage);
    }
}