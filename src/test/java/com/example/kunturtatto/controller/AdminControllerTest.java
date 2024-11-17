package com.example.kunturtatto.controller;

import com.example.kunturtatto.config.TestSecurityConfig;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IUploadFileService;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
public class AdminControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private IDesignService designService;

        @MockBean
        private ICategoryDesignService categoryDesignService;

        @MockBean
        private IAppointmentService appointmentService;

        @MockBean
        private IUploadFileService uploadFileService;

        @MockBean
        private IUserService userService;

        private List<CategoryDesign> categoryDesignList;
        private List<Design> designList;
        private List<User> userList;
        private List<Appointment> appointmentList;

        @BeforeEach
        public void setup() {
                categoryDesignList = Arrays.asList(
                                new CategoryDesign(1L, "Category1", "image1.jpg"),
                                new CategoryDesign(2L, "Category2", "image2.jpg"));

                designList = Arrays.asList(
                                new Design("Design1", "Description1", "image1.jpg", categoryDesignList.get(0)),
                                new Design("Design2", "Description2", "image2.jpg", categoryDesignList.get(1)));

                userList = Arrays.asList(
                                new User(1L, "prueba@gmail.com", "password", null, false, false, false, false, null,
                                                null));

                appointmentList = Arrays.asList(
                                new Appointment(1L, new Date(), "10:00", 100.0, "customer1@example.com",
                                                "Dragon Tattoo", "10x10", "http://example.com/ref",
                                                "arm", "Message for appointment."),
                                new Appointment(2L, new Date(), "15:00", 150.0, "customer2@example.com",
                                                "Phoenix Tattoo", "15x15", "http://example.com/ref2",
                                                "back", "Another message."));

                when(categoryDesignService.findById(1L)).thenReturn(Optional.of(categoryDesignList.get(0)));
                when(categoryDesignService.findAll()).thenReturn(categoryDesignList);
                when(designService.findAll()).thenReturn(designList);
                when(designService.findById(1L)).thenReturn(Optional.of(designList.get(0)));
                when(designService.findById(99L)).thenReturn(Optional.empty());
                when(userService.findUserById(1L)).thenReturn(Optional.of(userList.get(0)));
                when(appointmentService.findById(1L)).thenReturn(appointmentList.get(0));
                when(appointmentService.findById(99L)).thenThrow(new IllegalArgumentException("Invalid ID"));
        }

        @Test
        public void testSaveDesign_Success() throws Exception {
                MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg",
                                "test image".getBytes());
                when(categoryDesignService.findById(1L)).thenReturn(Optional.of(new CategoryDesign()));

                mockMvc.perform(((MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                                .multipart("/api/admin/diseños/crear")
                                .param("title", "New Design")
                                .param("description", "Description of the design")
                                .param("categoryDesign", "1"))
                                .file("image", file.getBytes()))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Diseño guardado exitosamente."));
        }

        @Test
        public void testSaveDesign_CategoryNotFound() throws Exception {
                MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg",
                                "test image".getBytes());
                when(categoryDesignService.findById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(((MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                                .multipart("/api/admin/diseños/crear")
                                .param("title", "New Design")
                                .param("description", "Description of the design")
                                .param("categoryDesign", "1"))
                                .file("image", file.getBytes()))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string("Ocurrió un error al guardar el diseño."));
        }

        @Test
        public void testUpdateDesign_Success() throws Exception {
                MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg",
                                "test image".getBytes());
                Design design = new Design();
                design.setIdDesign(1L);
                when(designService.findById(1L)).thenReturn(Optional.of(design));
                when(categoryDesignService.findById(1L)).thenReturn(Optional.of(new CategoryDesign()));

                mockMvc.perform(((MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                                .multipart("/api/admin/disenos/editar")
                                .param("idDesign", "1")
                                .param("title", "Updated Design")
                                .param("description", "Updated description")
                                .param("categoryDesign", "1"))
                                .file("image", file.getBytes()))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Diseño actualizado exitosamente."));
        }

        @Test
        public void testUpdateDesign_NotFound() throws Exception {
                MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg",
                                "test image".getBytes());
                when(designService.findById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(((MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                                .multipart("/api/admin/disenos/editar")
                                .param("idDesign", "1")
                                .param("title", "Updated Design")
                                .param("description", "Updated description")
                                .param("categoryDesign", "1"))
                                .file("image", file.getBytes()))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Diseño no encontrado."));
        }

        @Test
        public void testDeleteDesign_Success() throws Exception {
                Design design = new Design();
                design.setIdDesign(1L);
                design.setImage("image.jpg");
                when(designService.findById(1L)).thenReturn(Optional.of(design));

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/diseños/eliminar/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Diseño eliminado correctamente."));
        }

        @Test
        public void testDeleteDesign_NotFound() throws Exception {
                when(designService.findById(1L)).thenReturn(Optional.empty());

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/diseños/eliminar/1"))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Diseño no encontrado."));
        }

        @Test
        public void testDeleteDesign_ServerError() throws Exception {
                Design design = new Design();
                design.setIdDesign(1L);
                design.setImage("image.jpg");
                when(designService.findById(1L)).thenReturn(Optional.of(design));
                doThrow(new RuntimeException("Delete failed")).when(designService).delete(1L);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/diseños/eliminar/1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string("Ocurrió un error al eliminar el diseño."));
        }

        @Test
        public void testSaveCategory_Success() throws Exception {
                MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg",
                                "test image".getBytes());
                mockMvc.perform(((MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                                .multipart("/api/admin/categorias/crear/guardar")
                                .param("nameCategoryDesign", "New Category"))
                                .file("image", file.getBytes()))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Categoría creada exitosamente."));
        }

        @Test
        public void testSaveCategory_BadRequest() throws Exception {
                MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg",
                                "test image".getBytes());
                mockMvc.perform(((MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                                .multipart("/api/admin/categorias/crear/guardar")
                                .param("nameCategoryDesign", "") // Parametro vacío
                )
                                .file("image", file.getBytes()))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("El nombre de la categoría no puede estar vacío."));
        }

        @Test
        public void testDeleteCategory_Success() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/categorias/eliminar/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Categoría eliminada exitosamente."));
        }

        @Test
        public void testDeleteCategory_NotFound() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/categorias/eliminar/{id}", 99L))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Categoría no encontrada."));
        }

        @Test
        public void testDeleteUser_Success() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/usuarios/eliminar/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(content().string("User deleted successfully."));
        }

        @Test
        public void testDeleteUser_NotFound() throws Exception {
                when(userService.findUserById(99L)).thenReturn(java.util.Optional.empty());

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/usuarios/eliminar/{id}", 99L))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("User not found."));
        }

        @Test
        public void testCreateAppointment_Success() throws Exception {
                

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/crear-citas/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(appointmentList)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string("Appointment created successfully."));
        }

        @Test
        public void testEditAppointment_Success() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/citas/editar/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(appointmentList)))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Appointment updated successfully."));
        }

        @Test
        public void testDeleteAppointment_Success() throws Exception {
                when(appointmentService.findById(1L)).thenReturn(appointmentList.get(0));

                mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/citas/eliminar/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Appointment deleted successfully."));
        }
}
