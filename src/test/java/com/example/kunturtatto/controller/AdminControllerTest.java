package com.example.kunturtatto.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.RoleEnum;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUploadFileService;
import com.example.kunturtatto.service.IUserService;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICategoryDesignService categoryDesignService;

    @MockBean
    private IDesignService designService;

    @MockBean
    private IUserService userService;

    @MockBean
    private IUploadFileService uploadFileService;

    @MockBean
    private IAppointmentService appointmentService;

    private List<Design> designs;
    private List<CategoryDesign> categoriesDesign;
    private List<User> users;
    private List<Appointment> appointments;

    @BeforeEach
    public void setUp() {
        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setIdCategoryDesign(1L);
        categoryDesign.setNameCategoryDesign("Tattoos");
        categoryDesign.setImage("test-image.png");

        Design design1 = new Design();
        design1.setIdDesign(1L);
        design1.setTitle("Design 1");
        design1.setDescription("Description 1");
        design1.setCategoryDesign(categoryDesign);
        design1.setImage("image1.jpg");

        Design design2 = new Design();
        design2.setIdDesign(2L);
        design2.setTitle("Design 2");
        design2.setDescription("Description 2");
        design2.setCategoryDesign(categoryDesign);
        design2.setImage("image2.jpg");

        // Crear roles utilizando RoleEnum
        Role roleUser = new Role();
        roleUser.setIdRol(1L);
        roleUser.setRoleEnum(RoleEnum.USER);

        Role roleAdmin = new Role();
        roleAdmin.setIdRol(2L);
        roleAdmin.setRoleEnum(RoleEnum.ADMIN);

        // Crear usuarios de prueba
        User user1 = new User();
        user1.setIdUser(1L);
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setEnabled(true);
        user1.setAccountNoExpired(true);
        user1.setAccountNoLocked(true);
        user1.setCredentialNoExpired(true);
        user1.setRoles(new HashSet<>(Arrays.asList(roleUser)));

        User user2 = new User();
        user2.setIdUser(2L);
        user2.setEmail("admin@example.com");
        user2.setPassword("password2");
        user2.setEnabled(true);
        user2.setAccountNoExpired(true);
        user2.setAccountNoLocked(true);
        user2.setCredentialNoExpired(true);
        user2.setRoles(new HashSet<>(Arrays.asList(roleAdmin)));

        designs = Arrays.asList(design1, design2);
        categoriesDesign = Arrays.asList(categoryDesign);
        users = Arrays.asList(user1, user2);

        /* Appointments */
        Appointment appointment1 = new Appointment();
        appointment1.setIdAppointment(1L);
        appointment1.setDate(new Date());
        appointment1.setTime("14:30");
        appointment1.setPrice(35.00);
        appointment1.setCustomerEmail("Test@gmail.com");
        appointment1.setDesign("design1");
        appointment1.setTattooCm("42cm");
        appointment1.setLinkReference("https://Test.com");
        appointment1.setBody("Arm");
        appointment1.setMessage("test message");

        appointments = Arrays.asList(appointment1);

        when(appointmentService.findAll()).thenReturn(appointments);
        when(designService.findAll()).thenReturn(designs);
        when(categoryDesignService.findAll()).thenReturn(categoriesDesign);
        when(userService.findAll()).thenReturn(users);
    }

    /* Test Design */
    @Test
    public void testShowDesign() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/design/showDesign"))
                .andExpect(model().attribute("designs", designs))
                .andExpect(model().attribute("categories", categoriesDesign));

    }

    @Test
    public void testCreateDesign() throws Exception {
        mockMvc.perform(get("/admin/diseños/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/design/createDesign"))
                .andExpect(model().attribute("design", designs))
                .andExpect(model().attribute("categoriesDesign", categoriesDesign));
    }

    @Test
    public void testsaveDesign() throws Exception {
        Long categoryDesignId = 1L;
        String title = "Nes Design";
        String description = "Nes Design Description";
        String image = "newImage.jpg";
        MockMultipartFile file = new MockMultipartFile("image", "newImage.jpg", image, "image content".getBytes());

        when(categoryDesignService.findById(categoryDesignId)).thenReturn(Optional.of(categoriesDesign.get(0)));

        when(uploadFileService.saveImages(file)).thenReturn(image);

        mockMvc.perform(multipart("/admin/diseños/crear/guardar")
                .file(file)
                .param("title", title)
                .param("description", description)
                .param("categoryDesign", String.valueOf(categoryDesignId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Diseño guardado exitosamente"));

        // Verifica que se haya llamado al servicio de guardado de diseño
        verify(designService).save(any(Design.class));
    }

    @Test
    public void testUpdateDesign() throws Exception {
        Long designId = 1L;
        Design design = designs.get(0);
        Optional<Design> optionalDesign = Optional.of(design);

        when(designService.findById(designId)).thenReturn(optionalDesign);

        // Realiza la llamada GET y verifica la respuesta
        mockMvc.perform(get("/admin/diseños/editar/{id}", designId))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/design/editDesign"))
                .andExpect(model().attribute("design", design))
                .andExpect(model().attribute("categoriesDesign", categoriesDesign));
    }

    @Test
    public void testDeleteDesign() throws Exception {
        Long designId = 1L;
        Design design = designs.get(0);
        Optional<Design> optionalDesign = Optional.of(design);

        when(designService.findById(designId)).thenReturn(optionalDesign);

        doNothing().when(uploadFileService).deleteImage(design.getImage());

        mockMvc.perform(post("/admin/diseños/eliminar/{id}", designId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(designService).delete(designId);
        verify(uploadFileService).deleteImage(design.getImage());
    }

    /* Test Categories */
    @Test
    public void testShowCategoryDesign() throws Exception {
        mockMvc.perform(get("/admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/showCategory"))
                .andExpect(model().attribute("categories", categoriesDesign));

    }

    @Test
    public void testCreateCategoryDesign() throws Exception {
        mockMvc.perform(get("/admin/categorias/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/createCategory"))
                .andExpect(model().attribute("categoriesDesign", categoriesDesign));
    }

    @Test
    public void testSaveCategory() throws Exception {
        String nameCategoryDesign = "New Category";
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "Test Image Content".getBytes());

        // Simula el comportamiento del servicio de carga de archivos
        when(uploadFileService.saveImages(any(MultipartFile.class))).thenReturn("test.jpg");

        mockMvc.perform(multipart("/admin/categorias/crear/guardar")
                .file(file)
                .param("nameCategoryDesign", nameCategoryDesign))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categorias"))
                .andExpect(flash().attribute("message", "Categoría guardada exitosamente"));

        verify(categoryDesignService).save(any(CategoryDesign.class));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        Long categoryId = 1L;

        CategoryDesign category = new CategoryDesign();
        category.setIdCategoryDesign(categoryId);
        category.setImage("someImage.jpg");
        category.setNameCategoryDesign("someCategory");

        when(categoryDesignService.findById(categoryId)).thenReturn(Optional.of(category));

        doNothing().when(uploadFileService).deleteImage("someImage.jpg");
        doNothing().when(categoryDesignService).deleteById(categoryId);

        mockMvc.perform(post("/admin/categorias/eliminar/{id}", categoryId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categorias"));

        verify(uploadFileService).deleteImage("someImage.jpg");
        verify(categoryDesignService).deleteById(categoryId);
    }

    /* Tes Users */
    @Test
    public void testShowUsers() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/users/showUser"))
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("categories", categoriesDesign));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        Role roleUser = new Role();
        roleUser.setIdRol(1L);
        roleUser.setRoleEnum(RoleEnum.USER);

        User user1 = new User();
        user1.setIdUser(userId);
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setEnabled(true);
        user1.setAccountNoExpired(true);
        user1.setAccountNoLocked(true);
        user1.setCredentialNoExpired(true);
        user1.setRoles(new HashSet<>(Arrays.asList(roleUser)));

        doNothing().when(userService).deleteById(userId);

        mockMvc.perform(post("/admin/usuarios/eliminar/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attribute("message", "Usuario eliminado exitosamente."));
        verify(userService).deleteById(userId);

    }

    /* Test Appointments */
    @Test
    public void testShowAppointments() throws Exception {
        mockMvc.perform(get("/admin/citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/appointment/showAppointment"))
                .andExpect(model().attribute("categories", categoriesDesign))
                .andExpect(model().attribute("appointments", appointments));
    }

    @Test
    public void testCreateAppointments() throws Exception {
        mockMvc.perform(get("/admin/crear-citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/appointment/createAppointment"))
                .andExpect(model().attribute("designs", designs))
                .andExpect(model().attribute("categories", categoriesDesign));
    }

    @Test
    public void testEditAppointmentForm() throws Exception {
        Long idAppointment = 1L;

        // Obtener una cita simulada de la lista para la prueba
        Appointment appointment = appointments.get(0);

        // Configurar los mocks para que devuelvan los datos necesarios
        when(appointmentService.findById(idAppointment)).thenReturn(appointment); // Devolver la cita específica
        when(categoryDesignService.findAll()).thenReturn(categoriesDesign); // Devolver las categorías
        when(designService.findAll()).thenReturn(designs); // Devolver los diseños

        // Realizar la solicitud GET a "/admin/citas/editar/{id}"
        mockMvc.perform(get("/admin/citas/editar/{id}", idAppointment))
                .andExpect(status().isOk()) // Verificar que la respuesta es OK (200)
                .andExpect(view().name("/admin/appointment/editAppointment")) // Verificar el nombre de la vista
                .andExpect(model().attribute("appointment", appointment)) // Verificar que el modelo contiene el
                                                                          // Appointment correcto
                .andExpect(model().attribute("categories", categoriesDesign)) // Verificar que el modelo contiene las
                                                                              // categorías
                .andExpect(model().attribute("design", designs)); // Verificar que el modelo contiene los diseños
    }

    @Test
    public void testShowAppointmentsdetails() {

    }

}