package  com.example.kunturtatto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.example.kunturtatto.exception.InvalidAppointmentTimeException;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.AppointmentMapper;
import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.enums.AppointmentStatus;
import com.example.kunturtatto.repository.AppointmentRepository;
import com.example.kunturtatto.repository.DesignRepository;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;
import com.example.kunturtatto.service.impl.AppointmentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DesignRepository designRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private IContactService contactService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private AppointmentRequest validAppointmentRequest;
    private Appointment appointment;
    private AppointmentResponse appointmentResponse;
    private Design design;

    @BeforeEach
    void setUp() {
        // Configuración común para todos los tests
        validAppointmentRequest = AppointmentRequest.builder()
                .customerEmail("cliente@test.com")
                .date(LocalDate.now().plusDays(1))
                .time("10:00")
                .designId(1L)
                .build();

        design = new Design();
        design.setId(1L);
        design.setTitle("Tattoo Design");

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.PENDING);

        appointmentResponse = AppointmentResponse.builder()
                .id(1L)
                .customerEmail("cliente@test.com")
                .status(AppointmentStatus.PENDING)
                .build();
    }

    @Test
    void createAppointment_WithValidRequest_ShouldReturnAppointmentResponse() throws InvalidAppointmentTimeException {
        // Arrange
        when(appointmentMapper.toEntity(validAppointmentRequest)).thenReturn(appointment);
        when(designRepository.findById(1L)).thenReturn(Optional.of(design));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        // Act
        AppointmentResponse result = appointmentService.createAppointment(validAppointmentRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("cliente@test.com", result.getCustomerEmail());
        
        verify(appointmentRepository, times(1)).save(appointment);
        verify(contactService, times(1)).sendAppointmentConfirmation(appointment);
        verify(appointmentMapper, times(1)).toEntity(validAppointmentRequest);
        verify(appointmentMapper, times(1)).toResponse(appointment);
    }

    @Test
    void createAppointment_WithInvalidTime_ShouldThrowInvalidAppointmentTimeException() {
        // Arrange
        validAppointmentRequest.setDate(LocalDate.now());
        validAppointmentRequest.setTime(LocalTime.now().plusMinutes(30).toString());

        // Act & Assert
        assertThrows(InvalidAppointmentTimeException.class, 
            () -> appointmentService.createAppointment(validAppointmentRequest));
        
        verify(appointmentRepository, never()).save(any());
        verify(contactService, never()).sendAppointmentConfirmation(any());
    }

    @Test
    void createAppointment_WithNonExistentDesign_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(appointmentMapper.toEntity(validAppointmentRequest)).thenReturn(appointment);
        when(designRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> appointmentService.createAppointment(validAppointmentRequest));
        
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void getAppointmentById_WithExistingId_ShouldReturnAppointmentResponse() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        // Act
        AppointmentResponse result = appointmentService.getAppointmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void getAppointmentById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> appointmentService.getAppointmentById(999L));
        
        verify(appointmentRepository, times(1)).findById(999L);
    }

    @Test
    void updateAppointment_WithValidRequest_ShouldReturnUpdatedAppointment() throws InvalidAppointmentTimeException {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(designRepository.findById(1L)).thenReturn(Optional.of(design));
        when(appointmentMapper.updateFromRequest(validAppointmentRequest, appointment)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        // Act
        AppointmentResponse result = appointmentService.updateAppointment(1L, validAppointmentRequest);

        // Assert
        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(appointment);
        verify(contactService, times(1)).sendAppointmentUpdateNotification(appointment);
    }

    @Test
    void cancelAppointment_WithValidId_ShouldUpdateStatusAndSendNotification() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        // Act
        appointmentService.cancelAppointment(1L);

        // Assert
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
        verify(contactService, times(1)).sendAppointmentCancellation(appointment);
    }

    @Test
    void changeAppointmentStatus_FromCompletedToOther_ShouldThrowIllegalStateException() {
        // Arrange
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act & Assert
        assertThrows(IllegalStateException.class, 
            () -> appointmentService.changeAppointmentStatus(1L, AppointmentStatus.CONFIRMED));
        
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void getUpcomingAppointments_ShouldReturnFutureAppointments() {
        // Arrange
        List<Appointment> appointments = List.of(appointment);
        when(appointmentRepository.findByDateGreaterThanEqualOrderByDateAscTimeAsc(any(LocalDate.class)))
                .thenReturn(appointments);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        // Act
        List<AppointmentResponse> results = appointmentService.getUpcomingAppointments();

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        verify(appointmentRepository, times(1))
                .findByDateGreaterThanEqualOrderByDateAscTimeAsc(any(LocalDate.class));
    }

    // Tests para validación de horarios
    @Test
    void validateAppointmentTime_WithTimeBefore6AM_ShouldThrowException() {
        // Arrange
        LocalDate date = LocalDate.now().plusDays(1);
        String time = "05:00";

        // Act & Assert
        assertThrows(InvalidAppointmentTimeException.class, 
            () -> appointmentService.createAppointment(
                AppointmentRequest.builder()
                    .date(date)
                    .time(time)
                    .build()
            ));
    }

    @Test
    void validateAppointmentTime_WithTimeAfter11PM_ShouldThrowException() {
        // Arrange
        LocalDate date = LocalDate.now().plusDays(1);
        String time = "23:30";

        // Act & Assert
        assertThrows(InvalidAppointmentTimeException.class, 
            () -> appointmentService.createAppointment(
                AppointmentRequest.builder()
                    .date(date)
                    .time(time)
                    .build()
            ));
    }
}