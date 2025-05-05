package com.example.kunturtatto.service;

import java.time.LocalDate;
import java.util.List;

import com.example.kunturtatto.exception.InvalidAppointmentTimeException;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request) throws InvalidAppointmentTimeException;
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request)throws InvalidAppointmentTimeException;
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getAllAppointments();
    List<AppointmentResponse> getAppointmentsByDate(LocalDate date);
    List<AppointmentResponse> getTodayAppointments();
    List<AppointmentResponse> getUpcomingAppointments();
    void cancelAppointment(Long id);
    void confirmAppointment(Long id);
    void completeAppointment(Long id);
}
