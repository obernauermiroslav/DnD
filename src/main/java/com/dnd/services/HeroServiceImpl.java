package com.dnd.services;

import com.dnd.models.Hero;
import com.dnd.repositories.HeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HeroServiceImpl implements HeroService {

    private final HeroRepository heroRepository;

    @Autowired
    public HeroServiceImpl(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Override
    public Hero saveHero(Hero hero) {
        return heroRepository.save(hero);
    }


    @Override
    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    @Override
    public Optional<Hero> getHeroById(Long id) {
        return heroRepository.findById(id);
    }

    @Override
    public void updateHeroById(Long heroId, Hero updatedHero) {
        Optional<Hero> heroOptional = heroRepository.findById(heroId);
        if (heroOptional.isPresent()) {
            Hero hero = heroOptional.get();
            // Update the hero's attributes with the new values from updatedHero
            hero.setName(updatedHero.getName());
            hero.setAttack(updatedHero.getAttack());
            hero.setDefense(updatedHero.getDefense());
            hero.setHealth(updatedHero.getHealth());
            hero.setMana(updatedHero.getMana());
            hero.setGold(updatedHero.getGold());
            hero.setPotion(updatedHero.getPotion());
            hero.setSkillPoints(updatedHero.getSkillPoints());
            // ... Update other attributes as needed ...

            heroRepository.save(hero); // Save the updated hero to the database
        } else {
            // Hero with the given ID not found, you can handle the error accordingly
        }
    }

    @Override
    public void deleteHero(Long id) {
        heroRepository.deleteById(id);
    }

    @Override
    public Hero getYourHero() {
        return heroRepository.findById(1L).orElse(null);
    }
}