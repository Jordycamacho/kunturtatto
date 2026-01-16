package com.example.kunturtatto.controller;

import com.example.kunturtatto.dto.CategoryDto;
import com.example.kunturtatto.dto.DesignDto;
import com.example.kunturtatto.dto.SubCategoryDto;
import com.example.kunturtatto.service.CategoryService;
import com.example.kunturtatto.service.DesignService;
import com.example.kunturtatto.service.SubCategoryService;
import com.example.kunturtatto.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/Muthabara")
@PreAuthorize("permitAll()")
@Tag(name = "Frontend Público", description = "Endpoints públicos para clientes del estudio de tatuajes. No requiere autenticación.")
public class UserController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;
    @Autowired
    private DesignService designService;
    @Autowired
    private UserService userService;

    // Mapa para rate limiting por IP
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    private static final int MAX_REGISTER_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 60;

    @ModelAttribute("categories")
    public List<CategoryDto> categories() {
        log.debug("[USER_CONTROLLER] [MODEL_ATTRIBUTE] Cargando lista de categorías para el modelo");
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Página de inicio", description = "Muestra la página principal con las subcategorías de tatuajes, diseños y dibujos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página cargada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("")
    public String home(Model model, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        log.info("[USER_CONTROLLER] [HOME] Acceso desde IP: {}, User-Agent: {}",
                clientIp, userAgent);

        try {
            long startTime = System.currentTimeMillis();

            List<SubCategoryDto> tattooSubcategories = subCategoryService.getSubCategoriesByCategory(1L);
            List<SubCategoryDto> designSubcategories = subCategoryService.getSubCategoriesByCategory(2L);
            List<SubCategoryDto> drawingSubcategories = subCategoryService.getSubCategoriesByCategory(3L);

            long endTime = System.currentTimeMillis();

            log.debug("[USER_CONTROLLER] [HOME] Categorías cargadas en {} ms. "
                    + "Tatuajes: {}, Diseños: {}, Dibujos: {}",
                    (endTime - startTime),
                    tattooSubcategories.size(),
                    designSubcategories.size(),
                    drawingSubcategories.size());

            model.addAttribute("tattooSubcategories", tattooSubcategories);
            model.addAttribute("designSubcategories", designSubcategories);
            model.addAttribute("drawingSubcategories", drawingSubcategories);

            logAudit("PAGE_VIEW", "Página principal", clientIp, null);

        } catch (Exception e) {
            log.error("[USER_CONTROLLER] [HOME] Error al cargar página principal: {}",
                    e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la página");
        }

        return "user/index";
    }

    @Operation(summary = "Página de login", description = "Muestra el formulario de inicio de sesión para administradores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario de login cargado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ingresar")
    public String showLogin(Model model, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        log.info("[USER_CONTROLLER] [LOGIN_FORM] Acceso al formulario de login desde IP: {}", clientIp);

        logAudit("PAGE_VIEW", "Formulario de login", clientIp, null);

        return "user/login";
    }

    @Operation(summary = "Página de registro", description = "Muestra el formulario de registro para nuevos usuarios (actualmente no funcional)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario de registro cargado"),
            @ApiResponse(responseCode = "429", description = "Demasiados intentos de registro"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/registro")
    public String showSingUp(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String clientIp = getClientIp(request);

        if (isRateLimited(clientIp, "REGISTER")) {
            log.warn("[USER_CONTROLLER] [REGISTER_FORM] IP bloqueada por demasiados intentos: {}", clientIp);
            redirectAttributes.addFlashAttribute("error",
                    "Demasiados intentos de registro. Por favor, intente más tarde.");
            return "redirect:/Muthabara";
        }

        log.info("[USER_CONTROLLER] [REGISTER_FORM] Acceso al formulario de registro desde IP: {}", clientIp);

        logAudit("PAGE_VIEW", "Formulario de registro", clientIp, null);

        return "user/singup";
    }

    @Operation(summary = "Página de diseños", description = "Muestra los diseños disponibles, filtrados por categoría o subcategoría con paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diseños cargados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/diseños")
    public String showDesigns(
            @Parameter(description = "ID de la categoría para filtrar") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "ID de la subcategoría para filtrar") @RequestParam(required = false) Long subCategoryId,
            @Parameter(description = "Número de página (por defecto 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (por defecto 7)") @RequestParam(defaultValue = "7") int size,
            Model model,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        log.info("[USER_CONTROLLER] [DESIGNS] Acceso a diseños desde IP: {}, "
                + "categoryId: {}, subCategoryId: {}, User-Agent: {}",
                clientIp, categoryId, subCategoryId, userAgent);

        try {
            long startTime = System.currentTimeMillis();
            Page<DesignDto> designsPage;
            Long effectiveCategoryId = categoryId;

            if (subCategoryId != null && categoryId == null) {
                SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
                effectiveCategoryId = subCategory.getCategoryId();
                log.debug("[USER_CONTROLLER] [DESIGNS] Subcategoría encontrada: {} (Categoría: {})",
                        subCategory.getName(), effectiveCategoryId);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            String pageTitle;
            if (subCategoryId != null) {
                designsPage = designService.getDesignsBySubCategory(subCategoryId, pageable);
                SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
                pageTitle = subCategory.getName();
                log.debug("[USER_CONTROLLER] [DESIGNS] Mostrando diseños de subcategoría: {}", pageTitle);
            } else if (effectiveCategoryId != null) {
                designsPage = designService.getDesignsByCategory(effectiveCategoryId, pageable);
                CategoryDto category = categoryService.getCategoryById(effectiveCategoryId);
                pageTitle = category.getName();
                log.debug("[USER_CONTROLLER] [DESIGNS] Mostrando diseños de categoría: {}", pageTitle);
            } else {
                designsPage = designService.getAllDesignspPageable(pageable);
                pageTitle = "Todos los diseños";
                log.debug("[USER_CONTROLLER] [DESIGNS] Mostrando todos los diseños");
            }

            long endTime = System.currentTimeMillis();

            log.info("[USER_CONTROLLER] [DESIGNS] Carga completada en {} ms. "
                    + "Diseños: {}, Páginas: {}",
                    (endTime - startTime),
                    designsPage.getTotalElements(),
                    designsPage.getTotalPages());

            model.addAttribute("designs", designsPage.getContent());
            model.addAttribute("currentPage", designsPage.getNumber());
            model.addAttribute("totalPages", designsPage.getTotalPages());
            model.addAttribute("totalItems", designsPage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("selectedCategoryId", effectiveCategoryId);
            model.addAttribute("selectedSubCategoryId", subCategoryId);
            model.addAttribute("pageTitle", pageTitle);

            if (effectiveCategoryId != null) {
                List<SubCategoryDto> currentSubCategories = subCategoryService
                        .getSubCategoriesByCategory(effectiveCategoryId);
                model.addAttribute("currentSubCategories", currentSubCategories);
                log.debug("[USER_CONTROLLER] [DESIGNS] Subcategorías cargadas: {}",
                        currentSubCategories.size());
            }

            logAudit("PAGE_VIEW",
                    String.format("Página de diseños (Filtro: Categoría=%s, Subcategoría=%s)",
                            effectiveCategoryId, subCategoryId),
                    clientIp, null);

        } catch (Exception e) {
            log.error("[USER_CONTROLLER] [DESIGNS] Error al cargar diseños: {}",
                    e.getMessage(), e);
            model.addAttribute("error", "Error al cargar los diseños");
            logAudit("ERROR", "Error al cargar página de diseños", clientIp, e.getMessage());
        }

        return "user/designs";
    }

    @Operation(summary = "Página de contacto", description = "Muestra el formulario de contacto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario de contacto cargado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/contacto")
    public String showContact(Model model, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        log.info("[USER_CONTROLLER] [CONTACT] Acceso a contacto desde IP: {}", clientIp);

        // Auditoría
        logAudit("PAGE_VIEW", "Formulario de contacto", clientIp, null);

        return "user/contact";
    }

    @Operation(summary = "Procesar formulario de contacto", description = "Envía un mensaje de contacto (pendiente de implementación)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección después de enviar"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/contacto/guardar")
    public String saveContact(
            @Parameter(description = "Nombre del remitente") @RequestParam String nombre,
            @Parameter(description = "Email del remitente") @RequestParam String email,
            @Parameter(description = "Mensaje de contacto") @RequestParam String mensaje,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String clientIp = getClientIp(request);

        log.info("[USER_CONTROLLER] [CONTACT_SAVE] Intento de envío de contacto desde IP: {}, "
                + "Nombre: {}, Email: {}", clientIp, nombre, email);

        // Validación básica
        if (nombre == null || nombre.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                mensaje == null || mensaje.trim().isEmpty()) {

            log.warn("[USER_CONTROLLER] [CONTACT_SAVE] Datos inválidos desde IP: {}", clientIp);
            redirectAttributes.addFlashAttribute("error", "Todos los campos son obligatorios");
            return "redirect:/Muthabara/contacto";
        }

        try {
            log.info("[USER_CONTROLLER] [CONTACT_SAVE] Mensaje recibido: {} caracteres", mensaje.length());

            Thread.sleep(500);

            redirectAttributes.addFlashAttribute("success",
                    "¡Mensaje enviado exitosamente! Te contactaremos pronto.");

            logAudit("CONTACT_FORM",
                    String.format("Mensaje de contacto enviado: %s", email),
                    clientIp, null);

        } catch (Exception e) {
            log.error("[USER_CONTROLLER] [CONTACT_SAVE] Error al procesar contacto: {}",
                    e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al enviar el mensaje. Por favor, intente más tarde.");
            logAudit("ERROR", "Error en formulario de contacto", clientIp, e.getMessage());
        }

        return "redirect:/Muthabara/contacto";
    }

    @Operation(summary = "Procesar login", description = "Maneja el inicio de sesión (manejado por Spring Security)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección después de login"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login/guardar")
    public String saveLogIn(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String email = request.getParameter("username");

        log.info("[USER_CONTROLLER] [LOGIN_ATTEMPT] Intento de login desde IP: {}, Email: {}",
                clientIp, email != null ? email : "desconocido");

        logAudit("LOGIN_ATTEMPT",
                "Intento de inicio de sesión",
                clientIp,
                email != null ? email : "desconocido");

        return "redirect:/Muthabara";
    }

    @Operation(summary = "Procesar registro", description = "Registra un nuevo usuario en el sistema (actualmente deshabilitado para usuarios normales)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección después de registro"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "429", description = "Demasiados intentos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/registro/guardar")
    public String saveSignup(
            @Parameter(description = "Email del usuario") @RequestParam("email") String email,
            @Parameter(description = "Contraseña del usuario") @RequestParam("password") String password,
            @Parameter(description = "Confirmación de contraseña") @RequestParam("confirmPassword") String confirmPassword,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String clientIp = getClientIp(request);

        log.info("[USER_CONTROLLER] [REGISTER_ATTEMPT] Intento de registro desde IP: {}, Email: {}",
                clientIp, email);

        if (isRateLimited(clientIp, "REGISTER")) {
            log.warn("[USER_CONTROLLER] [REGISTER_ATTEMPT] IP bloqueada: {}", clientIp);
            redirectAttributes.addFlashAttribute("error",
                    "Demasiados intentos de registro. Por favor, intente más tarde.");
            return "redirect:/Muthabara/registro";
        }

        if (email == null || email.trim().isEmpty()) {
            log.warn("[USER_CONTROLLER] [REGISTER_ATTEMPT] Email vacío desde IP: {}", clientIp);
            redirectAttributes.addFlashAttribute("error", "El email es obligatorio");
            return "redirect:/Muthabara/registro";
        }

        if (password == null || password.trim().isEmpty()) {
            log.warn("[USER_CONTROLLER] [REGISTER_ATTEMPT] Contraseña vacía desde IP: {}", clientIp);
            redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria");
            return "redirect:/Muthabara/registro";
        }

        if (!password.equals(confirmPassword)) {
            log.warn("[USER_CONTROLLER] [REGISTER_ATTEMPT] Contraseñas no coinciden desde IP: {}", clientIp);
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/Muthabara/registro";
        }

        if (password.length() < 6) {
            log.warn("[USER_CONTROLLER] [REGISTER_ATTEMPT] Contraseña muy corta desde IP: {}", clientIp);
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "redirect:/Muthabara/registro";
        }

        try {
            incrementRateLimit(clientIp, "REGISTER");

            Date dateNow = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(dateNow);

            log.info("[USER_CONTROLLER] [REGISTER_ATTEMPT] Creando usuario: {} (Fecha: {})",
                    email, formattedDate);

            // NOTA: Actualmente este endpoint está deshabilitado para usuarios normales
            // Solo los administradores pueden crear usuarios a través del panel admin

            Thread.sleep(1000);

            log.warn("[USER_CONTROLLER] [REGISTER_ATTEMPT] Registro deshabilitado para usuarios normales. "
                    + "Email: {}, IP: {}", email, clientIp);

            redirectAttributes.addFlashAttribute("message",
                    "El registro de usuarios está actualmente deshabilitado. "
                            + "Solo los administradores pueden crear cuentas.");

            logAudit("REGISTER_ATTEMPT_DENIED",
                    "Intento de registro denegado (solo administradores)",
                    clientIp,
                    email);

        } catch (Exception e) {
            log.error("[USER_CONTROLLER] [REGISTER_ATTEMPT] Error durante registro: {}",
                    e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error durante el registro. Por favor, intente más tarde.");
            logAudit("ERROR", "Error en registro de usuario", clientIp, e.getMessage());
        }

        return "redirect:/Muthabara/ingresar";
    }

    @Operation(summary = "Política de privacidad", description = "Muestra la política de privacidad del estudio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página cargada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/politica-privacidad")
    public String privacyPolicy(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        log.info("[USER_CONTROLLER] [PRIVACY_POLICY] Acceso desde IP: {}", clientIp);

        logAudit("PAGE_VIEW", "Política de privacidad", clientIp, null);

        return "user/politica-privacidad";
    }

    @Operation(summary = "Términos y condiciones", description = "Muestra los términos y condiciones del estudio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página cargada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/terminos-condiciones")
    public String termsAndConditions(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        log.info("[USER_CONTROLLER] [TERMS] Acceso desde IP: {}", clientIp);

        logAudit("PAGE_VIEW", "Términos y condiciones", clientIp, null);

        return "user/terminos-condiciones";
    }

    // ==============================
    // MÉTODOS AUXILIARES DE SEGURIDAD
    // ==============================

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isRateLimited(String ip, String action) {
        String key = ip + "_" + action;
        RateLimitInfo info = rateLimitMap.get(key);

        if (info == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceFirstAttempt = currentTime - info.firstAttemptTime;

        if (TimeUnit.MILLISECONDS.toMinutes(timeSinceFirstAttempt) >= BLOCK_DURATION_MINUTES) {
            rateLimitMap.remove(key);
            return false;
        }

        if (info.attemptCount >= MAX_REGISTER_ATTEMPTS) {
            log.warn("[RATE_LIMIT] IP bloqueada: {} para acción: {} (Intentos: {})",
                    ip, action, info.attemptCount);
            return true;
        }

        return false;
    }

    private void incrementRateLimit(String ip, String action) {
        String key = ip + "_" + action;
        RateLimitInfo info = rateLimitMap.get(key);

        if (info == null) {
            info = new RateLimitInfo();
            info.firstAttemptTime = System.currentTimeMillis();
            rateLimitMap.put(key, info);
        }

        info.attemptCount++;
        info.lastAttemptTime = System.currentTimeMillis();

        log.debug("[RATE_LIMIT] Intento #{}/{} desde IP: {} para acción: {}",
                info.attemptCount, MAX_REGISTER_ATTEMPTS, ip, action);
    }

    /**
     * Log de auditoría centralizado
     */
    private void logAudit(String action, String details, String ip, String userEmail) {
        String auditMessage = String.format(
                "[AUDITORÍA_PÚBLICA] | Acción: %s | IP: %s | Email: %s | Detalles: %s | Timestamp: %d",
                action,
                ip != null ? ip : "desconocida",
                userEmail != null ? userEmail : "anónimo",
                details,
                System.currentTimeMillis());

        org.slf4j.Logger auditLogger = org.slf4j.LoggerFactory.getLogger("AUDIT_LOGGER_PUBLIC");
        auditLogger.info(auditMessage);

        log.info("[AUDIT] {}", auditMessage);
    }

    private static class RateLimitInfo {
        int attemptCount = 0;
        long firstAttemptTime = 0;
        long lastAttemptTime = 0;
    }

    // ==============================
    // ENDPOINTS DE MONITORIZACIÓN
    // ==============================

    @Operation(summary = "Estadísticas de uso público", description = "Muestra estadísticas de uso del sitio público (solo administradores)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas generadas"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/estadisticas")
    public String showStatistics(Model model) {
        log.info("[USER_CONTROLLER] [STATISTICS] Generando estadísticas de uso público");

        model.addAttribute("rateLimitEntries", rateLimitMap.size());

        model.addAttribute("cacheInfo",
                "El sitio usa cache para mejorar el rendimiento. Los diseños y categorías se cachean por 10 minutos.");

        model.addAttribute("securityInfo",
                "Sistema protegido con: Rate limiting, logs de auditoría, y validación de entrada.");

        return "admin/statistics/statistics";
    }

    @Operation(summary = "Limpiar rate limiting", description = "Limpia las entradas de rate limiting (solo administradores)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección después de limpiar"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping("/limpiar-rate-limiting")
    public String clearRateLimiting(RedirectAttributes redirectAttributes) {
        int beforeSize = rateLimitMap.size();
        rateLimitMap.clear();

        log.warn("[USER_CONTROLLER] [RATE_LIMIT_CLEAR] Rate limiting limpiado. "
                + "Entradas antes: {}, después: {}", beforeSize, rateLimitMap.size());

        redirectAttributes.addFlashAttribute("success",
                String.format("Rate limiting limpiado. Se removieron %d entradas.", beforeSize));

        return "redirect:/Muthabara/estadisticas";
    }
}