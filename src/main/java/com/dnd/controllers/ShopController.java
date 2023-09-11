package com.dnd.controllers;

import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.models.Shop;
import com.dnd.repositories.HeroRepository;
import com.dnd.repositories.ItemsRepository;
import com.dnd.services.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    @GetMapping("/readmeShop")
    public String showReadmeShopPage() {
        return "readmeShop";
    }

    @GetMapping("/dice")
    public String playDice(Model model) {
        Hero hero = heroService.getYourHero();
    
        if (hero == null) {
            return "error";
        }
    
        // Set the hero's gold in the model
        model.addAttribute("heroGold", hero.getGold());
    
        return "dice";
    }
    

    @PostMapping("/dice")
    public String playDiceGame(Model model, @RequestParam int betAmount) {
        Hero hero = heroService.getYourHero();

        if (hero == null) {
          
            return "error"; 
        }

        int heroGold = hero.getGold();

        // Check if the bet amount is valid (not negative and not more than hero's gold)
        if (betAmount <= 0 || betAmount > heroGold) {
            model.addAttribute("errorMessage", "Not enough gold to bet!");
            model.addAttribute("heroGold", heroGold); // Add the hero's gold to the model

            // Fetch the latest items from the database
            List<Items> availableItems = (List<Items>) itemsRepository.findAll();
            model.addAttribute("items", availableItems);

            return "dice";
        }

        int heroDice1 = (int) (Math.random() * 6) + 1;
        int heroDice2 = (int) (Math.random() * 6) + 1;
        int computerDice1 = (int) (Math.random() * 6) + 2;
        int computerDice2 = (int) (Math.random() * 6) + 1;

        int playerTotal = heroDice1 + heroDice2;
        int computerTotal = computerDice1 + computerDice2;

        // Determine the winner
        if (playerTotal > computerTotal) {
            int winnings = betAmount;
            hero.setGold(heroGold + winnings); // Increase hero's gold
            model.addAttribute("heroRoll1", heroDice1);
            model.addAttribute("heroRoll2", heroDice2);
            model.addAttribute("computerRoll1", computerDice1);
            model.addAttribute("computerRoll2", computerDice2);
            model.addAttribute("heroGold", hero.getGold());
            model.addAttribute("gameResult", "You win " + winnings + " gold!");
            heroRepository.save(hero);
        } else if (playerTotal < computerTotal) {
            model.addAttribute("heroRoll1", heroDice1);
            model.addAttribute("heroRoll2", heroDice2);
            model.addAttribute("computerRoll1", computerDice1);
            model.addAttribute("computerRoll2", computerDice2);
            hero.setGold(heroGold - betAmount); // Decrease hero's gold
            model.addAttribute("heroGold", hero.getGold());
            model.addAttribute("gameResult", "Computer wins! You lost " + betAmount + " gold!");
            heroRepository.save(hero);
        } else {
            model.addAttribute("heroRoll1", heroDice1);
            model.addAttribute("heroRoll2", heroDice2);
            model.addAttribute("computerRoll1", computerDice1);
            model.addAttribute("computerRoll2", computerDice2);
            model.addAttribute("heroGold", hero.getGold());
            model.addAttribute("gameResult", "It's a tie!");
            heroRepository.save(hero);
        }

        // Fetch the latest items from the database
        List<Items> availableItems = (List<Items>) itemsRepository.findAll();
        model.addAttribute("items", availableItems);

        return "dice"; // Return to the shop template with the game result
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

    @PostMapping("/get/shop/buy/{itemId}")
    public ResponseEntity buyItem(@PathVariable Long itemId) {
        var response = new HashMap<String, Object>();

        Items itemToBuy = itemsRepository.findById(itemId).orElse(null);
        if (itemToBuy != null) {
            Hero hero = heroService.getYourHero();
            if (hero != null) {
                // Check if the hero already has the same spell equipped
                if (itemToBuy.getType() == ItemType.SPELL) {
                    boolean hasSameSpell = hero.getEquippedItems().stream()
                            .anyMatch(existingSpell -> existingSpell.getName().equals(itemToBuy.getName()));

                    if (hasSameSpell) {
                        response.put("message", "Spell learned already!");
                    } else {
                        // Buy the spell and update the hero's equipment
                        int heroGold = hero.getGold();
                        int itemPrice = itemToBuy.getPrice();

                        if (heroGold >= itemPrice) {
                            hero.setGold(heroGold - itemPrice);
                            hero.equipItem(itemToBuy);
                            heroService.updateHeroById(hero.getId(), hero);
                            response.put("message", "Item purchased!");
                        } else {
                            response.put("message", "Not enough gold!");
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
                            response.put("message", "You already have that item!");
                            // Fetch the latest items from the database
                            List<Items> availableItems = (List<Items>) itemsRepository.findAll();
                            // Add the available items to the model
                            response.put("items", availableItems);
                            response.put("heroGold", hero.getGold());
                            return ResponseEntity.ok(response);
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
                            hero.setHealingPotion(hero.getHealingPotion() + 1);
                        } else if (itemToBuy.getName().equals("Mana Potion")) {
                            hero.setManaPotion(hero.getManaPotion() + 1);
                        } else {
                            hero.equipItem(itemToBuy);
                        }

                        hero.setGold(heroGold - itemPrice);
                        heroService.updateHeroById(hero.getId(), hero);
                        response.put("message", "Item purchased!");
                    } else {
                        response.put("message", "Not enough gold!");
                    }
                }
            } else {
                response.put("message", "Hero not found!");
            }
        } else {
            response.put("message", "Item not found!");
        }

        // Fetch the latest items from the database
        List<Items> availableItems = (List<Items>) itemsRepository.findAll();
        // Add the available items to the model
        response.put("items", availableItems);

        Hero updatedHero = heroService.getYourHero();
        if (updatedHero != null) {
            response.put("heroGold", updatedHero.getGold());
        }

        return ResponseEntity.ok(response);
    }

}
