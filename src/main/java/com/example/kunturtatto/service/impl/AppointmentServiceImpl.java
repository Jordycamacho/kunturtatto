package com.example.kunturtatto.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @CacheEvict(value = { "appointmentsAll", "appointmentsByDate", "appointmentsToday",
            "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public AppointmentResponse createAppointment(AppointmentRequest request) throws InvalidAppointmentTimeException {
        log.info(
                "[APPOINTMENT_SERVICE] Iniciando creación de cita. Datos recibidos: cliente={}, email={}, fecha={}, hora={}",
                request.getCustomerName(), request.getCustomerEmail(), request.getDate(), request.getTime());

        long startTime = System.currentTimeMillis();

        log.debug("[APPOINTMENT_SERVICE] Validando tiempo de cita. Fecha={}, Hora={}",
                request.getDate(), request.getTime());

        validateAppointmentTime(request.getDate(), request.getTime());

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setStatus(AppointmentStatus.PENDING);

        log.debug("[APPOINTMENT_SERVICE] Entidad Appointment construida: id=null, cliente={}, estado=PENDING",
                request.getCustomerName());

        if (request.getDesignId() != null) {
            log.debug("[APPOINTMENT_SERVICE] Buscando diseño asociado. DesignID={}", request.getDesignId());
            Design design = designRepository.findById(request.getDesignId())
                    .orElseThrow(() -> {
                        log.error("[APPOINTMENT_SERVICE] Diseño no encontrado. ID={}", request.getDesignId());
                        return new ResourceNotFoundException("Design not found", null, null);
                    });
            appointment.setDesign(design);
            log.debug("[APPOINTMENT_SERVICE] Diseño asociado encontrado: id={}, título={}",
                    design.getId(), design.getTitle());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Cita creada exitosamente. ID={}, cliente={}, tiempoTotal={}ms",
                savedAppointment.getId(), savedAppointment.getCustomerName(), (endTime - startTime));

        log.debug("[APPOINTMENT_SERVICE] Enviando confirmación de cita al cliente. Email={}",
                savedAppointment.getCustomerEmail());
        contactService.sendAppointmentConfirmation(savedAppointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    @CacheEvict(value = { "appointmentsAll", "appointmentById", "appointmentsByDate",
            "appointmentsToday", "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request)
            throws InvalidAppointmentTimeException {
        log.info(
                "[APPOINTMENT_SERVICE] Iniciando actualización de cita. ID={}, datosRecibidos: cliente={}, fecha={}, hora={}",
                id, request.getCustomerName(), request.getDate(), request.getTime());

        long startTime = System.currentTimeMillis();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada para actualización. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        log.debug(
                "[APPOINTMENT_SERVICE] Cita encontrada: id={}, clienteActual={}, estadoActual={}, fechaActual={}, horaActual={}",
                appointment.getId(), appointment.getCustomerName(), appointment.getStatus(),
                appointment.getDate(), appointment.getTime());

        log.debug("[APPOINTMENT_SERVICE] Validando nuevo tiempo de cita. Fecha={}, Hora={}",
                request.getDate(), request.getTime());

        validateAppointmentTime(request.getDate(), request.getTime());

        appointment = appointmentMapper.updateFromRequest(request, appointment);

        if (request.getDesignId() != null &&
                (appointment.getDesign() == null || !appointment.getDesign().getId().equals(request.getDesignId()))) {
            log.debug("[APPOINTMENT_SERVICE] Cambiando diseño asociado. DesignIDAnterior={}, DesignIDNuevo={}",
                    appointment.getDesign() != null ? appointment.getDesign().getId() : "null",
                    request.getDesignId());

            Design design = designRepository.findById(request.getDesignId())
                    .orElseThrow(() -> {
                        log.error("[APPOINTMENT_SERVICE] Diseño no encontrado. ID={}", request.getDesignId());
                        return new ResourceNotFoundException("Design not found", null, id);
                    });
            appointment.setDesign(design);
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Cita actualizada exitosamente. ID={}, cliente={}, tiempoTotal={}ms",
                id, updatedAppointment.getCustomerName(), (endTime - startTime));

        log.debug("[APPOINTMENT_SERVICE] Enviando notificación de actualización. Email={}",
                updatedAppointment.getCustomerEmail());
        contactService.sendAppointmentUpdateNotification(updatedAppointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "appointmentById", key = "#id")
    public AppointmentResponse getAppointmentById(Long id) {
        log.debug("[APPOINTMENT_SERVICE] Obteniendo cita por ID. ID={}", id);

        long startTime = System.currentTimeMillis();

        AppointmentResponse response = appointmentRepository.findById(id)
                .map(appointment -> {
                    long queryTime = System.currentTimeMillis() - startTime;
                    log.debug("[APPOINTMENT_SERVICE] Cita obtenida de BD. ID={}, cliente={}, tiempoConsulta={}ms",
                            id, appointment.getCustomerName(), queryTime);
                    return appointmentMapper.toResponse(appointment);
                })
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        long endTime = System.currentTimeMillis();
        log.debug("[APPOINTMENT_SERVICE] Cita retornada. ID={}, tiempoTotal={}ms", id, (endTime - startTime));

        return response;
    }

    @Override
    @CacheEvict(value = { "appointmentsAll", "appointmentById", "appointmentsByDate",
            "appointmentsToday", "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public void deleteAppointment(Long id) {
        log.info("[APPOINTMENT_SERVICE] Iniciando eliminación de cita. ID={}", id);

        long startTime = System.currentTimeMillis();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada para eliminación. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        log.debug("[APPOINTMENT_SERVICE] Cita encontrada para eliminación: id={}, cliente={}, estado={}",
                appointment.getId(), appointment.getCustomerName(), appointment.getStatus());

        appointmentRepository.delete(appointment);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Cita eliminada exitosamente. ID={}, cliente={}, tiempoTotal={}ms",
                id, appointment.getCustomerName(), (endTime - startTime));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "appointmentsAll")
    public List<AppointmentResponse> getAllAppointments() {
        log.debug("[APPOINTMENT_SERVICE] Obteniendo todas las citas");

        long startTime = System.currentTimeMillis();

        List<AppointmentResponse> appointments = appointmentRepository.findAllByOrderByDateAscTimeAsc().stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Total de citas obtenidas: {}, tiempoTotal={}ms",
                appointments.size(), (endTime - startTime));

        return appointments;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "appointmentsByDate", key = "#date.toString()")
    public List<AppointmentResponse> getAppointmentsByDate(LocalDate date) {
        log.debug("[APPOINTMENT_SERVICE] Obteniendo citas por fecha. Fecha={}", date);

        long startTime = System.currentTimeMillis();

        List<AppointmentResponse> appointments = appointmentRepository.findByDateOrderByTimeAsc(date).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.debug("[APPOINTMENT_SERVICE] Citas por fecha obtenidas: fecha={}, cantidad={}, tiempoConsulta={}ms",
                date, appointments.size(), (endTime - startTime));

        return appointments;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "appointmentsToday")
    public List<AppointmentResponse> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        log.debug("[APPOINTMENT_SERVICE] Obteniendo citas de hoy. Fecha={}", today);

        long startTime = System.currentTimeMillis();

        List<AppointmentResponse> appointments = getAppointmentsByDate(today);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Citas de hoy obtenidas: cantidad={}, tiempoTotal={}ms",
                appointments.size(), (endTime - startTime));

        return appointments;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "appointmentsUpcoming")
    public List<AppointmentResponse> getUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        log.debug("[APPOINTMENT_SERVICE] Obteniendo citas próximas");

        long startTime = System.currentTimeMillis();

        List<AppointmentResponse> appointments = appointmentRepository
                .findByDateGreaterThanEqualOrderByDateAscTimeAsc(today).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Citas próximas obtenidas: cantidad={}, tiempoTotal={}ms",
                appointments.size(), (endTime - startTime));

        return appointments;
    }

    @Override
    @CacheEvict(value = { "appointmentsAll", "appointmentById", "appointmentsByDate",
            "appointmentsToday", "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public void cancelAppointment(Long id) {
        log.info("[APPOINTMENT_SERVICE] Cancelando cita. ID={}", id);

        long startTime = System.currentTimeMillis();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada para cancelación. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        log.debug("[APPOINTMENT_SERVICE] Cita encontrada: id={}, estadoActual={}",
                appointment.getId(), appointment.getStatus());

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Cita cancelada. ID={}, tiempoTotal={}ms", id, (endTime - startTime));

        log.debug("[APPOINTMENT_SERVICE] Enviando notificación de cancelación. Email={}",
                appointment.getCustomerEmail());
        contactService.sendAppointmentCancellation(appointment);
    }

    @Override
    @CacheEvict(value = { "appointmentsAll", "appointmentById", "appointmentsByDate",
            "appointmentsToday", "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public void confirmAppointment(Long id) {
        log.info("[APPOINTMENT_SERVICE] Confirmando cita. ID={}", id);

        long startTime = System.currentTimeMillis();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada para confirmación. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        log.debug("[APPOINTMENT_SERVICE] Cita encontrada: id={}, estadoActual={}",
                appointment.getId(), appointment.getStatus());

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Cita confirmada. ID={}, tiempoTotal={}ms", id, (endTime - startTime));

        log.debug("[APPOINTMENT_SERVICE] Enviando confirmación. Email={}", appointment.getCustomerEmail());
        contactService.sendAppointmentConfirmation(appointment);
    }

    @Override
    @CacheEvict(value = { "appointmentsAll", "appointmentById", "appointmentsByDate",
            "appointmentsToday", "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public void completeAppointment(Long id) {
        log.info("[APPOINTMENT_SERVICE] Completando cita. ID={}", id);

        long startTime = System.currentTimeMillis();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada para completar. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        log.debug("[APPOINTMENT_SERVICE] Cita encontrada: id={}, estadoActual={}",
                appointment.getId(), appointment.getStatus());

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        long endTime = System.currentTimeMillis();
        log.info("[APPOINTMENT_SERVICE] Cita completada. ID={}, tiempoTotal={}ms", id, (endTime - startTime));
    }

    @Override
    @CacheEvict(value = { "appointmentsAll", "appointmentById", "appointmentsByDate",
            "appointmentsToday", "appointmentsUpcoming", "appointmentsByStatus" }, allEntries = true)
    public AppointmentResponse changeAppointmentStatus(Long id, AppointmentStatus newStatus) {
        log.info("[APPOINTMENT_SERVICE] Cambiando estado de cita. ID={}, nuevoEstado={}", id, newStatus);

        long startTime = System.currentTimeMillis();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[APPOINTMENT_SERVICE] Cita no encontrada para cambio de estado. ID={}", id);
                    return new ResourceNotFoundException("Appointment not found", null, id);
                });

        log.debug("[APPOINTMENT_SERVICE] Cita encontrada: id={}, estadoActual={}, nuevoEstado={}",
                appointment.getId(), appointment.getStatus(), newStatus);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.COMPLETED) {
            log.warn("[APPOINTMENT_SERVICE] Intento de modificar cita completada. ID={}, estadoActual=COMPLETED", id);
            throw new IllegalStateException("No se puede modificar una cita completada");
        }

        appointment.setStatus(newStatus);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        long endTime = System.currentTimeMillis();
        log.info(
                "[APPOINTMENT_SERVICE] Estado de cita cambiado. ID={}, estadoAnterior={}, estadoNuevo={}, tiempoTotal={}ms",
                id, appointment.getStatus(), newStatus, (endTime - startTime));

        log.debug("[APPOINTMENT_SERVICE] Enviando notificaciones según nuevo estado. Estado={}", newStatus);
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
                log.debug("[APPOINTMENT_SERVICE] No se requiere notificación para estado: {}", newStatus);
                break;
        }

        return appointmentMapper.toResponse(updatedAppointment);
    }

    private void validateAppointmentTime(LocalDate date, String time) throws InvalidAppointmentTimeException {
        log.debug("[APPOINTMENT_SERVICE] Validando tiempo de cita. Fecha={}, Hora={}", date, time);

        LocalTime appointmentTime;
        try {
            appointmentTime = LocalTime.parse(time);
        } catch (Exception e) {
            log.error("[APPOINTMENT_SERVICE] Formato de hora inválido. Hora={}, error={}", time, e.getMessage());
            throw new InvalidAppointmentTimeException("Formato de hora inválido: " + time);
        }

        if (date.isEqual(LocalDate.now())) {
            if (appointmentTime.isBefore(LocalTime.now().plusHours(1))) {
                log.error("[APPOINTMENT_SERVICE] Hora de cita inválida para hoy. Hora={}, horaActual={}",
                        appointmentTime, LocalTime.now());
                throw new InvalidAppointmentTimeException(
                        "La cita debe ser programada con al menos 1 hora de anticipación");
            }
        }

        if (appointmentTime.isBefore(LocalTime.of(6, 0))) {
            log.error("[APPOINTMENT_SERVICE] Hora antes del horario permitido. Hora={}", appointmentTime);
            throw new InvalidAppointmentTimeException("Las citas no pueden ser antes de las 6:00 AM");
        }

        if (appointmentTime.isAfter(LocalTime.of(23, 0))) {
            log.error("[APPOINTMENT_SERVICE] Hora después del horario permitido. Hora={}", appointmentTime);
            throw new InvalidAppointmentTimeException("Las citas no pueden ser después de las 23:00 PM");
        }

        log.debug("[APPOINTMENT_SERVICE] Validación de tiempo exitosa. Fecha={}, Hora={}", date, time);
    }
}