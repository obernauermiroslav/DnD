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
        Items helmet1 = new Items(null, "Leather helmet", ItemType.HELMET, 0, 1,7,0, 50,0,"");
        itemsRepository.save(helmet1);
        Items helmet2 = new Items(null, "Chain mail helmet", ItemType.HELMET, 0, 2,9,0,75,0,"");
        itemsRepository.save(helmet2);
        Items helmet3 = new Items(null, "Plate helmet", ItemType.HELMET, 0, 3,12,0,110,0,"");
        itemsRepository.save(helmet3);
        Items helmet4 = new Items(null, "Mage Hat", ItemType.HELMET, 0, 1,5,13,75,0," + 13 mana");
        itemsRepository.save(helmet4);


        Items armor1 = new Items(null, "Leather armor", ItemType.ARMOR, 0, 2,14,0,70,0,"");
        itemsRepository.save(armor1);
        Items armor2 = new Items(null, "Chain mail armor", ItemType.ARMOR, 0, 4,19,0,115,0,"");
        itemsRepository.save(armor2);
        Items armor3 = new Items(null, "Plate armor", ItemType.ARMOR, 0, 6,26,0,155,0,"");
        itemsRepository.save(armor3);


        Items trousers1 = new Items(null, "Leather trousers", ItemType.TROUSERS, 0, 1,8,0,60,0,"");
        itemsRepository.save(trousers1);
        Items trousers2 = new Items(null, "Chain mail trousers", ItemType.TROUSERS, 0, 2,13,0,105,0,"");
        itemsRepository.save(trousers2);
        Items trousers3 = new Items(null, "Plate trousers", ItemType.TROUSERS, 0, 3,19,0,130,0,"");
        itemsRepository.save(trousers3);


        Items boots1 = new Items(null, "Leather boots", ItemType.BOOTS, 0, 1,7,0,50,0,"");
        itemsRepository.save(boots1);
        Items boots2 = new Items(null, "Chain mail boots", ItemType.BOOTS, 0, 2,9,0,80,0,"");
        itemsRepository.save(boots2);
        Items boots3 = new Items(null, "Plate  boots", ItemType.BOOTS, 0, 3,16,0,110,0,"");
        itemsRepository.save(boots3);


        Items gloves1 = new Items(null, "Leather gloves", ItemType.GLOVES, 0, 1,7,0,55,0,"");
        itemsRepository.save(gloves1);
        Items gloves2 = new Items(null, "Chain mail gloves", ItemType.GLOVES, 0, 2,9,0,80,0,"");
        itemsRepository.save(gloves2);
        Items gloves3 = new Items(null, "Plate gloves", ItemType.GLOVES, 0, 3,13,0,105,0,"");
        itemsRepository.save(gloves3);


        Items cloak1 = new Items(null, "Cloak", ItemType.CLOAK, 0, 1,7,0,60,0,"");
        itemsRepository.save(cloak1);
        Items cloak2 = new Items(null, "Enchanted Cloak", ItemType.CLOAK, 0, 3,11,10,105,0,"+10 mana");
        itemsRepository.save(cloak2);
        Items cloak3 = new Items(null, "Dragon Cloak", ItemType.CLOAK, 0, 5,20,0,135,0,"");
        itemsRepository.save(cloak3);


        Items weapon1 = new Items(null, "Dagger", ItemType.WEAPON, 2, 0,0,0,50,0,"");
        itemsRepository.save(weapon1);
        Items weapon2 = new Items(null, "Sword", ItemType.WEAPON, 3, 0,0,0,75,0,"");
        itemsRepository.save(weapon2);
        Items weapon3 = new Items(null, "Axe", ItemType.WEAPON, 4, 0,0,0,100,0,"");
        itemsRepository.save(weapon3);
        Items weapon4 = new Items(null, "Morningstar", ItemType.WEAPON, 6, 0,0,0,125,0,"");
        itemsRepository.save(weapon4);
        Items weapon5 = new Items(null, "Halberd", ItemType.WEAPON, 8, 0,0,0,150,0,"");
        itemsRepository.save(weapon5);
        Items weapon6 = new Items(null, "Warhammer", ItemType.WEAPON, 11, 0,0,0,200,0,"");
        itemsRepository.save(weapon6);
        Items weapon7 = new Items(null, "Mage's Staff", ItemType.WEAPON, 2, 0,0,13,135,0," + 12 mana");
        itemsRepository.save(weapon7);
        Items weapon8 = new Items(null, "Archmage's Staff", ItemType.WEAPON, 4, 0,0,25,180,0," + 25 mana");
        itemsRepository.save(weapon8);


        Items shield1 = new Items(null, "Small shield", ItemType.SHIELD, 0, 2,8,0,90,0,"16% chance to block enemy attack");
        itemsRepository.save(shield1);
        Items shield2 = new Items(null, "Medium shield", ItemType.SHIELD, 0, 3,12,0,125,0,"16% chance to block enemy attack");
        itemsRepository.save(shield2);
        Items shield3 = new Items(null, "Tower shield", ItemType.SHIELD, 0, 4,16,0,155,0,"16% chance to block enemy attack");
        itemsRepository.save(shield3);
        Items shield4 = new Items(null, "Fire Shield", ItemType.SHIELD, 0, 5,20,0,190,0,"18% chance to block enemy attack and can protect you against fire");
        itemsRepository.save(shield4);


        Items ring1 = new Items(null, "Ring of Health", ItemType.RING, 0, 0,20,0,100,0,"+20 health");
        itemsRepository.save(ring1);
        Items ring2 = new Items(null, "Ring of Attack", ItemType.RING, 3, 0,0,0,120,0,"+3 attack");
        itemsRepository.save(ring2);
        Items ring3 = new Items(null, "Ring of Defence", ItemType.RING, 0, 3,0,0,120,0,"+3 defence");
        itemsRepository.save(ring3);
        Items ring4 = new Items(null, "Ring of Mana", ItemType.RING, 0, 0,0,16,100,0,"+16 mana");
        itemsRepository.save(ring4);

        Items necklace1 = new Items(null, "Necklace of Power", ItemType.NECKLACE, 1, 1,10,0,110,0,"+1 attack, +1 defence, +10 health");
        itemsRepository.save(necklace1);
        Items necklace2 = new Items(null, "Necklace of Greater Power", ItemType.NECKLACE, 2, 2,20,0,150,0,"+2 attack,+2 defence, +20 health ");
        itemsRepository.save(necklace2);
        Items necklace3 = new Items(null, "Necklace of Spell Power", ItemType.NECKLACE, 0, 0,25,20,160,0," +25 health, +20 mana ");
        itemsRepository.save(necklace3);


        Items potion1 = new Items(null, "Healing Potion", ItemType.POTION, 0, 0,0,0,120,0,"Heals up to 30 health");
        itemsRepository.save(potion1);
        Items potion2 = new Items(null, "Mana Potion", ItemType.POTION, 0, 0,0,0,100,0,"Restores 20 mana");
        itemsRepository.save(potion2);


        Items spell1 = new Items(null, "Firebolt", ItemType.SPELL, 0, 0,0,0,210,6,"Damages enemy for 30 points and ignores armor");
        itemsRepository.save(spell1);
        Items spell2 = new Items(null, "Weakness", ItemType.SPELL, 0, 0,0,0,210,7,"Decreases enemy attack by 3 points");
        itemsRepository.save(spell2);
        Items spell3 = new Items(null, "Sunder Armor", ItemType.SPELL, 0, 0,0,0,210,7,"Decreases enemy defence by 3 points");
        itemsRepository.save(spell3);
        Items spell4 = new Items(null, "Healing", ItemType.SPELL, 0, 0,0,0,200,6,"Heals up to 25 health");
        itemsRepository.save(spell4);
        Items spell5 = new Items(null, "Death Ray", ItemType.SPELL, 0, 0,0,0,730,15,"Causes heavy damage 100 points to enemy and ignores armor");
        itemsRepository.save(spell5);
        Items spell6 = new Items(null, "Life Steal", ItemType.SPELL, 0, 0,0,0,220,8,"Steals up to 20 health from living enemy and heals you");
        itemsRepository.save(spell6);


        Enemies enemy1 = new Enemies(1L,"Figurine", 60,60,0,4,"Yes, it's a Training figurine...everyone starts somewhere, try not to hit yourself.");
        enemiesRepository.save(enemy1);
        Enemies enemy2 = new Enemies(2L,"Giant rat", 105,105,12,5,"Bigger and more dangerous that standard Rat, but should not be hard to kill.");
        enemiesRepository.save(enemy2);
        Enemies enemy3 = new Enemies(3L,"Goblin", 150,150,16,8,"He is small, mean, armed with mace and can steal your money if you are not carefull.");
        enemiesRepository.save(enemy3);
        Enemies enemy4= new Enemies(4L,"Orc", 235,235,19,10,"This fearless warrior is armed with greatsword and leather armor.");
        enemiesRepository.save(enemy4);
        Enemies enemy5= new Enemies(5L,"Troll", 290,290,22,13,"He stinks, carries big two handed club and if he hits you, his smell will not bother you anymore.  ");
        enemiesRepository.save(enemy5);
        Enemies enemy6= new Enemies(6L,"Elemental", 345,345,26,15,"Being of pure fire, his attacks ignore your armor, and cannot be blocked with normal shield.");
        enemiesRepository.save(enemy6);
        Enemies enemy7= new Enemies(7L,"Minotaur", 380,380,27,16,"Beware his horns, 2-handed axe and RAGE!");
        enemiesRepository.save(enemy7);
        Enemies enemy8= new Enemies(8L,"Behemoth", 435,435,31,19,"He looks angry and his long claws are thirsty for your blood.");
        enemiesRepository.save(enemy8);
        Enemies enemy9= new Enemies(9L,"Medusa", 400,400,30,17,"Once a beautiful woman, now she can turn you into a stone with one look.");
        enemiesRepository.save(enemy9);
        Enemies enemy10= new Enemies(10L,"Lich", 285,285,34,16,"This undead mage ignores your armor, and his attacks cannot be blocked with shield.");
        enemiesRepository.save(enemy10);
        Enemies enemy11= new Enemies(11L,"Dragon", 530,530,42,23,"Winged death, let's hope you are ready.");
        enemiesRepository.save(enemy11);
    }
}