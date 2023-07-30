package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.repositories.HeroRepository;
import com.dnd.services.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/hero")
public class HeroController {

    private final HeroService heroService;

    @Autowired
    public HeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @GetMapping
    public String viewHero(Model model) {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            model.addAttribute("hero", hero);
            return "hero";
        } else {
            // Create a new hero with default attributes if no hero exists
            Hero newHero = new Hero();
            heroService.saveHero(newHero);
            model.addAttribute("hero", newHero);
            return "hero";
        }
    }

    @PostMapping("/save")
    public String saveHero(@RequestParam("heroName") String heroName) {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            hero.setName(heroName);
        } else {
            // Create a new hero with the given name
            hero = new Hero(heroName);
            hero.setGold(500);
            hero.setMana(10);
            hero.setPotion(3);
            hero.setSkillPoints(3);
        }
        heroService.saveHero(hero);
        return "redirect:/hero";
    }
}