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
    private int maxHealth;
    private int attack;
    private int defense;
    private int gold;
    private int healingPotion;
    private int manaPotion;
    private int spell;
    private int mana;
    private int runes;
    private int skillPoints;
    private int permanentHealthUpgrades;
    private int permanentAttackUpgrades;
    private int permanentDefenseUpgrades;
    private int permanentManaUpgrades;
    private boolean hasShield;
    private int baseAttack = 8;
    private int baseDefense = 7;

    @Transient
    private Set<Long> purchasedItems = new HashSet<>();

    @Transient
    private Map<ItemType, Integer> itemTypeHealthBonuses = new HashMap<>();
    @Transient
    private Items newlyPurchasedItem; // New field to store the newly purchased item

    public Items getUpgradedWeapon() {
        return upgradedWeapon;
    }

    public void setUpgradedWeapon(Items upgradedWeapon) {
        this.upgradedWeapon = upgradedWeapon;
    }

    @Transient
    private Items upgradedWeapon;

    public void setNewlyPurchasedItem(Items newlyPurchasedItem) {
        this.newlyPurchasedItem = newlyPurchasedItem;
    }

    @Transient
    private Set<Long> upgradedItemIds = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "hero_equipped_items", joinColumns = @JoinColumn(name = "hero_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
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
                    purchasedItems.add(item.getId()); // Mark item as purchased
                    updateStats();
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
                // Update health bonus for the item type
                int itemTypeHealthBonus = itemTypeHealthBonuses.getOrDefault(item.getType(), 0);
                itemTypeHealthBonus += item.getHealthBonus();
                itemTypeHealthBonuses.put(item.getType(), itemTypeHealthBonus);

                equippedItems.add(item);
                purchasedItems.add(item.getId()); // Mark item as purchased
                updateStats();
            }
        }
    }

    public void unequipItem(Items item) {
        equippedItems.remove(item);

        int itemTypeHealthBonus = itemTypeHealthBonuses.getOrDefault(item.getType(), 0);
        itemTypeHealthBonus -= item.getHealthBonus();
        itemTypeHealthBonuses.put(item.getType(), itemTypeHealthBonus);

        updateStats();
    }

    public void updateStats() {
        int totalAttackBonus = 0;
        int totalDefenseBonus = 0;
        int totalHealthBonus = 0;
        int totalManaBonus = 0;

        // Calculate total bonuses from equipped items
        for (Items item : equippedItems) {
            totalAttackBonus += item.getAttackBonus();
            totalDefenseBonus += item.getDefenseBonus();
            totalHealthBonus += itemTypeHealthBonuses.getOrDefault(item.getType(), 0);

            // Check if the item has already been purchased to avoid adding the mana bonus
            // again
            if (purchasedItems.contains(item.getId())) {
                totalManaBonus += item.getManaBonus();
            }
        }

        // Include permanent upgrades in the stats calculation
        totalAttackBonus += permanentAttackUpgrades;
        totalDefenseBonus += permanentDefenseUpgrades;
        totalHealthBonus += permanentHealthUpgrades;

        // Update the hero's attack, defense, and health stats
        this.attack = getBaseAttack() + totalAttackBonus;
        this.defense = getBaseDefense() + totalDefenseBonus;
        maxHealth = getMaxHealth() + totalHealthBonus;
        this.mana = getMana() + totalManaBonus;
    }

    public void updateStatsUpgrade() {
        int totalAttackBonus = 0;
        int totalDefenseBonus = 0;
        int totalHealthBonus = 0;

        // Calculate total bonuses from equipped items
        for (Items item : equippedItems) {
            totalAttackBonus += item.getAttackBonus();
            totalDefenseBonus += item.getDefenseBonus();

            // Check if the item has already been purchased to avoid adding full health and
            // mana bonuses again
            if (purchasedItems.contains(item.getId())) {
                totalHealthBonus += item.getHealthBonus();
            }
        }

        // Include permanent upgrades in the stats calculation
        totalAttackBonus += permanentAttackUpgrades;
        totalDefenseBonus += permanentDefenseUpgrades;

        // If a weapon has been upgraded, subtract the old attack bonus and add the new
        // bonus
        if (upgradedWeapon != null) {
            totalAttackBonus -= upgradedWeapon.getAttackBonus();
            totalAttackBonus += upgradedWeapon.getAttackBonus();
        }

        // Update the hero's attack and defense stats
        this.attack = getBaseAttack() + totalAttackBonus;
        this.defense = getBaseDefense() + totalDefenseBonus;

        // Update the hero's health stat using the actual current health from the table
        // plus the total health bonus
        this.health = getHealth();

        // Reset the newlyPurchasedItem after updating stats
        newlyPurchasedItem = null;
    }

    public boolean hasUpgradedItem(Long itemId) {
        return upgradedItemIds.contains(itemId);
    }

    public void markItemAsUpgraded(Long itemId) {
        upgradedItemIds.add(itemId);
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
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

    public int getHealingPotion() {
        return healingPotion;
    }

    public void setHealingPotion(int healingPotion) {
        this.healingPotion = healingPotion;
    }

    public int getManaPotion() {
        return manaPotion;
    }

    public void setManaPotion(int manaPotion) {
        this.manaPotion = manaPotion;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setBaseAttack(int baseAttack) {
        this.baseAttack = baseAttack;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public void setBaseDefense(int baseDefense) {
        this.baseDefense = baseDefense;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseHealth() {
        return health;
    }

    public int getBaseMana() {
        return mana;
    }

    public int calculateMaxHealth() {
        int totalHealthBonus = 0;

        // Calculate total health bonus from equipped items
        for (Items item : equippedItems) {
            totalHealthBonus += itemTypeHealthBonuses.getOrDefault(item.getType(), 0);
        }

        // Include permanent health upgrades
        totalHealthBonus += permanentHealthUpgrades;

        // Calculate and return the initial max health value
        return getMaxHealth() + totalHealthBonus;
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

    public int getPermanentManaUpgrades() {
        return permanentManaUpgrades;
    }

    public void setPermanentManaUpgrades(int permanentManaUpgrades) {
        this.permanentManaUpgrades = permanentManaUpgrades;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void setHasShield(boolean hasShield) {
        this.hasShield = hasShield;
    }

    public boolean hasFireShield() {
        for (Items item : equippedItems) {
            if (item.getType() == ItemType.SHIELD && item.getName().equalsIgnoreCase("Fire Shield")) {
                return true;
            }
        }
        return false;
    }

    public int getRunes() {
        return runes;
    }

    public void setRunes(int runes) {
        this.runes = runes;
    }

    public Hero(String name) {
        this.name = name;
    }

    public void upgradeHealth() {
        if (skillPoints > 0) {
            maxHealth += 30;
            health += 30;
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