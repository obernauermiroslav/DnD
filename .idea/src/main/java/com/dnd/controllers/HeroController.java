package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.repositories.HeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hero")
public class HeroController {

    private final HeroRepository heroRepository;

    @Autowired
    public HeroController(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @GetMapping
    public String viewHero(Model model) {
        // Fetch the hero from the database
        Hero hero = heroRepository.findByName("hero");
        if (hero != null) {
            // Add the hero and equipped items to the model
            model.addAttribute("hero", hero);

            // Return the "hero" Thymeleaf template
            return "hero";
        } else {
            return "error";
        }
    }
}