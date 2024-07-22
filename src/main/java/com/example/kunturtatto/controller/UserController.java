package com.example.kunturtatto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/")
public class UserController {
    
    @GetMapping("")
    public String home() {
        
        return "user/index";
    }

    @GetMapping("/diseños")
    public String desings() {
        
        return "user/desings";
    }


    @GetMapping("/profile/{idUsers----------}")
    public String perfilUsuario() {
        
        return "user/profile";
    }

}
