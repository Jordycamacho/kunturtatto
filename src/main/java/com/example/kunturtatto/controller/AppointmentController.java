package com.example.kunturtatto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.service.AppointmentService;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;

import org.springframework.ui.Model;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final DesignService designService;
    private final CategoryService categoryService;

    @GetMapping
    public String showAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getUpcomingAppointments());
        return "admin/appointment/listAppointment";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("appointmentRequest", new AppointmentRequest());
        model.addAttribute("designs", designService.getAllDesigns());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/appointment/createAppointment";
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        model.addAttribute("appointments", appointmentService.getUpcomingAppointments());
        return "admin/appointment/calendar";
    }

    @GetMapping("/{id}")
    public String viewAppointment(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "admin/appointment/viewAppointment";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("appointmentRequest", appointmentService.getAppointmentById(id));
        model.addAttribute("designs", designService.getAllDesigns());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/appointment/editAppointment";
    }

    @PostMapping("/create")
    public String createAppointment(
            @Valid @ModelAttribute AppointmentRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("designs", designService.getAllDesigns());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/appointments/create";
        }
        
        try {
            appointmentService.createAppointment(request);
            redirectAttributes.addFlashAttribute("success", "Cita creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear cita: " + e.getMessage());
            return "redirect:/admin/appointments/create";
        }
        
        return "redirect:/admin/appointments";
    }

    @PostMapping("/{id}/edit")
    public String updateAppointment(
            @PathVariable Long id,
            @Valid @ModelAttribute AppointmentRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("designs", designService.getAllDesigns());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/appointments/edit";
        }
        
        try {
            appointmentService.updateAppointment(id, request);
            redirectAttributes.addFlashAttribute("success", "Cita actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cita: " + e.getMessage());
            return "redirect:/admin/appointments/" + id + "/edit";
        }
        
        return "redirect:/admin/appointments/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }
        return "redirect:/admin/appointments/" + id;
    }

    @PostMapping("/{id}/confirm")
    public String confirmAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.confirmAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Cita confirmada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al confirmar cita: " + e.getMessage());
        }
        return "redirect:/admin/appointments/" + id;
    }

    @PostMapping("/{id}/complete")
    public String completeAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.completeAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Cita marcada como completada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al completar cita: " + e.getMessage());
        }
        return "redirect:/admin/appointments/" + id;
    }

}
