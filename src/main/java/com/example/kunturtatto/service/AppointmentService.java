package com.example.kunturtatto.service;

import java.time.LocalDate;
import java.util.List;

import com.example.kunturtatto.exception.InvalidAppointmentTimeException;
import com.example.kunturtatto.model.enums.AppointmentStatus;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request) throws InvalidAppointmentTimeException;
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request)throws InvalidAppointmentTimeException;
    AppointmentResponse changeAppointmentStatus(Long id, AppointmentStatus newStatus);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getAppointmentsByDate(LocalDate date);
    List<AppointmentResponse> getUpcomingAppointments();
    List<AppointmentResponse> getTodayAppointments();
    List<AppointmentResponse> getAllAppointments();
    void deleteAppointment(Long id);
    void cancelAppointment(Long id);
    void confirmAppointment(Long id);
    void completeAppointment(Long id);
}
