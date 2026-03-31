package com.example.lab06.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        // Tra ve ten view login.html
        return "login";
    }

    @GetMapping("/home/search")
    public String search() {
        return "redirect:/products";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}

