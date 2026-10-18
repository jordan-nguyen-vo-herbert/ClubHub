package com.clubhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Handles the home page
public class HomeController {

    @GetMapping("/") // GET: user opens the site root
    public String home() {
        return "index"; // renders templates/index.html
    }
}
