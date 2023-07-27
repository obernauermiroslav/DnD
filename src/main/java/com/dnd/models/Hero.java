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
        return 7;
    }

    public int getBaseDefense() {
        return 5;
    }

    public int getBaseHealth() {
        return 100;
    }

    public Hero(String name) {
        this.name = name;
        this.health = 100;
        this.attack = 7;
        this.defense = 5;
        this.gold = 500;
        this.potion = 2;
        this.mana = 10;
    }
    }