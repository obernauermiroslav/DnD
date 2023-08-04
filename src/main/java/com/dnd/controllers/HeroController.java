package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.repositories.HeroRepository;
import com.dnd.services.HeroService;
import com.dnd.services.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/hero")
public class HeroController {

    private final HeroService heroService;
    private final ItemsService itemsService;

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

    @PostMapping("/save")
    public String saveHero(@RequestParam("heroName") String heroName) {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            hero.setName(heroName);
        } else {
            // Create a new hero with the given name
            hero = new Hero(heroName);
            hero.setGold(600);
            hero.setMana(10);
            hero.setPotion(3);
            hero.setSkillPoints(3);
            hero.setHealth(130);
            hero.setAttack(8);
            hero.setDefense(7);
            hero.setRunes(1);
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

    @RequestMapping(value = "/upgradeItem/{itemId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String upgradeItem(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            // Check if the hero has at least one rune
            if (hero.getRunes() > 0) {
                Items itemToUpgrade = null;
                // Find the item in the equippedItems list
                for (Items item : hero.getEquippedItems()) {
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
                        itemToUpgrade.setAttackBonus(itemToUpgrade.getAttackBonus() + 1);
                        hero.setRunes(hero.getRunes() - 1);
                        redirectAttributes.addFlashAttribute("upgradeMessageItem", "Weapon upgraded.");
                    } else if (isArmorType(itemToUpgrade.getType())) {
                        // Upgrade health and defense for armor, cloak, gloves, trousers, helmet, shield, boots
                        itemToUpgrade.setHealthBonus(itemToUpgrade.getHealthBonus() + 2);
                        itemToUpgrade.setDefenseBonus(itemToUpgrade.getDefenseBonus() + 1);
                        hero.setRunes(hero.getRunes() - 1);
                        redirectAttributes.addFlashAttribute("upgradeMessageItem", "Item upgraded.");
                    } else {
                        // For other item types, upgrading is not allowed, so do nothing
                    }

                    // Save the updated item using the ItemsService
                    itemsService.saveItem(itemToUpgrade);

                    // Update the hero's stats based on equipped items
                    heroService.updateHeroStats(hero);

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
