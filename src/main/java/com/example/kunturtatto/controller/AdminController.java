package com.example.kunturtatto.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUploadFileService;
import com.example.kunturtatto.service.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    Logger logger = LoggerFactory.getLogger(AdminController.class);

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

    /* <================ Create Designs ================> */

    @PostMapping("/diseños/crear")
    @Operation(summary = "Crear un diseño nuevo", description = "Permite guardar un nuevo diseño en la base de datos.")
    public ResponseEntity<String> saveDesign(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file) {

        logger.info("Iniciando la creación de un nuevo diseño: {}", title);

        try {
            Design design = new Design();
            design.setTitle(title);
            design.setDescription(description);

            CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            design.setCategoryDesign(categoryDesign);

            handleImageUpload(file, design);
            designService.save(design);

            logger.info("Diseño creado exitosamente: {}", title);
            return ResponseEntity.ok("Diseño guardado exitosamente.");
        } catch (Exception e) {
            logger.error("Error al guardar el diseño: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al guardar el diseño.");
        }
    }

    @PutMapping("/disenos/editar")
    @Operation(summary = "Actualizar un diseño existente", description = "Actualiza los datos de un diseño, incluyendo su título, descripción, categoría e imagen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diseño actualizado correctamente."),
            @ApiResponse(responseCode = "404", description = "Diseño o categoría no encontrados."),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar el diseño.")
    })
    public ResponseEntity<String> updateDesign(
            @RequestParam("idDesign") Long idDesign,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file) {

        logger.info("Iniciando actualización del diseño con ID: {}", idDesign);

        Optional<Design> optionalDesign = designService.findById(idDesign);
        if (!optionalDesign.isPresent()) {
            logger.warn("Diseño no encontrado para el ID: {}", idDesign);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diseño no encontrado.");
        }

        Design design = optionalDesign.get();
        design.setTitle(title);
        design.setDescription(description);

        CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId).orElse(null);
        if (categoryDesign == null) {
            logger.warn("Categoría no encontrada para el ID: {}", categoryDesignId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada.");
        }
        design.setCategoryDesign(categoryDesign);

        try {
            handleImageUpload(file, design);
            designService.update(design);
            logger.info("Diseño actualizado exitosamente: {}", title);
            return ResponseEntity.ok("Diseño actualizado exitosamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar el diseño: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al actualizar el diseño.");
        }
    }

    @DeleteMapping("/diseños/eliminar/{id}")
    @Operation(summary = "Eliminar un diseño existente", description = "Elimina un diseño de la base de datos, incluyendo su imagen si no es predeterminada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diseño eliminado correctamente."),
            @ApiResponse(responseCode = "404", description = "Diseño no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno al eliminar el diseño.")
    })
    public ResponseEntity<String> deleteDesign(@PathVariable long id) {

        logger.info("Iniciando eliminación del diseño con ID: {}", id);

        Optional<Design> optionalDesign = designService.findById(id);
        if (!optionalDesign.isPresent()) {
            logger.warn("Diseño no encontrado para el ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diseño no encontrado.");
        }

        Design design = optionalDesign.get();

        try {
            if (design != null && !design.getImage().equals("default.jpg")) {
                uploadFileService.deleteImage(design.getImage());
                logger.info("Imagen eliminada correctamente: {}", design.getImage());
            }
            designService.delete(id);
            logger.info("Diseño eliminado correctamente con ID: {}", id);
            return ResponseEntity.ok("Diseño eliminado correctamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar el diseño con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al eliminar el diseño.");
        }
    }

    /* <================ Create Categories ================> */

    @PostMapping("/categorias/crear/guardar")
    @Operation(summary = "Guardar una nueva categoría", description = "Permite crear y guardar una nueva categoría con su respectiva imagen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría guardada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados."),
            @ApiResponse(responseCode = "500", description = "Error interno al guardar la categoría.")
    })
    public ResponseEntity<String> saveCategory(@RequestParam("nameCategoryDesign") String nameCategoryDesign,
            @RequestParam("image") MultipartFile file) {
        logger.info("Iniciando creación de una nueva categoría con nombre: {}", nameCategoryDesign);
        try {
            if (nameCategoryDesign == null || nameCategoryDesign.trim().isEmpty()) {
                logger.warn("Nombre de la categoría vacío o nulo.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El nombre de la categoría no puede estar vacío.");
            }

            CategoryDesign categoryDesign = new CategoryDesign();
            categoryDesign.setNameCategoryDesign(nameCategoryDesign);

            handleCategoryImageUpload(file, categoryDesign);
            categoryDesignService.save(categoryDesign);
            logger.info("Categoría creada exitosamente: {}", nameCategoryDesign);
            return ResponseEntity.ok("Categoría creada exitosamente.");
        } catch (Exception e) {
            logger.error("Error al crear la categoría: {}", nameCategoryDesign, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la categoría.");
        }
    }

    @PostMapping("/categorias/eliminar/{id}")
    @Operation(summary = "Eliminar una categoría", description = "Permite eliminar una categoría específica por su ID, incluyendo su imagen si aplica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente."),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada."),
            @ApiResponse(responseCode = "500", description = "Error interno al eliminar la categoría.")
    })
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        logger.info("Iniciando eliminación de categoría con ID: {}", id);
        try {
            Optional<CategoryDesign> optionalCategory = categoryDesignService.findById(id);
            if (!optionalCategory.isPresent()) {
                logger.warn("Categoría no encontrada con ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada.");
            }

            CategoryDesign category = optionalCategory.get();
            if (!category.getImage().equals("default.jpg")) {
                uploadFileService.deleteImage(category.getImage());
                logger.info("Imagen eliminada correctamente: {}", category.getImage());
            }

            categoryDesignService.deleteById(id);
            logger.info("Categoría eliminada correctamente con ID: {}", id);
            return ResponseEntity.ok("Categoría eliminada exitosamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar la categoría con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la categoría.");
        }
    }

    /* <================ Create Users ================> */

    @PostMapping("/usuarios/eliminar/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user from the database by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "500", description = "Internal error while deleting the user.")
    })
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        logger.info("Starting deletion of user with ID: {}", id);
        try {
            Optional<User> optionalUser = userService.findUserById(id);
            if (!optionalUser.isPresent()) {
                logger.warn("User not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            userService.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting the user.");
        }
    }

    /* <================ Create Appointments ================> */

    @PostMapping("/crear-citas/crear")
    @Operation(summary = "Create a new appointment", description = "Creates a new appointment and saves it to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data."),
            @ApiResponse(responseCode = "500", description = "Error creating the appointment.")
    })
    public ResponseEntity<String> createAppointment(@RequestBody Appointment appointment) {
        logger.info("Starting creation of a new appointment.");
        try {
            appointmentService.save(appointment);
            logger.info("Appointment created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment created successfully.");
        } catch (Exception e) {
            logger.error("Error creating appointment.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while creating the appointment.");
        }
    }

    @PostMapping("/citas/editar/{id}")
    @Operation(summary = "Edit an existing appointment", description = "Updates the details of an existing appointment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment updated successfully."),
            @ApiResponse(responseCode = "404", description = "Appointment not found."),
            @ApiResponse(responseCode = "500", description = "Error updating the appointment.")
    })
    public ResponseEntity<String> editAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        logger.info("Starting update for appointment with ID: {}", id);
        try {
            appointment.setIdAppointment(id);
            appointmentService.save(appointment);
            logger.info("Appointment updated successfully.");
            return ResponseEntity.ok("Appointment updated successfully.");
        } catch (Exception e) {
            logger.error("Error updating appointment with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while updating the appointment.");
        }
    }

    @PostMapping("/citas/eliminar/{id}")
    @Operation(summary = "Delete an appointment", description = "Deletes an existing appointment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Appointment not found."),
            @ApiResponse(responseCode = "500", description = "Error deleting the appointment.")
    })
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        logger.info("Starting deletion of appointment with ID: {}", id);
        try {
            appointmentService.deleteById(id);
            logger.info("Appointment deleted successfully.");
            return ResponseEntity.ok("Appointment deleted successfully.");
        } catch (Exception e) {
            logger.error("Error deleting appointment with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting the appointment.");
        }
    }

    /* Métodos Utilitarios */

    private void handleImageUpload(MultipartFile file, Design design) throws IOException {
        if (!file.isEmpty()) {
            String imageName = uploadFileService.saveImages(file);
            design.setImage(imageName);
        } else {
            design.setImage("default.jpg");
        }
    }

    private void handleCategoryImageUpload(MultipartFile file, CategoryDesign categoryDesign) {
        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);
                categoryDesign.setImage(imageName);
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            categoryDesign.setImage("default.jpg");
        }
    }
}
