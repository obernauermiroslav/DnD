package com.dnd.services;

import com.dnd.models.Hero;
import com.dnd.models.Items;
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
            hero.setHealingPotion(updatedHero.getHealingPotion());
            hero.setManaPotion(updatedHero.getManaPotion());
            hero.setSkillPoints(updatedHero.getSkillPoints());
            // ... Update other attributes as needed ...

            heroRepository.save(hero); // Save the updated hero to the database
        } else {
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

    @Override
    public List<Items> getEquippedItems(Long heroId) {
        Optional<Hero> optionalHero = heroRepository.findById(heroId);
        if (optionalHero.isPresent()) {
            Hero hero = optionalHero.get();
            return hero.getEquippedItems();
        } else {
            return null; // Return null or throw an exception if hero not found
        }
    }

    @Override
    public void updateHeroEquippedItems(Long heroId, List<Items> equippedItems) {
        Optional<Hero> optionalHero = heroRepository.findById(heroId);
        if (optionalHero.isPresent()) {
            Hero hero = optionalHero.get();
            hero.setEquippedItems(equippedItems);
            heroRepository.save(hero);
        } else {
            // Handle hero not found
        }
    }
}

