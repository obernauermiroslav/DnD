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
        Items helmet1 = new Items(null, "Leather helmet", ItemType.HELMET, 0, 1,6, 50,0,"");
        itemsRepository.save(helmet1);
        Items helmet2 = new Items(null, "Chain mail helmet", ItemType.HELMET, 0, 2,8,75,0,"");
        itemsRepository.save(helmet2);
        Items helmet3 = new Items(null, "Plate helmet", ItemType.HELMET, 0, 3,11,110,0,"");
        itemsRepository.save(helmet3);


        Items armor1 = new Items(null, "Leather armor", ItemType.ARMOR, 0, 2,12,70,0,"");
        itemsRepository.save(armor1);
        Items armor2 = new Items(null, "Chain mail armor", ItemType.ARMOR, 0, 4,17,115,0,"");
        itemsRepository.save(armor2);
        Items armor3 = new Items(null, "Plate armor", ItemType.ARMOR, 0, 6,25,150,0,"");
        itemsRepository.save(armor3);


        Items trousers1 = new Items(null, "Leather trousers", ItemType.TROUSERS, 0, 1,7,60,0,"");
        itemsRepository.save(trousers1);
        Items trousers2 = new Items(null, "Chain mail trousers", ItemType.TROUSERS, 0, 2,12,105,0,"");
        itemsRepository.save(trousers2);
        Items trousers3 = new Items(null, "Plate trousers", ItemType.TROUSERS, 0, 3,18,130,0,"");
        itemsRepository.save(trousers3);


        Items boots1 = new Items(null, "Leather boots", ItemType.BOOTS, 0, 1,6,50,0,"");
        itemsRepository.save(boots1);
        Items boots2 = new Items(null, "Chain mail boots", ItemType.BOOTS, 0, 2,8,80,0,"");
        itemsRepository.save(boots2);
        Items boots3 = new Items(null, "Plate  boots", ItemType.BOOTS, 0, 3,13,100,0,"");
        itemsRepository.save(boots3);


        Items gloves1 = new Items(null, "Leather gloves", ItemType.GLOVES, 0, 1,6,50,0,"");
        itemsRepository.save(gloves1);
        Items gloves2 = new Items(null, "Chain mail gloves", ItemType.GLOVES, 0, 2,8,80,0,"");
        itemsRepository.save(gloves2);
        Items gloves3 = new Items(null, "Plate gloves", ItemType.GLOVES, 0, 3,11,105,0,"");
        itemsRepository.save(gloves3);


        Items cloak1 = new Items(null, "Cloak", ItemType.CLOAK, 0, 1,6,60,0,"");
        itemsRepository.save(cloak1);
        Items cloak2 = new Items(null, "Enchanted Cloak", ItemType.CLOAK, 0, 3,12,100,0,"");
        itemsRepository.save(cloak2);
        Items cloak3 = new Items(null, "Dragon Cloak", ItemType.CLOAK, 0, 5,17,130,0,"");
        itemsRepository.save(cloak3);


        Items weapon1 = new Items(null, "Dagger", ItemType.WEAPON, 2, 0,0,50,0,"");
        itemsRepository.save(weapon1);
        Items weapon2 = new Items(null, "Sword", ItemType.WEAPON, 3, 0,0,80,0,"");
        itemsRepository.save(weapon2);
        Items weapon3 = new Items(null, "Axe", ItemType.WEAPON, 4, 0,0,100,0,"");
        itemsRepository.save(weapon3);
        Items weapon4 = new Items(null, "Hammer", ItemType.WEAPON, 6, 0,0,125,0,"");
        itemsRepository.save(weapon4);
        Items weapon5 = new Items(null, "Spear", ItemType.WEAPON, 8, 0,0,150,0,"");
        itemsRepository.save(weapon5);
        Items weapon6 = new Items(null, "Morningstar", ItemType.WEAPON, 11, 0,0,200,0,"");
        itemsRepository.save(weapon6);


        Items shield1 = new Items(null, "Small shield", ItemType.SHIELD, 0, 2,7,85,0,"15% chance to block enemy attack");
        itemsRepository.save(shield1);
        Items shield2 = new Items(null, "Medium shield", ItemType.SHIELD, 0, 3,10,125,0,"15% chance to block enemy attack");
        itemsRepository.save(shield2);
        Items shield3 = new Items(null, "Tower shield", ItemType.SHIELD, 0, 4,12,155,0,"15% chance to block enemy attack");
        itemsRepository.save(shield3);

        Items ring1 = new Items(null, "Ring of Health", ItemType.RING, 0, 0,10,100,0,"+10 to health");
        itemsRepository.save(ring1);
        Items ring2 = new Items(null, "Ring of Attack", ItemType.RING, 2, 0,0,100,0,"+2 to attack");
        itemsRepository.save(ring2);
        Items ring3 = new Items(null, "Ring of Defence", ItemType.RING, 0, 2,0,100,0,"+2 to defence");
        itemsRepository.save(ring3);

        Items necklace1 = new Items(null, "Necklace of Power", ItemType.NECKLACE, 1, 1,5,100,0,"+1 attack, +1 defence and +5 health");
        itemsRepository.save(necklace1);
        Items necklace2 = new Items(null, "Necklace of Greater Power", ItemType.NECKLACE, 2, 2,10,150,0,"+2 attack,+2 defence and +10 health ");
        itemsRepository.save(necklace2);


        Items potion = new Items(null, "Healing Potion", ItemType.POTION, 0, 0,0,120,0,"Heals for 33 points");
        itemsRepository.save(potion);


        Items spell1 = new Items(null, "Fire bolt", ItemType.SPELL, 0, 0,0,210,5,"Damages enemy for 22 points and ignores armor");
        itemsRepository.save(spell1);
        Items spell2 = new Items(null, "Weakness", ItemType.SPELL, 0, 0,0,210,5,"Decreases enemy attack by 2 points");
        itemsRepository.save(spell2);


        Enemies enemy1 = new Enemies(1L,"Great rat", 85,8,4);
        enemiesRepository.save(enemy1);
        Enemies enemy2 = new Enemies(2L,"Goblin", 116,16,6);
        enemiesRepository.save(enemy2);
        Enemies enemy3= new Enemies(3L,"Orc", 225,18,8);
        enemiesRepository.save(enemy3);
        Enemies enemy4= new Enemies(4L,"Troll", 275,21,11);
        enemiesRepository.save(enemy4);
        Enemies enemy5= new Enemies(5L,"Drake", 330,23,14);
        enemiesRepository.save(enemy5);
        Enemies enemy6= new Enemies(6L,"Behemoth", 420,28,17);
        enemiesRepository.save(enemy6);
        Enemies enemy7= new Enemies(7L,"Lich", 220,30,14);
        enemiesRepository.save(enemy7);
    }
}