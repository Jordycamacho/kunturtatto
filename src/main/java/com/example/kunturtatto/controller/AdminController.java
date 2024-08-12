package com.example.kunturtatto.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUploadFileService;
import com.example.kunturtatto.service.IUserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("denyAll()")
public class AdminController {

    @Autowired
    private IDesignService designService;

    @Autowired
    private ICategoryDesignService categoryDesignService;

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    private IUserService userService;

    /* Create Designs */
    @GetMapping("")
    @PreAuthorize("hasAuthority('CREATE')")
    public String showDesign(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        List<Design> designs = designService.findAll();
        model.addAttribute("designs", designs);

        return "admin/design/showDesign";
    }

    @GetMapping("/diseños/crear")
    @PreAuthorize("hasAuthority('CREATE')")
    public String createDesign(Model model) {

        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        model.addAttribute("design", designService.findAll());
        model.addAttribute("categoriesDesign", categoryDesignService.findAll());

        return "admin/design/createDesign";
    }

    @GetMapping("/diseños/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String editDesign(@PathVariable Long id, Model model) {

        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        Design design = new Design();
        Optional<Design> optionalDesign = designService.findById(id);

        if (optionalDesign.isPresent()) {

            design = optionalDesign.get();

            model.addAttribute("design", design);
            model.addAttribute("categoriesDesign", categoryDesignService.findAll());
        } else {
            model.addAttribute("error", "No se encontró el diseño");
        }

        return "/admin/design/editDesign";
    }

    @PostMapping("/diseños/crear/guardar")
    @PreAuthorize("hasAuthority('CREATE')")
    public String saveDesign(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Design design = new Design();
        design.setTitle(title);
        design.setDescription(description);
        CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId).orElse(null);
        if (categoryDesign != null) {
            design.setCategoryDesign(categoryDesign);
        }

        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);

                design.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error al guardar la imagen");
                return "redirect:/admin/diseños";
            }
        } else {
            design.setImage("default.jpg");
        }

        designService.save(design);
        redirectAttributes.addFlashAttribute("message", "Diseño guardado exitosamente");
        return "redirect:/admin";
    }

    @PostMapping("/diseños/editar/guardar")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String updateDesign(
            @RequestParam("idDesign") Long idDesign,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Optional<Design> optionalDesign = designService.findById(idDesign);
        if (!optionalDesign.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Diseño no encontrado");
            return "redirect:/admin";
        }

        Design design = optionalDesign.get();
        design.setTitle(title);
        design.setDescription(description);
        CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId).orElse(null);
        if (categoryDesign != null) {
            design.setCategoryDesign(categoryDesign);
        }

        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);
                design.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error al guardar la imagen");
                return "redirect:/admin/diseños";
            }
        }

        designService.update(design);
        redirectAttributes.addFlashAttribute("message", "Diseño actualizado con éxito");
        return "redirect:/admin";
    }

    @PostMapping("/diseños/eliminar/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public String Delete(@PathVariable long id) {

        Design design = new Design();
        design = designService.findById(id).get();

        if (!design.getImage().equals("default.jpg")) {
            uploadFileService.deleteImage(design.getImage());
        }

        designService.delete(id);

        return "redirect:/admin";
    }

    /* Create Categories */
    @GetMapping("/categorias")
    @PreAuthorize("hasAuthority('CREATE')")
    public String showCategory(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        return "admin/categoryDesign/showCategory";
    }

    @GetMapping("/categorias/crear")
    @PreAuthorize("hasAuthority('CREATE')")
    public String createCategory(Model model) {

        model.addAttribute("categoriesDesign", categoryDesignService.findAll());

        return "admin/categoryDesign/createCategory";
    }

    @PostMapping("categorias/crear/guardar")
    @PreAuthorize("hasAuthority('CREATE')")
    public String saveCategory(@RequestParam("nameCategoryDesign") String nameCategoryDesign,
            RedirectAttributes redirectAttributes) {

        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setNameCategoryDesign(nameCategoryDesign);
        categoryDesignService.save(categoryDesign);

        redirectAttributes.addFlashAttribute("message", "Categoría guardada exitosamente");

        return "redirect:/admin/categorias";
    }

    @PostMapping("categorias/eliminar/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public String deleteCategoty(@PathVariable Long id) {

        categoryDesignService.deleteById(id);

        return "redirect:/admin/categorias";
    }

    /* Administracion usuarios */
    @GetMapping("/usuarios")
    @PreAuthorize("hasAuthority('CREATE')")
    public String showUsers(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        return "/admin/users/showUser";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Usuario eliminado exitosamente.");
        return "redirect:/usuarios";
    }

    /* Administracion citas */
    @GetMapping("/citas")
    @PreAuthorize("hasAuthority('CREATE')")
    public String showAppointments(Model model) {

        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        List<Appointment> appointments = appointmentService.findAll().stream()
                .sorted((a1, a2) -> {
                    int dateCompare = a1.getDate().compareTo(a2.getDate());
                    if (dateCompare == 0) {
                        return a1.getTime().compareTo(a2.getTime());
                    }
                    return dateCompare;
                })
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointments);

        return "/admin/appointment/showAppointment";
    }

    @GetMapping("/crear-citas")
    @PreAuthorize("hasAuthority('CREATE')")
    public String createAppointments(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        List<Design> designs = designService.findAll();

        model.addAttribute("designs", designs);
        model.addAttribute("categories", categoryDesigns);

        return "/admin/appointment/createAppointment";
    }

    @GetMapping("/citas/editar/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        List<Design> designs = designService.findAll();
        model.addAttribute("design", designs);
        Appointment appointment = appointmentService.findById(id);
        model.addAttribute("appointment", appointment);

        return "/admin/appointment/editAppointment";
    }

    @GetMapping("/citas/verDetalles/{id}")
    @PreAuthorize("hasAuthority('CREATE')")
    public String showAppointmentDetails(@PathVariable Long id, Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);

        Appointment appointment = appointmentService.findById(id);
        model.addAttribute("appointment", appointment);
        return "/admin/appointment/showAppointmentDetails";
    }

    @PostMapping("/crear-citas/crear")
    @PreAuthorize("hasAuthority('CREATE')")
    public String createAppointment(Appointment appointment) {
        appointmentService.save(appointment);
        return "redirect:/admin/citas";
    }

    @PostMapping("/citas/editar/guardar/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String editAppointment(@PathVariable Long id, @ModelAttribute Appointment appointment,
            RedirectAttributes redirectAttributes) {
        appointment.setIdAppointment(id);
        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("message", "Cita actualizada exitosamente.");
        return "redirect:/admin/citas";
    }

    @PostMapping("/citas/eliminar/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Cita eliminada exitosamente.");
        return "redirect:/admin/citas";
    }

}
