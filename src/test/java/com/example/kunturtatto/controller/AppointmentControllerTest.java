package com.example.kunturtatto.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import com.example.kunturtatto.model.enums.AppointmentStatus;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;
import com.example.kunturtatto.service.AppointmentService;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private DesignService designService;

    @MockitoBean
    private CategoryService categoryService;

    private AppointmentRequest appointmentRequest;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        appointmentRequest = new AppointmentRequest();
        appointmentRequest.setCustomerEmail("test@example.com");

        appointmentResponse = AppointmentResponse.builder()
                .id(1L)
                .customerEmail("test@example.com")
                .status(AppointmentStatus.PENDING)
                .build();
    }

    @Test
    void showAppointments_ShouldReturnAppointmentListView() throws Exception {
        // Arrange
        when(appointmentService.getUpcomingAppointments()).thenReturn(List.of(appointmentResponse));

        // Act & Assert
        mockMvc.perform(get("/admin/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/appointment/listAppointment"))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    void showCreateForm_ShouldReturnCreateViewWithAttributes() throws Exception {
        // Arrange
        when(designService.getAllDesigns()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/admin/appointments/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/appointment/createAppointment"))
                .andExpect(model().attributeExists("appointmentRequest"))
                .andExpect(model().attributeExists("designs"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void createAppointment_WithValidRequest_ShouldRedirectWithSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(appointmentService).createAppointment(any(AppointmentRequest.class));

        // Act & Assert
        mockMvc.perform(post("/admin/appointments/create")
                .flashAttr("appointmentRequest", appointmentRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/appointments"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void createAppointment_WithBindingErrors_ShouldReturnToCreateForm() throws Exception {
        // Arrange
        when(designService.getAllDesigns()).thenReturn(Collections.emptyList());

        // Simular error de validación (email vacío)
        AppointmentRequest invalidRequest = new AppointmentRequest();

        // Act & Assert
        mockMvc.perform(post("/admin/appointments/create")
                .flashAttr("appointmentRequest", invalidRequest))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/appointments/create"))
                .andExpect(model().attributeExists("designs"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void viewAppointment_WithValidId_ShouldReturnViewWithAppointment() throws Exception {
        // Arrange
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentResponse);

        // Act & Assert
        mockMvc.perform(get("/admin/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/appointment/viewAppointment"))
                .andExpect(model().attributeExists("appointment"));
    }

    @Test
    void deleteAppointment_WithValidId_ShouldRedirectWithSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(appointmentService).deleteAppointment(1L);

        // Act & Assert
        mockMvc.perform(post("/admin/appointments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/appointments"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void cancelAppointment_WithValidId_ShouldRedirectWithSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(appointmentService).cancelAppointment(1L);

        // Act & Assert
        mockMvc.perform(post("/admin/appointments/1/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/appointments/1"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void changeStatus_WithValidParameters_ShouldRedirectWithSuccessMessage() throws Exception {
        // Arrange
        when(appointmentService.changeAppointmentStatus(1L, AppointmentStatus.CONFIRMED))
                .thenReturn(appointmentResponse);

        // Act & Assert
        mockMvc.perform(post("/admin/appointments/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/appointments/1"))
                .andExpect(flash().attributeExists("success"));
    }
}