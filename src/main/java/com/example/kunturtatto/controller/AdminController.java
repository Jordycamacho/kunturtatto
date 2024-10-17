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
@PreAuthorize("permitAll()")
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
    /**
     * Muestra la página principal de gestión de diseños.
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista para mostrar los diseños.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('CREATE')")
    public String showDesign(Model model) {
        addCategoriesAndDesignsToModel(model);
        return "admin/design/showDesign";
    }

    /**
     * Muestra el formulario para crear un nuevo diseño.
     *
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista para crear un diseño.
     */
    @GetMapping("/diseños/crear")
    public String createDesign(Model model) {
        addCategoriesToModel(model);
        model.addAttribute("design", new Design());
        return "admin/design/createDesign";
    }

    /**
     * Muestra el formulario para editar un diseño existente.
     *
     * @param id    El ID del diseño a editar.
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista para editar un diseño.
     */
    @GetMapping("/diseños/editar/{id}")
    public String editDesign(@PathVariable Long id, Model model) {
        addCategoriesToModel(model);
        Optional<Design> optionalDesign = designService.findById(id);

        if (optionalDesign.isPresent()) {
            model.addAttribute("design", optionalDesign.get());
        } else {
            model.addAttribute("error", "No se encontró el diseño");
        }

        return "/admin/design/editDesign";
    }

    /**
     * Guarda un nuevo diseño.
     * 
     * @param redirectAttributes Atributos para redirección.
     * @return Redirige a la página de gestión de diseños.
     */
    @PostMapping("/diseños/crear/guardar")
    public String saveDesign(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Design design = new Design();
        design.setTitle(title);
        design.setDescription(description);
        CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        design.setCategoryDesign(categoryDesign);

        handleImageUpload(file, design, redirectAttributes);
        designService.save(design);
        redirectAttributes.addFlashAttribute("message", "Diseño guardado exitosamente");
        return "redirect:/admin";
    }

    /**
     * Actualiza un diseño existente.
     * 
     * @param idDesign           ID del diseño a actualizar.
     * @param title              Nuevo título del diseño.
     * @param description        Nueva descripción del diseño.
     * @param categoryDesignId   ID de la nueva categoría del diseño.
     * @param file               Archivo de imagen para el diseño.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirige a la página de administración si la actualización es
     *         exitosa, o a la página principal si no se encuentra el diseño.
     */
    @PostMapping("/diseños/editar/guardar")
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

        handleImageUpload(file, design, redirectAttributes);
        designService.update(design);
        redirectAttributes.addFlashAttribute("message", "Diseño actualizado con éxito");
        return "redirect:/admin";
    }

    /**
     * Elimina un diseño existente.
     * 
     * @param id ID del diseño a eliminar.
     * @return Redirige a la página de administración.
     */
    @PostMapping("/diseños/eliminar/{id}")
    public String deleteDesign(@PathVariable long id) {
        Design design = designService.findById(id).orElse(null);

        if (design != null && !design.getImage().equals("default.jpg")) {
            uploadFileService.deleteImage(design.getImage());
        }

        designService.delete(id);
        return "redirect:/admin";
    }

    /* Create Categories */

    /**
     * Muestra la lista de categorías.
     * 
     * @param model Modelo para agregar atributos.
     * @return Vista de la página de categorías.
     */
    @GetMapping("/categorias")
    public String showCategory(Model model) {
        addCategoriesToModel(model);
        return "admin/categoryDesign/showCategory";
    }

    /**
     * Muestra el formulario para crear una nueva categoría.
     * 
     * @param model Modelo para agregar atributos.
     * @return Vista del formulario de creación de categoría.
     */
    @GetMapping("/categorias/crear")
    public String createCategory(Model model) {
        addCategoriesToModel(model);
        return "admin/categoryDesign/createCategory";
    }

    /**
     * Guarda una nueva categoría.
     * 
     * @param nameCategoryDesign Nombre de la nueva categoría.
     * @param file               Archivo de imagen para la categoría.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirige a la página de gestión de categorías.
     */
    @PostMapping("categorias/crear/guardar")
    public String saveCategory(@RequestParam("nameCategoryDesign") String nameCategoryDesign,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setNameCategoryDesign(nameCategoryDesign);

        handleCategoryImageUpload(file, categoryDesign, redirectAttributes);
        categoryDesignService.save(categoryDesign);
        redirectAttributes.addFlashAttribute("message", "Categoría guardada exitosamente");

        return "redirect:/admin/categorias";
    }

    /**
     * Elimina una categoría existente.
     * 
     * @param id ID de la categoría a eliminar.
     * @return Redirige a la página de gestión de categorías.
     */
    @PostMapping("categorias/eliminar/{id}")
    public String deleteCategory(@PathVariable Long id) {
        CategoryDesign category = categoryDesignService.findById(id).orElse(null);

        if (category != null && !category.getImage().equals("default.jpg")) {
            uploadFileService.deleteImage(category.getImage());
        }

        categoryDesignService.deleteById(id);
        return "redirect:/admin/categorias";
    }

    /* Administracion usuarios */

    /**
     * Muestra la lista de usuarios.
     * 
     * @param model Modelo para agregar atributos.
     * @return Vista de la página de usuarios.
     */
    @GetMapping("/usuarios")
    public String showUsers(Model model) {
        addCategoriesToModel(model);
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "/admin/users/showUser";
    }

    /**
     * Elimina un usuario existente.
     * 
     * @param id                 ID del usuario a eliminar.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirige a la página de gestión de usuarios.
     */
    @PostMapping("/usuarios/eliminar/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Usuario eliminado exitosamente.");
        return "redirect:/usuarios";
    }

    /* Administracion citas */
    /**
     * Muestra la lista de citas.
     * 
     * @param model Modelo para agregar atributos.
     * @return Vista de la página de citas.
     */
    @GetMapping("/citas")
    public String showAppointments(Model model) {
        addCategoriesToModel(model);
        List<Appointment> appointments = appointmentService.findAll().stream()
                .sorted((a1, a2) -> {
                    int dateCompare = a1.getDate().compareTo(a2.getDate());
                    return dateCompare == 0 ? a1.getTime().compareTo(a2.getTime()) : dateCompare;
                })
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointments);
        return "/admin/appointment/showAppointment";
    }

    /**
     * Muestra el formulario para crear una nueva cita.
     * 
     * @param model Modelo para agregar atributos.
     * @return Vista del formulario de creación de citas.
     */
    @GetMapping("/crear-citas")
    public String createAppointments(Model model) {
        addCategoriesToModel(model);
        model.addAttribute("designs", designService.findAll());
        return "/admin/appointment/createAppointment";
    }

    /**
     * Muestra el formulario para editar una cita existente.
     * 
     * @param id    ID de la cita a editar.
     * @param model Modelo para agregar atributos.
     * @return Vista del formulario de edición de citas.
     */

    @GetMapping("/citas/editar/{id}")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        addCategoriesToModel(model);
        model.addAttribute("design", designService.findAll());
        model.addAttribute("appointment", appointmentService.findById(id));
        return "/admin/appointment/editAppointment";
    }

    /**
     * Muestra los detalles de una cita específica.
     * 
     * @param id    ID de la cita.
     * @param model Modelo para agregar atributos.
     * @return Vista de la página de detalles de la cita.
     */
    @GetMapping("/citas/verDetalles/{id}")
    public String showAppointmentDetails(@PathVariable Long id, Model model) {
        addCategoriesToModel(model);
        model.addAttribute("appointment", appointmentService.findById(id));
        return "/admin/appointment/showAppointmentDetails";
    }

    /**
     * Crea una nueva cita.
     * 
     * @param appointment Objeto Appointment con los detalles de la cita.
     * @return Redirige a la página de gestión de citas.
     */
    @PostMapping("/crear-citas/crear")
    public String createAppointment(Appointment appointment) {
        appointmentService.save(appointment);
        return "redirect:/admin/citas";
    }

    /**
     * Actualiza una cita existente.
     * 
     * @param id                 ID de la cita a actualizar.
     * @param appointment        Objeto Appointment con los nuevos detalles.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirige a la página de gestión de citas.
     */
    @PostMapping("/citas/editar/guardar/{id}")
    public String editAppointment(@PathVariable Long id, @ModelAttribute Appointment appointment,
            RedirectAttributes redirectAttributes) {
        appointment.setIdAppointment(id);
        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("message", "Cita actualizada exitosamente.");
        return "redirect:/admin/citas";
    }

    /**
     * Elimina una cita existente.
     * 
     * @param id                 ID de la cita a eliminar.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirige a la página de gestión de citas.
     */
    @PostMapping("/citas/eliminar/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Cita eliminada exitosamente.");
        return "redirect:/admin/citas";
    }

    /* Métodos Utilitarios */
    private void addCategoriesToModel(Model model) {
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
    }

    private void addCategoriesAndDesignsToModel(Model model) {
        addCategoriesToModel(model);
        model.addAttribute("designs", designService.findAll());
    }

    private void handleImageUpload(MultipartFile file, Design design, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);
                design.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error al guardar la imagen");
            }
        } else {
            design.setImage("default.jpg");
        }
    }

    private void handleCategoryImageUpload(MultipartFile file, CategoryDesign categoryDesign,
            RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);
                categoryDesign.setImage(imageName);
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error al guardar la imagen");
            }
        } else {
            categoryDesign.setImage("default.jpg");
        }
    }
}
