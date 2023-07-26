package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.models.Shop;
import com.dnd.repositories.HeroRepository;
import com.dnd.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ShopController {

    @Autowired
    private ItemsRepository itemsRepository;
    private final HeroRepository heroRepository;
    private final Shop shop;
    private boolean isShopInitialized = false;

    @Autowired
    public ShopController(ItemsRepository itemsRepository, HeroRepository heroRepository) {
        this.itemsRepository = itemsRepository;
        this.heroRepository = heroRepository;
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
        if (!isShopInitialized) {
            initializeShop();
            isShopInitialized = true;
        }

        // Get the available items from the shop
        List<Items> availableItems = shop.getAvailableItems();

        // Add the available items to the model
        model.addAttribute("items", availableItems);

        // Get the hero from the repository
        Hero hero = heroRepository.findByName("hero");
        if (hero != null) {
            // Add the hero's gold to the model
            model.addAttribute("heroGold", hero.getGold());
        } else {
            // Create a new Hero if not found in the repository
            Hero newHero = new Hero();
            // Set an initial gold value
            newHero.setGold(0);
            // Save the newHero to the repository
            heroRepository.save(newHero);
            // Add the newHero's gold to the model
            model.addAttribute("heroGold", newHero.getGold());
        }

        // Return the shop view
        return "shop";
    }

    @PostMapping("/shop/buy/{itemId}")
    public String buyItem(@PathVariable Long itemId, Model model) {
        Items itemToBuy = itemsRepository.findById(itemId).orElse(null);
        if (itemToBuy != null) {
            Hero hero = heroRepository.findByName("hero");
            if (hero != null) {
                // Check if the hero already has an item of the same type equipped
                ItemType itemType = itemToBuy.getType();

                Items existingItemOfType = hero.getEquippedItems().stream()
                        .filter(item -> item.getType() == itemType)
                        .findFirst()
                        .orElse(null);

                if (existingItemOfType != null) {
                    // Hero already has an item of the same type equipped
                    // Check if the names of the items are different
                    if (!existingItemOfType.getName().equals(itemToBuy.getName())) {
                        // Names are different, replace the old item with the new one
                        hero.unequipItem(existingItemOfType);
                    } else {
                        model.addAttribute("message", "You already have that item!");
                        // Show the updated shop with available items
                        List<Items> availableItems = shop.getAvailableItems();
                        model.addAttribute("items", availableItems);
                        model.addAttribute("heroGold", hero.getGold());
                        return "shop";
                    }
                }

                // Check if the hero has enough currency (gold) to buy the item
                int heroGold = hero.getGold();
                int itemPrice = itemToBuy.getPrice();

                if (heroGold >= itemPrice) {
                    // Reduce the hero's gold by the item price
                    hero.setGold(heroGold - itemPrice);

                    // If the purchased item is a healing potion, increase the potion count
                    if (itemToBuy.getName().equals("Healing Potion")) {
                        hero.setPotion(hero.getPotion() + 1);
                    } else {
                        // If the purchased item is not a healing potion, equip the item
                        hero.equipItem(itemToBuy);
                    }

                    heroRepository.save(hero);
                    model.addAttribute("message", "Item purchased successfully!");
                } else {
                    model.addAttribute("message", "Not enough gold to buy the item!");
                }
            } else {
                model.addAttribute("message", "Hero not found!");
            }
        } else {
            model.addAttribute("message", "Item not found!");
        }

        // Get the updated hero from the repository
        Hero updatedHero = heroRepository.findByName("hero");
        if (updatedHero != null) {
            // Add the updated hero's gold to the model
            model.addAttribute("heroGold", updatedHero.getGold());
        }

        // Show the updated shop with available items
        List<Items> availableItems = shop.getAvailableItems();
        model.addAttribute("items", availableItems);

        return "shop";
    }
}