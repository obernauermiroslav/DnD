package com.dnd;

import com.dnd.models.Enemies;
import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.repositories.EnemiesRepository;
import com.dnd.repositories.HeroRepository;
import com.dnd.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DnDApplication implements CommandLineRunner {

    private final ItemsRepository itemsRepository;
    private final HeroRepository heroRepository;
    private final EnemiesRepository enemiesRepository;

    @Autowired
    public DnDApplication(ItemsRepository itemsRepository, HeroRepository heroRepository,EnemiesRepository enemiesRepository) {
        this.itemsRepository = itemsRepository;
        this.heroRepository = heroRepository;
        this.enemiesRepository = enemiesRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(DnDApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Items helmet1 = new Items(null, "Leather helmet", ItemType.HELMET, 0, 1,5, 50,0);
        itemsRepository.save(helmet1);
        Items helmet2 = new Items(null, "Chain mail helmet", ItemType.HELMET, 0, 2,7,70,0);
        itemsRepository.save(helmet2);
        Items helmet3 = new Items(null, "Plate helmet", ItemType.HELMET, 0, 3,10,110,0);
        itemsRepository.save(helmet3);


        Items armor1 = new Items(null, "Leather armor", ItemType.ARMOR, 0, 2,10,70,0);
        itemsRepository.save(armor1);
        Items armor2 = new Items(null, "Chain mail armor", ItemType.ARMOR, 0, 4,15,115,0);
        itemsRepository.save(armor2);
        Items armor3 = new Items(null, "Plate armor", ItemType.ARMOR, 0, 6,20,150,0);
        itemsRepository.save(armor3);


        Items trousers1 = new Items(null, "Leather trousers", ItemType.TROUSERS, 0, 1,5,60,0);
        itemsRepository.save(trousers1);
        Items trousers2 = new Items(null, "Chain mail trousers", ItemType.TROUSERS, 0, 2,10,100,0);
        itemsRepository.save(trousers2);
        Items trousers3 = new Items(null, "Plate trousers", ItemType.TROUSERS, 0, 3,15,130,0);
        itemsRepository.save(trousers3);


        Items boots1 = new Items(null, "Leather boots", ItemType.BOOTS, 0, 1,5,50,0);
        itemsRepository.save(boots1);
        Items boots2 = new Items(null, "Chain mail boots", ItemType.BOOTS, 0, 2,7,80,0);
        itemsRepository.save(boots2);
        Items boots3 = new Items(null, "Plate  boots", ItemType.BOOTS, 0, 3,10,100,0);
        itemsRepository.save(boots3);


        Items gloves1 = new Items(null, "Leather gloves", ItemType.GLOVES, 0, 1,5,50,0);
        itemsRepository.save(gloves1);
        Items gloves2 = new Items(null, "Chain mail gloves", ItemType.GLOVES, 0, 2,7,80,0);
        itemsRepository.save(gloves2);
        Items gloves3 = new Items(null, "Plate gloves", ItemType.GLOVES, 0, 3,10,100,0);
        itemsRepository.save(gloves3);


        Items cloak1 = new Items(null, "Cloak", ItemType.CLOAK, 0, 1,5,60,0);
        itemsRepository.save(cloak1);
        Items cloak2 = new Items(null, "Enchanted Cloak", ItemType.CLOAK, 0, 3,10,100,0);
        itemsRepository.save(cloak2);
        Items cloak3 = new Items(null, "Dragon Cloak", ItemType.CLOAK, 0, 5,15,130,0);
        itemsRepository.save(cloak3);


        Items weapon1 = new Items(null, "Dagger", ItemType.WEAPON, 2, 0,0,50,0);
        itemsRepository.save(weapon1);
        Items weapon2 = new Items(null, "Sword", ItemType.WEAPON, 3, 0,0,80,0);
        itemsRepository.save(weapon2);
        Items weapon3 = new Items(null, "Axe", ItemType.WEAPON, 4, 0,0,100,0);
        itemsRepository.save(weapon3);
        Items weapon4 = new Items(null, "Hammer", ItemType.WEAPON, 6, 0,0,120,0);
        itemsRepository.save(weapon4);
        Items weapon5 = new Items(null, "Spear", ItemType.WEAPON, 8, 0,0,150,0);
        itemsRepository.save(weapon5);


        Items shield1 = new Items(null, "Small shield", ItemType.SHIELD, 0, 2,7,70,0);
        itemsRepository.save(shield1);
        Items shield2 = new Items(null, "Medium shield", ItemType.SHIELD, 0, 3,10,100,0);
        itemsRepository.save(shield2);
        Items shield3 = new Items(null, "Tower shield", ItemType.SHIELD, 0, 4,12,130,0);
        itemsRepository.save(shield3);

        Items potion = new Items(null, "Healing Potion", ItemType.POTION, 0, 0,0,120,0);
        itemsRepository.save(potion);

        Items spell = new Items(null, "Fire bolt", ItemType.SPELL, 0, 0,0,210,5);
        itemsRepository.save(spell);

        // Create a new Hero instance
        Hero hero = new Hero("hero");

        Enemies enemy1 = new Enemies(1L,"Goblin", 160,15,6);
        Enemies enemy2= new Enemies(2L,"Orc", 220,17,8);
        Enemies enemy3= new Enemies(3L,"Troll", 270,20,11);
        Enemies enemy4= new Enemies(4L,"Dragon", 330,24,14);

// Save the Hero entity to the database
        heroRepository.save(hero);
        enemiesRepository.save(enemy1);
        enemiesRepository.save(enemy2);
        enemiesRepository.save(enemy3);
        enemiesRepository.save(enemy4);
    }
}