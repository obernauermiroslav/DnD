package com.dnd.services;

import com.dnd.models.Hero;
import com.dnd.repositories.HeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HeroService {
    private final HeroRepository heroRepository;

    @Autowired
    public HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    // Method to save a new hero
    public Hero saveHero(Hero hero) {
        return heroRepository.save(hero);
    }

    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    // Method to retrieve a hero by id
    public Optional<Hero> getHeroById(Long id) {
        return heroRepository.findById(id);
    }

    // Method to update a hero
    public Hero updateHero(Hero hero) {
        return heroRepository.save(hero);
    }

    // Method to delete a hero by id
    public void deleteHero(Long id) {
        heroRepository.deleteById(id);
    }

    public Hero getYourHero() {
        return heroRepository.findById(1L).orElse(null);
    }
}