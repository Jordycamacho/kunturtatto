package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.TattooConsultationDto;
import com.example.kunturtatto.request.TattooConsultationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TattooConsultationService {
    TattooConsultationDto createConsultation(TattooConsultationRequest request);
    TattooConsultationDto getConsultationById(Long id);
    Page<TattooConsultationDto> getAllConsultations(Pageable pageable);
    Page<TattooConsultationDto> getUnreadConsultations(Pageable pageable);
    Page<TattooConsultationDto> searchConsultations(String searchTerm, Pageable pageable);
    TattooConsultationDto markAsRead(Long id);
    TattooConsultationDto markAsUnread(Long id);
    void deleteConsultation(Long id);
    long countUnread();
    long countTodayConsultations();
    long countTotalConsultations();
    List<TattooConsultationDto> getRecentConsultations(int limit);
}