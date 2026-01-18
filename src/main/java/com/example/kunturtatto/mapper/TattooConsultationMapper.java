package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.TattooConsultationDto;
import com.example.kunturtatto.model.TattooConsultation;
import com.example.kunturtatto.request.TattooConsultationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TattooConsultationMapper {
    TattooConsultationMapper INSTANCE = Mappers.getMapper(TattooConsultationMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "leido", constant = "false")
    TattooConsultation toEntity(TattooConsultationRequest request);
    
    TattooConsultationDto toDto(TattooConsultation tattooConsultation);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "leido", source = "leido")
    TattooConsultation toEntity(TattooConsultationDto dto);
}