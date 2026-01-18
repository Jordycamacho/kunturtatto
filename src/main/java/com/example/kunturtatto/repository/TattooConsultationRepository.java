package com.example.kunturtatto.repository;

import com.example.kunturtatto.model.TattooConsultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TattooConsultationRepository extends JpaRepository<TattooConsultation, Long> {

    // Consultas básicas
    Page<TattooConsultation> findAll(Pageable pageable);
    
    List<TattooConsultation> findByLeidoFalse();
    
    Page<TattooConsultation> findByLeidoFalse(Pageable pageable);
    
    Page<TattooConsultation> findByLeidoTrue(Pageable pageable);
    
    // Consultas por fechas
    List<TattooConsultation> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Consultas por email
    List<TattooConsultation> findByEmail(String email);
    
    // Consulta nativa para estadísticas
    @Query(value = "SELECT COUNT(*) FROM consulta_tatuaje WHERE fecha_creacion >= :fecha", nativeQuery = true)
    long countDesdeFecha(@Param("fecha") LocalDateTime fecha);
    
    // Consulta para obtener no leídos con paginación
    @Query("SELECT t FROM TattooConsultation t WHERE t.leido = false ORDER BY t.fechaCreacion DESC")
    Page<TattooConsultation> findNoLeidosPaginados(Pageable pageable);
    
    // Consulta para buscar por nombre o email
    @Query("SELECT t FROM TattooConsultation t WHERE LOWER(t.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(t.email) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<TattooConsultation> buscarPorNombreOEmail(@Param("busqueda") String busqueda, Pageable pageable);
}