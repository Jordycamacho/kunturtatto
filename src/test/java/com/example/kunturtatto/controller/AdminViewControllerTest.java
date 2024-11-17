package com.example.kunturtatto.controller;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.kunturtatto.config.TestSecurityConfig;
import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUserService;

@WebMvcTest(AdminViewController.class)
@Import(TestSecurityConfig.class)
public class AdminViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDesignService designService;

    @MockBean
    private ICategoryDesignService categoryDesignService;

    @MockBean
    private IAppointmentService appointmentService;

    @MockBean
    private IUserService userService;

    private List<CategoryDesign> categoryDesignList;
    private List<Design> designList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        categoryDesignList = Arrays.asList(
                new CategoryDesign(1L, "Category1", "image1.jpg"),
                new CategoryDesign(2L, "Category2", "image2.jpg"));

        designList = Arrays.asList(
                new Design("Design1", "Description1", "image1.jpg", categoryDesignList.get(0)),
                new Design("Design2", "Description2", "image2.jpg", categoryDesignList.get(1)));

        when(categoryDesignService.findAll()).thenReturn(categoryDesignList);
        when(designService.findAll()).thenReturn(designList);
        when(designService.findById(1L)).thenReturn(Optional.of(designList.get(0)));
        when(designService.findById(99L)).thenReturn(Optional.empty()); // ID inexistente
    }

    /* <================ Métodos para Diseños ================> */

    @Test
    public void testShowDesign() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/design/showDesign"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("designs"));
    }

    @Test
    public void testCreateDesign() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/diseños/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/design/createDesign"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("design"));
    }

    @Test
    public void testEditDesign_ValidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/diseños/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/design/editDesign"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("design"));
    }

    /* <================ Métodos para categorias ================> */

    @Test
    public void testShowCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/showCategory"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testCreateCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categorias/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/createCategory"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testShowCategory_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/showCategory"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testShowCategory_Failure() throws Exception {
        when(categoryDesignService.findAll()).thenThrow(new RuntimeException("Error loading categories"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testCreateCategory_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categorias/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/createCategory"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testCreateCategory_Failure() throws Exception {
        when(categoryDesignService.findAll()).thenThrow(new RuntimeException("Error loading category creation view"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/categorias/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

    /* <================ Métodos para Usuarios ================> */

    @Test
    public void testShowUsers_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/users/showUser"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testShowUsers_Failure() throws Exception {
        when(userService.findAll()).thenThrow(new RuntimeException("Error loading user list"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

    /* <================ Métodos para Citas ================> */

    @Test
    public void testShowAppointments_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/appointment/showAppointment"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testShowAppointments_Failure() throws Exception {
        when(appointmentService.findAll()).thenThrow(new RuntimeException("Error loading appointments"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testCreateAppointments_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/crear-citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/appointment/createAppointment"))
                .andExpect(model().attributeExists("designs"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testCreateAppointments_Failure() throws Exception {
        when(designService.findAll()).thenThrow(new RuntimeException("Error loading designs"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/crear-citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testEditAppointmentForm_Success() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setIdAppointment(1L);
        when(appointmentService.findById(1L)).thenReturn(appointment);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/appointment/editAppointment"))
                .andExpect(model().attributeExists("appointment"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testEditAppointmentForm_NotFound() throws Exception {
        when(appointmentService.findById(99L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas/editar/99"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testEditAppointmentForm_Failure() throws Exception {
        when(appointmentService.findById(1L)).thenThrow(new RuntimeException("Error loading appointment"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testShowAppointmentDetails_Success() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setIdAppointment(1L);
        when(appointmentService.findById(1L)).thenReturn(appointment);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas/verDetalles/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/appointment/showAppointmentDetails"))
                .andExpect(model().attributeExists("appointment"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testShowAppointmentDetails_NotFound() throws Exception {
        when(appointmentService.findById(99L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas/verDetalles/99"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testShowAppointmentDetails_Failure() throws Exception {
        when(appointmentService.findById(1L)).thenThrow(new RuntimeException("Error loading appointment details"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/citas/verDetalles/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/500"))
                .andExpect(model().attributeExists("error"));
    }

}
