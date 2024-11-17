package com.example.kunturtatto.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private static final Logger logger = LoggerFactory.getLogger(AdminViewController.class);

    @Autowired
    private IDesignService designService;

    @Autowired
    private ICategoryDesignService categoryDesignService;

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private IUserService userService;

    /* <================ Métodos para Diseños ================> */
    @GetMapping("")
    @Operation(summary = "Página principal de administración de diseños", description = "Muestra la lista de diseños y las categorías disponibles para su gestión.")
    public String showDesign(Model model) {
        logger.info("Accediendo a la página principal de administración de diseños.");
        addCategoriesAndDesignsToModel(model);
        return "admin/design/showDesign";
    }

    @GetMapping("/diseños/crear")
    @Operation(summary = "Formulario para creación de diseño", description = "Muestra el formulario donde se puede crear un nuevo diseño.")
    public String createDesign(Model model) {
        logger.info("Accediendo al formulario de creación de un nuevo diseño.");
        addCategoriesToModel(model);
        model.addAttribute("design", new Design());
        return "admin/design/createDesign";
    }

    @GetMapping("/diseños/editar/{id}")
    @Operation(summary = "Formulario para edición de diseño", description = "Permite cargar la información de un diseño existente para su edición.")
    @ApiResponse(responseCode = "200", description = "Formulario cargado correctamente.")
    public String editDesign(@PathVariable Long id, Model model) {
        logger.info("Accediendo al formulario de edición para el diseño con ID: {}", id);
        addCategoriesToModel(model);
        Optional<Design> optionalDesign = designService.findById(id);

        if (optionalDesign.isPresent()) {
            model.addAttribute("design", optionalDesign.get());
        } else {
            logger.warn("Diseño no encontrado para el ID: {}", id);
            model.addAttribute("error", "No se encontró el diseño");
        }

        return "/admin/design/editDesign";
    }

    /* <================ Métodos para categorias ================> */
    @GetMapping("/categorias")
    @Operation(summary = "Visualizar la lista de categorías", description = "Devuelve la vista con la lista de todas las categorías disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vista cargada correctamente."),
            @ApiResponse(responseCode = "500", description = "Error al cargar la vista.")
    })
    public String showCategory(Model model) {
        logger.info("Accediendo a la vista de lista de categorías.");
        try {
            addCategoriesToModel(model);
            return "admin/categoryDesign/showCategory";
        } catch (Exception e) {
            logger.error("Error al cargar la vista de categorías.", e);
            model.addAttribute("error", "No se pudo cargar la vista de categorías.");
            return "error/500";
        }
    }

    @GetMapping("/categorias/crear")
    @Operation(summary = "Mostrar formulario para crear una nueva categoría", description = "Devuelve la vista del formulario para crear una nueva categoría.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vista cargada correctamente."),
            @ApiResponse(responseCode = "500", description = "Error al cargar la vista.")
    })
    public String createCategory(Model model) {
        logger.info("Accediendo a la vista de creación de categoría.");
        try {
            addCategoriesToModel(model);
            return "admin/categoryDesign/createCategory";
        } catch (Exception e) {
            logger.error("Error al cargar la vista de creación de categoría.", e);
            model.addAttribute("error", "No se pudo cargar la vista de creación de categoría.");
            return "error/500";
        }
    }

    /* <================ Métodos para Usuarios ================> */

    @GetMapping("/usuarios")
    @Operation(summary = "View user list", description = "Displays a view with a list of all registered users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View loaded successfully."),
            @ApiResponse(responseCode = "500", description = "Error loading the view.")
    })
    public String showUsers(Model model) {
        logger.info("Accessing the user list view.");
        try {
            addCategoriesToModel(model);
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
            logger.info("User list loaded successfully.");
            return "/admin/users/showUser";
        } catch (Exception e) {
            logger.error("Error loading the user list view.", e);
            model.addAttribute("error", "Unable to load the user list view.");
            return "error/500";
        }
    }

    /* <================ Métodos para Citas ================> */
    @GetMapping("/citas")
    @Operation(summary = "View all appointments", description = "Displays a view with a list of all appointments, sorted by date and time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View loaded successfully."),
            @ApiResponse(responseCode = "500", description = "Error loading the appointments view.")
    })
    public String showAppointments(Model model) {
        logger.info("Accessing the appointment list view.");
        try {
            addCategoriesToModel(model);
            List<Appointment> appointments = appointmentService.findAll().stream()
                    .sorted((a1, a2) -> {
                        int dateCompare = a1.getDate().compareTo(a2.getDate());
                        return dateCompare == 0 ? a1.getTime().compareTo(a2.getTime()) : dateCompare;
                    })
                    .collect(Collectors.toList());
            model.addAttribute("appointments", appointments);
            logger.info("Appointments loaded successfully.");
            return "/admin/appointment/showAppointment";
        } catch (Exception e) {
            logger.error("Error loading appointments view.", e);
            model.addAttribute("error", "Unable to load appointments.");
            return "error/500";
        }
    }

    @GetMapping("/crear-citas")
    @Operation(summary = "View appointment creation form", description = "Displays a form to create a new appointment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View loaded successfully."),
            @ApiResponse(responseCode = "500", description = "Error loading the appointment creation form.")
    })
    public String createAppointments(Model model) {
        logger.info("Accessing the appointment creation form.");
        try {
            addCategoriesToModel(model);
            model.addAttribute("designs", designService.findAll());
            return "/admin/appointment/createAppointment";
        } catch (Exception e) {
            logger.error("Error loading appointment creation form.", e);
            model.addAttribute("error", "Unable to load appointment creation form.");
            return "error/500";
        }
    }

    @GetMapping("/citas/editar/{id}")
    @Operation(summary = "View appointment edit form", description = "Displays a form to edit an existing appointment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Form loaded successfully."),
            @ApiResponse(responseCode = "404", description = "Appointment not found."),
            @ApiResponse(responseCode = "500", description = "Error loading the appointment edit form.")
    })
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        logger.info("Accessing the edit form for appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentService.findById(id);
            if (appointment != null) {
                addCategoriesToModel(model);
            model.addAttribute("appointment", appointment);
            return "/admin/appointment/editAppointment";
            } else {
                logger.warn("Appointment not found for ID: {}", id);
                model.addAttribute("error", "Appointment not found.");
                return "error/404";
            }
        } catch (Exception e) {
            logger.error("Error loading appointment edit form.", e);
            model.addAttribute("error", "Unable to load appointment edit form.");
            return "error/500";
        }
    }

    @GetMapping("/citas/verDetalles/{id}")
    @Operation(summary = "View appointment details", description = "Displays detailed information for a specific appointment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Details loaded successfully."),
            @ApiResponse(responseCode = "404", description = "Appointment not found."),
            @ApiResponse(responseCode = "500", description = "Error loading the appointment details.")
    })
    public String showAppointmentDetails(@PathVariable Long id, Model model) {
        logger.info("Accessing details for appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentService.findById(id);
            if (appointment != null) {
                addCategoriesToModel(model);
                model.addAttribute("appointment", appointment);
                return "/admin/appointment/showAppointmentDetails";
            } else {
                logger.warn("Appointment not found for ID: {}", id);
                model.addAttribute("error", "Appointment not found.");
                return "error/404";
            }
        } catch (Exception e) {
            logger.error("Error loading appointment details.", e);
            model.addAttribute("error", "Unable to load appointment details.");
            return "error/500";
        }
    }

    /* <================ Métodos utilitarios para vistas ================> */
    private void addCategoriesToModel(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
    }

    private void addCategoriesAndDesignsToModel(Model model) {
        addCategoriesToModel(model);
        model.addAttribute("designs", designService.findAll());
    }
}
