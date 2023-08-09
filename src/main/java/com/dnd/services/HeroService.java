package com.dnd.services;

import com.dnd.models.Hero;
import com.dnd.models.Items;
import com.dnd.repositories.HeroRepository;

import java.util.List;
import java.util.Optional;

public interface HeroService {

    // Method to save a new hero
    Hero saveHero(Hero hero);

    List<Hero> getAllHeroes();

    // Method to retrieve a hero by id
    Optional<Hero> getHeroById(Long id);

    // Method to update a hero
    void updateHeroById(Long heroId, Hero updatedHero);

    // Method to delete a hero by id
    void deleteHero(Long id);

    Hero getYourHero();

    List<Items> getEquippedItems(Long heroId);
    //void updateHeroStats(Hero hero);

    void updateHeroEquippedItems(Long heroId, List<Items> equippedItems);
}
