package com.example.kunturtatto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.model.enums.AppointmentStatus;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.service.AppointmentService;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;

import org.springframework.ui.Model;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
@Tag(name = "Gestión de Citas", description = "Operaciones administrativas para la gestión de citas del estudio de tatuajes")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final DesignService designService;
    private final CategoryService categoryService;

    @Operation(summary = "Mostrar lista de citas", description = "Muestra la página de administración con todas las citas próximas del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página cargada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public String showAppointments(Model model) {
        log.info("[GET /admin/appointments] Mostrando lista de citas");

        try {
            long startTime = System.currentTimeMillis();
            var appointments = appointmentService.getUpcomingAppointments();
            long endTime = System.currentTimeMillis();

            log.info("[GET /admin/appointments] Se cargaron {} citas en {} ms",
                    appointments.size(), (endTime - startTime));

            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            log.error("[GET /admin/appointments] Error al cargar citas: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar las citas");
        }

        return "admin/appointment/listAppointment";
    }

    @Operation(summary = "Formulario de creación de cita", description = "Muestra el formulario para crear una nueva cita")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario cargado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("[GET /admin/appointments/create] Mostrando formulario de creación de cita");

        try {
            long startTime = System.currentTimeMillis();
            var designs = designService.getAllDesigns();
            var categories = categoryService.getAllCategories();
            long endTime = System.currentTimeMillis();

            log.info("[GET /admin/appointments/create] Datos cargados: diseños={}, categorías={}, tiempo={}ms",
                    designs.size(), categories.size(), (endTime - startTime));

            model.addAttribute("appointmentRequest", new AppointmentRequest());
            model.addAttribute("designs", designs);
            model.addAttribute("categories", categories);
        } catch (Exception e) {
            log.error("[GET /admin/appointments/create] Error al cargar formulario: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el formulario");
        }

        return "admin/appointment/createAppointment";
    }

    @Operation(summary = "Mostrar calendario de citas", description = "Muestra la vista de calendario con todas las citas próximas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calendario cargado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        log.info("[GET /admin/appointments/calendar] Mostrando calendario de citas");

        try {
            long startTime = System.currentTimeMillis();
            var appointments = appointmentService.getUpcomingAppointments();
            long endTime = System.currentTimeMillis();

            log.info("[GET /admin/appointments/calendar] Se cargaron {} citas para el calendario en {} ms",
                    appointments.size(), (endTime - startTime));

            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            log.error("[GET /admin/appointments/calendar] Error al cargar calendario: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el calendario");
        }

        return "admin/appointment/calendar";
    }

    @Operation(summary = "Ver detalles de cita", description = "Muestra los detalles de una cita específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de cita cargados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public String viewAppointment(
            @Parameter(description = "ID de la cita a visualizar", required = true, example = "1") @PathVariable Long id,
            Model model) {

        log.info("[GET /admin/appointments/{}] Mostrando detalles de cita", id);

        try {
            long startTime = System.currentTimeMillis();
            var appointment = appointmentService.getAppointmentById(id);
            long endTime = System.currentTimeMillis();

            log.info("[GET /admin/appointments/{}] Cita cargada en {} ms", id, (endTime - startTime));

            model.addAttribute("appointment", appointment);
        } catch (Exception e) {
            log.error("[GET /admin/appointments/{}] Error al cargar cita: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la cita");
            return "redirect:/admin/appointments";
        }

        return "admin/appointment/viewAppointment";
    }

    @Operation(summary = "Formulario de edición de cita", description = "Muestra el formulario para editar una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario cargado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @Parameter(description = "ID de la cita a editar", required = true, example = "1") @PathVariable Long id,
            Model model) {

        log.info("[GET /admin/appointments/{}/edit] Mostrando formulario de edición de cita", id);

        try {
            long startTime = System.currentTimeMillis();
            var appointment = appointmentService.getAppointmentById(id);
            var designs = designService.getAllDesigns();
            var categories = categoryService.getAllCategories();
            long endTime = System.currentTimeMillis();

            log.info("[GET /admin/appointments/{}/edit] Datos cargados en {} ms", id, (endTime - startTime));

            model.addAttribute("appointmentRequest", appointment);
            model.addAttribute("designs", designs);
            model.addAttribute("categories", categories);
        } catch (Exception e) {
            log.error("[GET /admin/appointments/{}/edit] Error al cargar formulario: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el formulario de edición");
            return "redirect:/admin/appointments";
        }

        return "admin/appointment/editAppointment";
    }

    @Operation(summary = "Crear nueva cita", description = "Procesa el formulario y crea una nueva cita en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de crear"),
            @ApiResponse(responseCode = "400", description = "Datos del formulario inválidos"),
            @ApiResponse(responseCode = "404", description = "Diseño no encontrado (si se especificó)"),
            @ApiResponse(responseCode = "409", description = "Tiempo de cita inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/create")
    public String createAppointment(
            @Parameter(description = "Datos de la cita a crear", required = true) @Valid @ModelAttribute AppointmentRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("[POST /admin/appointments/create] Procesando creación de cita. Cliente={}, Email={}, Fecha={}",
                request.getCustomerName(), request.getCustomerEmail(), request.getDate());

        if (bindingResult.hasErrors()) {
            log.error("[POST /admin/appointments/create] Errores de validación encontrados: {}",
                    bindingResult.getAllErrors());

            try {
                var designs = designService.getAllDesigns();
                var categories = categoryService.getAllCategories();
                model.addAttribute("designs", designs);
                model.addAttribute("categories", categories);
            } catch (Exception e) {
                log.error("[POST /admin/appointments/create] Error al cargar datos para corrección: {}",
                        e.getMessage());
            }

            return "admin/appointment/createAppointment";
        }

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.createAppointment(request);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/create] Cita creada exitosamente en {} ms", (endTime - startTime));
            redirectAttributes.addFlashAttribute("success", "Cita creada exitosamente");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/create] Error al crear cita: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al crear cita: " + e.getMessage());

            try {
                var designs = designService.getAllDesigns();
                var categories = categoryService.getAllCategories();
                model.addAttribute("designs", designs);
                model.addAttribute("categories", categories);
            } catch (Exception ex) {
                log.error("[POST /admin/appointments/create] Error al cargar datos: {}", ex.getMessage());
            }

            return "admin/appointment/createAppointment";
        }

        return "redirect:/admin/appointments";
    }

    @Operation(summary = "Actualizar cita existente", description = "Actualiza una cita existente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de actualizar"),
            @ApiResponse(responseCode = "400", description = "Datos del formulario inválidos"),
            @ApiResponse(responseCode = "404", description = "Cita o diseño no encontrado"),
            @ApiResponse(responseCode = "409", description = "Tiempo de cita inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/edit")
    public String updateAppointment(
            @Parameter(description = "ID de la cita a actualizar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la cita", required = true) @Valid @ModelAttribute AppointmentRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("[POST /admin/appointments/{}/edit] Procesando actualización de cita", id);

        if (bindingResult.hasErrors()) {
            log.error("[POST /admin/appointments/{}/edit] Errores de validación encontrados: {}",
                    id, bindingResult.getAllErrors());

            try {
                var designs = designService.getAllDesigns();
                var categories = categoryService.getAllCategories();
                model.addAttribute("designs", designs);
                model.addAttribute("categories", categories);
            } catch (Exception e) {
                log.error("[POST /admin/appointments/{}/edit] Error al cargar datos: {}", id, e.getMessage());
            }

            return "admin/appointment/editAppointment";
        }

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.updateAppointment(id, request);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/{}/edit] Cita actualizada exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Cita actualizada exitosamente");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/{}/edit] Error al actualizar cita: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cita: " + e.getMessage());

            try {
                var designs = designService.getAllDesigns();
                var categories = categoryService.getAllCategories();
                model.addAttribute("designs", designs);
                model.addAttribute("categories", categories);
            } catch (Exception ex) {
                log.error("[POST /admin/appointments/{}/edit] Error al cargar datos: {}", id, ex.getMessage());
            }

            return "admin/appointment/editAppointment";
        }

        return "redirect:/admin/appointments/" + id;
    }

    @Operation(summary = "Eliminar cita", description = "Elimina una cita específica del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de eliminar"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/delete")
    public String deleteAppointment(
            @Parameter(description = "ID de la cita a eliminar", required = true, example = "1") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("[POST /admin/appointments/{}/delete] Eliminando cita", id);

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.deleteAppointment(id);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/{}/delete] Cita eliminada exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Cita eliminada exitosamente");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/{}/delete] Error al eliminar cita: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar cita: " + e.getMessage());
        }

        return "redirect:/admin/appointments";
    }

    @Operation(summary = "Cancelar cita", description = "Cancela una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de cancelar"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede cancelar una cita completada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(
            @Parameter(description = "ID de la cita a cancelar", required = true, example = "1") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("[POST /admin/appointments/{}/cancel] Cancelando cita", id);

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.cancelAppointment(id);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/{}/cancel] Cita cancelada exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/{}/cancel] Error al cancelar cita: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }

        return "redirect:/admin/appointments/" + id;
    }

    @Operation(summary = "Confirmar cita", description = "Confirma una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de confirmar"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/confirm")
    public String confirmAppointment(
            @Parameter(description = "ID de la cita a confirmar", required = true, example = "1") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("[POST /admin/appointments/{}/confirm] Confirmando cita", id);

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.confirmAppointment(id);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/{}/confirm] Cita confirmada exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Cita confirmada exitosamente");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/{}/confirm] Error al confirmar cita: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al confirmar cita: " + e.getMessage());
        }

        return "redirect:/admin/appointments/" + id;
    }

    @Operation(summary = "Marcar cita como completada", description = "Marca una cita como completada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de completar"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/complete")
    public String completeAppointment(
            @Parameter(description = "ID de la cita a marcar como completada", required = true, example = "1") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("[POST /admin/appointments/{}/complete] Completando cita", id);

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.completeAppointment(id);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/{}/complete] Cita completada exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Cita marcada como completada");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/{}/complete] Error al completar cita: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al completar cita: " + e.getMessage());
        }

        return "redirect:/admin/appointments/" + id;
    }

    @Operation(summary = "Cambiar estado de cita", description = "Cambia el estado de una cita a cualquier estado disponible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección exitosa después de cambiar estado"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede modificar una cita completada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/status")
    public String changeStatus(
            @Parameter(description = "ID de la cita a modificar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la cita", required = true, example = "CONFIRMED") @RequestParam AppointmentStatus status,
            RedirectAttributes redirectAttributes) {

        log.info("[POST /admin/appointments/{}/status] Cambiando estado de cita. Nuevo estado={}", id, status);

        try {
            long startTime = System.currentTimeMillis();
            appointmentService.changeAppointmentStatus(id, status);
            long endTime = System.currentTimeMillis();

            log.info("[POST /admin/appointments/{}/status] Estado de cita actualizado exitosamente en {} ms",
                    id, (endTime - startTime));

            redirectAttributes.addFlashAttribute("success", "Estado de cita actualizado exitosamente");

        } catch (Exception e) {
            log.error("[POST /admin/appointments/{}/status] Error al actualizar estado: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar estado: " + e.getMessage());
        }

        return "redirect:/admin/appointments/" + id;
    }
}