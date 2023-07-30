package com.dnd.models;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "hero")
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int health;
    private int attack;
    private int defense;
    private int gold;
    private int potion;
    private int spell;
    private int mana;

    private int skillPoints;
    private int permanentHealthUpgrades;
    private int permanentAttackUpgrades;
    private int permanentDefenseUpgrades;
    private boolean hasShield;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "hero_equipped_items",
            joinColumns = @JoinColumn(name = "hero_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Items> equippedItems = new ArrayList<>();

    public List<Items> getEquippedItems() {
        return equippedItems;
    }

    public void setEquippedItems(List<Items> equippedItems) {
        this.equippedItems = equippedItems;
    }

    public void equipItem(Items item) {
        // Check if the item is not already equipped
        if (!equippedItems.contains(item)) {
            if (item.getType() == ItemType.SPELL) {
                // For spells, check if the hero already has a spell with the same name
                boolean hasSameSpell = equippedItems.stream()
                        .anyMatch(existingSpell -> existingSpell.getName().equals(item.getName()));

                if (!hasSameSpell) {
                    equippedItems.add(item);
                }
            } else {
                // For non-spell items, check for duplicates and equip the item
                Items existingItemOfType = equippedItems.stream()
                        .filter(existingItem -> existingItem.getType() == item.getType())
                        .findFirst()
                        .orElse(null);

                if (existingItemOfType != null) {
                    // Unequip the existing item of the same type
                    unequipItem(existingItemOfType);
                }
                equippedItems.add(item);
            }

            updateStats();
        }
    }



    public void unequipItem(Items item) {
        equippedItems.remove(item);
        updateStats();
    }

    public void updateStats() {
        int totalAttackBonus = 0;
        int totalDefenseBonus = 0;
        int totalHealthBonus = 0;

        // Calculate total bonuses from equipped items
        for (Items item : equippedItems) {
            totalAttackBonus += item.getAttackBonus();
            totalDefenseBonus += item.getDefenseBonus();
            totalHealthBonus += item.getHealthBonus();
        }

        // Include permanent upgrades in the stats calculation
        totalAttackBonus += permanentAttackUpgrades;
        totalDefenseBonus += permanentDefenseUpgrades;
        totalHealthBonus += permanentHealthUpgrades;

        // Update the hero's attack, defense, and health stats
        this.attack = getBaseAttack() + totalAttackBonus;
        this.defense = getBaseDefense() + totalDefenseBonus;
        this.health = getBaseHealth() + totalHealthBonus;
    }

    public Hero() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getPotion() {
        return potion;
    }

    public void setPotion(int potion) {
        this.potion = potion;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }
    public int getBaseAttack() {
        return 8;
    }

    public int getBaseDefense() {
        return 6;
    }

    public int getBaseHealth() {
        return 125;
    }
    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getPermanentHealthUpgrades() {
        return permanentHealthUpgrades;
    }
    public void setPermanentHealthUpgrades(int permanentHealthUpgrades) {
        this.permanentHealthUpgrades = permanentHealthUpgrades;
    }

    public int getPermanentAttackUpgrades() {
        return permanentAttackUpgrades;
    }

    public void setPermanentAttackUpgrades(int permanentAttackUpgrades) {
        this.permanentAttackUpgrades = permanentAttackUpgrades;
    }

    public int getPermanentDefenseUpgrades() {
        return permanentDefenseUpgrades;
    }

    public void setPermanentDefenseUpgrades(int permanentDefenseUpgrades) {
        this.permanentDefenseUpgrades = permanentDefenseUpgrades;
    }
    public boolean hasShield() {
        return hasShield;
    }

    public void setHasShield(boolean hasShield) {
        this.hasShield = hasShield;
    }


    public Hero(String name) {
        this.name = name;
    }

    public void upgradeHealth() {
        if (skillPoints > 0) {
            health += 20;
            permanentHealthUpgrades += 20;
            skillPoints -= 1;
        }
    }
    public void upgradeMana() {
        if (skillPoints > 0) {
            mana += 5;
            skillPoints -= 1;
        }
    }

    public void upgradeAttack() {
        if (skillPoints > 0) {
            attack += 1;
            permanentAttackUpgrades += 1;
            skillPoints -= 1;
        }
    }

    public void upgradeDefense() {
        if (skillPoints > 0) {
            defense += 1;
            permanentDefenseUpgrades += 1;
            skillPoints -= 1;
        }
    }

}