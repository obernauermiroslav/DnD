package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.models.Shop;
import com.dnd.repositories.HeroRepository;
import com.dnd.repositories.ItemsRepository;
import com.dnd.services.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class ShopController {

    private final ItemsRepository itemsRepository;
    private final HeroRepository heroRepository;
    private final HeroService heroService;
    private final Shop shop;
    private boolean isShopInitialized = false;

    @Autowired
    public ShopController(ItemsRepository itemsRepository, HeroRepository heroRepository, HeroService heroService) {
        this.itemsRepository = itemsRepository;
        this.heroRepository = heroRepository;
        this.heroService = heroService;
        this.shop = new Shop();
        initializeShop();
    }

    // Method to initialize the shop with some available items
    private void initializeShop() {
        List<Items> allItems = new ArrayList<>();
        itemsRepository.findAll().forEach(allItems::add);
        for (Items item : allItems) {
            shop.addItem(item);
        }
    }


    @GetMapping("/shop")
    public String viewShop(Model model) {
        // Fetch the latest items from the database
        List<Items> availableItems = (List<Items>) itemsRepository.findAll();
        // Add the available items to the model
        model.addAttribute("items", availableItems);

        // Get the hero from the service
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            // Add the hero's gold to the model
            model.addAttribute("heroGold", hero.getGold());
            model.addAttribute("equippedItems", hero.getEquippedItems());
            model.addAttribute("hero", hero);
        } else {
            // Create a new Hero if not found in the service
            Hero newHero = new Hero();
            // Set an initial gold value
            newHero.setGold(0);
            // Save the newHero to the service
            heroService.saveHero(newHero);
            // Add the newHero's gold to the model
            model.addAttribute("heroGold", newHero.getGold());
            model.addAttribute("hero", newHero); // Note: Update to newHero
        }

        // Return the shop view
        return "shop";
    }


    @GetMapping("/api/equipped-items")
    @ResponseBody
    public List<Items> getEquippedItems() {
        Hero hero = heroService.getYourHero();
        if (hero != null) {
            return hero.getEquippedItems();
        }
        return Collections.emptyList();
    }

    @PostMapping("/shop/buy/{itemId}")
    public String buyItem(@PathVariable Long itemId, Model model) {
        Items itemToBuy = itemsRepository.findById(itemId).orElse(null);
        if (itemToBuy != null) {
            Hero hero = heroService.getYourHero();
            if (hero != null) {
                // Check if the hero already has the same spell equipped
                if (itemToBuy.getType() == ItemType.SPELL) {
                    boolean hasSameSpell = hero.getEquippedItems().stream()
                            .anyMatch(existingSpell -> existingSpell.getName().equals(itemToBuy.getName()));

                    if (hasSameSpell) {
                        model.addAttribute("message", "Spell learned already!");
                    } else {
                        // Buy the spell and update the hero's equipment
                        int heroGold = hero.getGold();
                        int itemPrice = itemToBuy.getPrice();

                        if (heroGold >= itemPrice) {
                            hero.setGold(heroGold - itemPrice);
                            hero.equipItem(itemToBuy);
                            heroService.updateHeroById(hero.getId(), hero);
                            model.addAttribute("message", "Item purchased!");
                        } else {
                            model.addAttribute("message", "Not enough gold!");
                        }
                    }
                } else {
                    // For non-spell items, proceed as before
                    Items existingItemOfType = hero.getEquippedItems().stream()
                            .filter(item -> item.getType() == itemToBuy.getType())
                            .findFirst()
                            .orElse(null);

                    if (existingItemOfType != null) {
                        if (!existingItemOfType.getName().equals(itemToBuy.getName())) {
                            hero.unequipItem(existingItemOfType);
                        } else {
                            model.addAttribute("message", "You already have that item!");
                            // Fetch the latest items from the database
                            List<Items> availableItems = (List<Items>) itemsRepository.findAll();
                            // Add the available items to the model
                            model.addAttribute("items", availableItems);
                            model.addAttribute("heroGold", hero.getGold());
                            return "shop";
                        }
                    }

                    int heroGold = hero.getGold();
                    int itemPrice = itemToBuy.getPrice();

                    if (heroGold >= itemPrice) {
                        // Update hero's equipment and gold
                        if (itemToBuy.getType() == ItemType.SHIELD) {
                            // Set hasShield to true since the hero bought a shield
                            hero.setHasShield(true);
                            hero.equipItem(itemToBuy);
                        } else if (itemToBuy.getName().equals("Healing Potion")) {
                            hero.setPotion(hero.getPotion() + 1);
                        } else {
                            hero.equipItem(itemToBuy);
                        }

                        hero.setGold(heroGold - itemPrice);
                        heroService.updateHeroById(hero.getId(), hero);
                        model.addAttribute("message", "Item purchased!");
                    } else {
                        model.addAttribute("message", "Not enough gold!");
                    }
                }
            } else {
                model.addAttribute("message", "Hero not found!");
            }
        } else {
            model.addAttribute("message", "Item not found!");
        }

        // Fetch the latest items from the database
        List<Items> availableItems = (List<Items>) itemsRepository.findAll();
        // Add the available items to the model
        model.addAttribute("items", availableItems);

        Hero updatedHero = heroService.getYourHero();
        if (updatedHero != null) {
            model.addAttribute("heroGold", updatedHero.getGold());
        }

        return "shop";
    }

}
