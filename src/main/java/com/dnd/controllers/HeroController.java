package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.services.HeroService;
import com.dnd.services.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/hero")
public class HeroController {

    private final HeroService heroService;
    private final ItemsService itemsService;
    private final Map<Long, Boolean> upgradedItemsMap = new HashMap<>();
    private boolean startingBonusApplied = false;

    @Autowired
    public HeroController(HeroService heroService, ItemsService itemsService) {
        this.heroService = heroService;
        this.itemsService = itemsService;
    }

    @GetMapping
    public String viewHero(Model model) {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            model.addAttribute("hero", hero);
            return "hero";
        } else {
            // Create a new hero with default attributes if no hero exists
            Hero newHero = new Hero();
            heroService.saveHero(newHero);
            model.addAttribute("hero", newHero);
            return "hero";
        }
    }

    @GetMapping("/readmeHero")
    public String showReadmeHeroPage() {
        return "readmeHero";
    }

    @PostMapping("/save")
    public String saveHero(@RequestParam("heroName") String heroName,
            @RequestParam("startingBonus") String startingBonus, Model model) {
        if (heroName.length() > 10) {
            model.addAttribute("heroNameError", "Hero name must be a maximum of 10 characters");
            return "main";
        }
    
        Hero hero = heroService.getYourHero();
        boolean isNewHero = false; // Flag to check if the hero is new
    
        if (hero == null) {
            // Create a new hero with the given name
            hero = new Hero(heroName);
            hero.setGold(710);
            hero.setMana(20);
            hero.setPotion(3);
            hero.setSkillPoints(3);
            hero.setHealth(130);
            hero.setMaxHealth(130);
            hero.setAttack(8);
            hero.setDefense(7);
            hero.setRunes(3);
            isNewHero = true; // Mark the hero as new
        } else {
            hero.setName(heroName);
        }
    
        if (isNewHero) {
            // Apply the chosen starting bonus only for new heroes
            if ("health".equals(startingBonus)) {
                hero.setHealth(hero.getHealth() + 35);
                hero.setMaxHealth(hero.getMaxHealth() + 35);
            } else if ("attack".equals(startingBonus)) {
                hero.setAttack(hero.getAttack() + 2);
            } else if ("mana".equals(startingBonus)) {
                hero.setMana(hero.getMana() + 12);
            }
        }
    
        heroService.saveHero(hero);
        return "redirect:/hero";
    }
    

    private boolean isArmorType(ItemType itemType) {
        Set<ItemType> armorTypes = new HashSet<>(Arrays.asList(ItemType.ARMOR, ItemType.CLOAK, ItemType.GLOVES,
                ItemType.TROUSERS, ItemType.HELMET, ItemType.SHIELD,
                ItemType.BOOTS));
        return armorTypes.contains(itemType);
    }

    @RequestMapping(value = "/upgradeItem/{itemId}", method = { RequestMethod.GET, RequestMethod.POST })
    public String upgradeItem(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            // Fetch the latest hero stats from the database
            hero = heroService.getHeroById(hero.getId()).orElse(null);

            // Check if the hero has at least one rune
            if (hero.getRunes() > 0) {
                Items itemToUpgrade = null;
                List<Items> equippedItems = heroService.getEquippedItems(hero.getId());

                // Find the item in the equippedItems list
                for (Items item : equippedItems) {
                    if (item.getId().equals(itemId)) {
                        itemToUpgrade = item;
                        break;
                    }
                }

                // Upgrade the item if found
                if (itemToUpgrade != null) {

                    // Check the item type and apply upgrades based on the type
                    if (itemToUpgrade.getType() == ItemType.WEAPON) {
                        // Upgrade attack for weapon and specific spell
                        int attackBonusBeforeUpgrade = itemToUpgrade.getAttackBonus();
                        itemToUpgrade.setAttackBonus(itemToUpgrade.getAttackBonus() + 1);
                        hero.setRunes(hero.getRunes() - 1);
                        redirectAttributes.addFlashAttribute("upgradeMessageItem",
                                itemToUpgrade.getName() + " upgraded:\n+1 attack.");

                        // Calculate the attack bonus difference after the upgrade
                        int attackBonusDifference = itemToUpgrade.getAttackBonus() - attackBonusBeforeUpgrade;

                        // Update the hero's attack using the actual current attack from the table plus
                        // the attack bonus difference
                        hero.setAttack(hero.getAttack() + attackBonusDifference);

                    } else if (isArmorType(itemToUpgrade.getType())) {
                        // Check if the hero has already bought and upgraded this item
                        boolean hasUpgradedItem = upgradedItemsMap.getOrDefault(itemId, false);

                        // Store the health bonus before upgrading the armor
                        int healthBonusBeforeUpgrade = itemToUpgrade.getHealthBonus();
                        int defenseBonusBeforeUpgrade = itemToUpgrade.getDefenseBonus();

                        // If the item has not been upgraded, add the full health bonus
                        if (!hasUpgradedItem) {
                            itemToUpgrade.setHealthBonus(itemToUpgrade.getHealthBonus() + 2); // Add the full health
                                                                                              // bonus
                            itemToUpgrade.setDefenseBonus(itemToUpgrade.getDefenseBonus() + 1); // Add the full defense
                                                                                                // bonus
                            upgradedItemsMap.put(itemId, true); // Mark the item as upgraded in the map
                        } else {
                            // If the item has been previously upgraded, add only the upgrade amount
                            itemToUpgrade.setHealthBonus(itemToUpgrade.getHealthBonus() + 2); // Add only the upgrade
                                                                                              // amount for health
                            itemToUpgrade.setDefenseBonus(itemToUpgrade.getDefenseBonus() + 1); // Add only the upgrade
                                                                                                // amount for defense
                        }

                        hero.setRunes(hero.getRunes() - 1);
                        redirectAttributes.addFlashAttribute("upgradeMessageItem",
                                itemToUpgrade.getName() + " upgraded: +1 defense, +2 health.");

                        // Calculate the health bonus difference after the upgrade
                        int healthBonusDifference = itemToUpgrade.getHealthBonus() - healthBonusBeforeUpgrade;

                        // Update the hero's health using the actual current health from the table plus
                        // the health bonus difference
                        hero.setMaxHealth(hero.getMaxHealth() + healthBonusDifference);

                        // Save the updated item using the ItemsService
                        itemsService.saveItem(itemToUpgrade);

                    } else {
                        // For other item types, upgrading is not allowed, so do nothing
                    }

                    // Save the updated item using the ItemsService
                    itemsService.saveItem(itemToUpgrade);

                    // Update the hero's stats based on equipped items
                    hero.updateStatsUpgrade();
                    heroService.saveHero(hero);
                } else {
                    // Item not found in equippedItems list, handle this case accordingly
                    redirectAttributes.addFlashAttribute("upgradeMessageItem", "Item not found for upgrade.");
                }
            } else {
                // Hero does not have enough runes, set an appropriate message
                redirectAttributes.addFlashAttribute("upgradeMessageItem", "Not enough runes.");
            }
        }
        return "redirect:/hero";
    }
}