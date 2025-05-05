package com.example.kunturtatto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "design", ignore = true)
    Appointment toEntity(AppointmentRequest request);
    
    @Mapping(target = "designTitle", source = "design.title")
    AppointmentResponse toResponse(Appointment appointment);
    
    default Appointment updateFromRequest(AppointmentRequest request, Appointment appointment) {
        appointment.setCustomerName(request.getCustomerName());
        appointment.setCustomerEmail(request.getCustomerEmail());
        appointment.setCustomerPhone(request.getCustomerPhone());
        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setPrice(request.getPrice());
        appointment.setTattooSize(request.getTattooSize());
        appointment.setBodyPart(request.getBodyPart());
        appointment.setReferenceLinks(request.getReferenceLinks());
        appointment.setCustomDescription(request.getCustomDescription());
        return appointment;
    }
}