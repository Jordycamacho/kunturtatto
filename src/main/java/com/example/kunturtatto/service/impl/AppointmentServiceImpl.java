package com.example.kunturtatto.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.mapper.AppointmentMapper;
import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.exception.*;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.enums.AppointmentStatus;
import com.example.kunturtatto.repository.AppointmentRepository;
import com.example.kunturtatto.repository.DesignRepository;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;
import com.example.kunturtatto.service.AppointmentService;
import com.example.kunturtatto.service.IContactService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DesignRepository designRepository;
    private final AppointmentMapper appointmentMapper;
    private final IContactService contactService;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) throws InvalidAppointmentTimeException {
        log.info("Creating new appointment for customer: {}", request.getCustomerEmail());

        validateAppointmentTime(request.getDate(), request.getTime());

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setStatus(AppointmentStatus.PENDING);

        if (request.getDesignId() != null) {
            Design design = designRepository.findById(request.getDesignId())
                    .orElseThrow(() -> {
                        log.error("Design not found with ID: {}", request.getDesignId());
                        return new ResourceNotFoundException("Design not found");
                    });
            appointment.setDesign(design);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.debug("Appointment created with ID: {}", savedAppointment.getId());

        contactService.sendAppointmentConfirmation(savedAppointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request)
            throws InvalidAppointmentTimeException {
        log.info("Updating appointment with ID: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found");
                });

        validateAppointmentTime(request.getDate(), request.getTime());

        appointment = appointmentMapper.updateFromRequest(request, appointment);

        if (request.getDesignId() != null &&
                (appointment.getDesign() == null || !appointment.getDesign().getId().equals(request.getDesignId()))) {
            Design design = designRepository.findById(request.getDesignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Design not found"));
            appointment.setDesign(design);
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.debug("Appointment updated with ID: {}", updatedAppointment.getId());

        contactService.sendAppointmentUpdateNotification(updatedAppointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        log.debug("Fetching appointment with ID: {}", id);
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse)
                .orElseThrow(() -> {
                    log.error("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found");
                });
    }

    @Override
    public void deleteAppointment(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found");
                });

        appointmentRepository.delete(appointment);
        log.debug("Appointment deleted with ID: {}", id);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        log.debug("Fetching all appointments");
        return appointmentRepository.findAllByOrderByDateAscTimeAsc().stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDate(LocalDate date) {
        log.debug("Fetching appointments for date: {}", date);
        return appointmentRepository.findByDateOrderByTimeAsc(date).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        log.debug("Fetching today's appointments");
        return getAppointmentsByDate(today);
    }

    @Override
    public List<AppointmentResponse> getUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        log.debug("Fetching upcoming appointments");
        return appointmentRepository.findByDateGreaterThanEqualOrderByDateAscTimeAsc(today).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelAppointment(Long id) {
        log.info("Cancelling appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        contactService.sendAppointmentCancellation(appointment);
    }

    @Override
    public void confirmAppointment(Long id) {
        log.info("Confirming appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        contactService.sendAppointmentConfirmation(appointment);
    }

    @Override
    public void completeAppointment(Long id) {
        log.info("Completing appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    @Override
    public AppointmentResponse changeAppointmentStatus(Long id, AppointmentStatus newStatus) {
        log.info("Changing status of appointment ID: {} to {}", id, newStatus);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("No se puede modificar una cita completada");
        }

        appointment.setStatus(newStatus);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        switch (newStatus) {
            case CONFIRMED:
                contactService.sendAppointmentConfirmation(updatedAppointment);
                break;
            case CANCELLED:
                contactService.sendAppointmentCancellation(updatedAppointment);
                break;
            case COMPLETED:
                contactService.sendAppointmentCompletion(updatedAppointment);
                break;
            case PENDING:
                contactService.sendAppointmentUpdateNotification(updatedAppointment);
                break;
            default:
                break;
        }

        return appointmentMapper.toResponse(updatedAppointment);
    }

    private void validateAppointmentTime(LocalDate date, String time) throws InvalidAppointmentTimeException {
        if (date.isEqual(LocalDate.now())) {
            LocalTime appointmentTime = LocalTime.parse(time);
            if (appointmentTime.isBefore(LocalTime.now().plusHours(1))) {
                log.error("Attempt to create appointment with invalid time: {}", time);
                throw new InvalidAppointmentTimeException(" is not a valid appointment time");
            }
        }

        LocalTime appointmentTime = LocalTime.parse(time);
        if (appointmentTime.isBefore(LocalTime.of(9, 0))) {
            throw new InvalidAppointmentTimeException("Appointments cannot be before 9:00 AM");
        }
        if (appointmentTime.isAfter(LocalTime.of(18, 0))) {
            throw new InvalidAppointmentTimeException("Appointments cannot be after 6:00 PM");
        }
    }
}