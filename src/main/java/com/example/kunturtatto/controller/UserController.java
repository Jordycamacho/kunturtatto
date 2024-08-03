package com.example.kunturtatto.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/KunturTattoo")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private  ICategoryDesignService categoryDesignService;

    @Autowired
    private IDesignService designService;

    @GetMapping("")
    public String home(Model model) {
        List <CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
        
        return "user/index";
    }

    @GetMapping("/ingresar")
    public String showLogin(Model model) {
        List <CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
        return "user/login";
    }

    @GetMapping("/registro")
    public String showSingUp(Model model) {
        List <CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
        return "user/singup";
    }

    @GetMapping("/profile/{id}")
    public String perfilUsuario(Model model) {
        List <CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
        return "user/userprofile";
    }

    @GetMapping("/diseños")
    public String showDesigns(Model model) {
        List<Design> designs = designService.findAll();
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("designs", designs);
        model.addAttribute("categories", categoryDesigns);
        return "user/designs";
    }

     @GetMapping("/diseños/{categoryId}")
    public String showDesignsByCategory(@PathVariable Long categoryId, Model model) {
        List<Design> designs = designService.findByCategoryDesign(categoryId);
        CategoryDesign category = categoryDesignService.findById(categoryId).orElse(null);

        model.addAttribute("designs", designs);
        model.addAttribute("category", category);
        List<CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
        return "user/designs";
    }

    @GetMapping("/contacto")
    public String showContact(Model model) {
        List <CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categories", categoryDesigns);
        return "user/contact";
    }

    @PostMapping("/contacto/guardar")
    public String saveContact() {

        return "redirect:/KunturTattoo/contacto";
    }

    @PostMapping("/login/guardar")
    public String saveLogIn() {

        return "redirect:KunturTattoo";
    }

    @PostMapping("/registro/guardar")
    public String saveSignup(@RequestParam("email") String email, @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        String rol = "USER";
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        String formattedDate = formatter.format(dateNow);

        // Crear un nuevo usuario
        User user = new User();
        user.setEmail(email);

        user.setPassword(password);

        user.setType(rol);
        user.setRegistrationDate(dateNow);

        // Guardar el usuario en el servicio
        userService.save(user);

        redirectAttributes.addFlashAttribute("message", "Usuario registrado exitosamente.");
        return "redirect:/KunturTattoo/ingresar";
    }

    
}