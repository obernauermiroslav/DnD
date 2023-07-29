package com.dnd.controllers;

import com.dnd.models.Enemies;
import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.services.EnemiesService;
import com.dnd.services.HeroService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class GameController {

    private final HeroService heroService;
    private final EnemiesService enemiesService;
    @Autowired
    private HttpSession session;

    @Autowired
    public GameController(HeroService heroService, EnemiesService enemiesService) {
        this.heroService = heroService;
        this.enemiesService = enemiesService;
    }

    @GetMapping("/")
    public String startNewGame() {
        // Invalidate the session to start a new game
        session.invalidate();
        return "main"; // Return the name of the main.html template
    }

    @GetMapping("/fight")
    public String showFightPage(Model model, HttpSession session) {
        // Load your hero from the hero table
        Hero hero = heroService.getYourHero();
        model.addAttribute("hero", hero);

        // Check if the session contains the current enemy ID
        Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
        if (currentEnemyId == null) {
            // If the current enemy ID is not in the session, get the first enemy
            Enemies firstEnemy = enemiesService.getFirstEnemy();
            if (firstEnemy != null) {
                currentEnemyId = firstEnemy.getId();
                session.setAttribute("currentEnemyId", currentEnemyId);
            } else {
                // Handle the case where there are no enemies in the database
                model.addAttribute("errorMessage", "No enemies found!");
                return "error-page"; // Return the error-page template
            }
        }

        // Use the current enemy ID to get the enemy
        Enemies enemy = enemiesService.getEnemyById(currentEnemyId);

        // If the enemy is null or there are no more enemies to fight, display the victory message
        if (enemy == null) {
            model.addAttribute("noMoreEnemies", true);
            model.addAttribute("fightResult", "You have won the game! Congratulations!");
            return "fight";
        }

        model.addAttribute("enemy", enemy);
        return "fight";
    }

    @PostMapping("/fight")
    public String fight(@RequestParam("heroId") Long heroId,
                        @RequestParam("enemyId") Long enemyId,
                        Model model,
                        HttpSession session) {
        // Load your hero and the enemy from their respective tables using the IDs received from the form
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        Enemies enemy = enemiesService.getEnemyById(enemyId);
        if (hero == null || enemy == null) {
            model.addAttribute("errorMessage", "Hero or enemy not found");
            return "error-page";
        }

        // Perform the fight logic
        int heroAttack = hero.getAttack() - enemy.getDefence();
        int enemyAttack = enemy.getAttack() - hero.getDefense();

        // Check if the attack is ineffective and set it to zero
        heroAttack = Math.max(heroAttack, 0);
        enemyAttack = Math.max(enemyAttack, 0);

        int newEnemyHealth = enemy.getHealth() - heroAttack;
        int newHeroHealth = hero.getHealth() - enemyAttack;

        // Update the health of the hero and the enemy
        hero.setHealth(Math.max(newHeroHealth, 0));
        enemy.setHealth(newEnemyHealth);

        // Save the updated hero and enemy data back to their respective tables
        heroService.saveHero(hero);
        enemiesService.saveEnemy(enemy);

        // Set the hero and enemy as model attributes to display their updated health on the fight page
        model.addAttribute("hero", hero);
        model.addAttribute("enemy", enemy);
        model.addAttribute("heroAttack", heroAttack);
        model.addAttribute("enemyAttack", enemyAttack);

        // Determine the fight result message based on hero's and enemy's health after the fight
        String fightResult;
        if (newEnemyHealth <= 0) {
            hero.setGold(hero.getGold() + 150);
            hero.setHealth(hero.getHealth() + 100);
            hero.setMana(hero.getMana() + 5);
            hero.setSkillPoints(hero.getSkillPoints() + 2);
            heroService.saveHero(hero);

            // Remove the defeated enemy from the database
            enemiesService.deleteEnemy(enemy);

            // Get the next enemy for the next fight
            Enemies nextEnemy = enemiesService.getNextEnemy();
            if (nextEnemy != null) {
                session.setAttribute("currentEnemyId", nextEnemy.getId());
                model.addAttribute("enemy", nextEnemy);
                return "hero";
            } else {
                // Handle the case where there are no more enemies in the database
                fightResult = "You have won the game! Congratulations!";
                model.addAttribute("noMoreEnemies", true);
            }
        } else if (newHeroHealth <= 0) {
            fightResult = "You have lost!";
        } else {
            fightResult = "";
        }
        model.addAttribute("fightResult", fightResult);

        // Set attack messages based on the fight outcome
        model.addAttribute("heroAttackMessage", heroAttack > 0 ? "Your hero attacks for " + heroAttack + " damage!" : "");
        model.addAttribute("enemyAttackMessage", enemyAttack > 0 ? "Enemy attacks for " + enemyAttack + " damage!" : "");

        model.addAttribute("ineffectiveAttackMessage", heroAttack <= 0 ? "Your hero's attack is ineffective!" : "");
        model.addAttribute("ineffectiveAttackMessage", enemyAttack == 0 ? "Enemy's attack is ineffective!" : "");

        return "fight";
    }

    @PostMapping("/heal")
    public String healHero(@RequestParam("heroId") Long heroId,
                           Model model,
                           HttpSession session) {
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        if (hero == null) {
            model.addAttribute("message", "Hero not found");
            return "Hero not found";
        }

        // Check if the hero has at least one healing potion
        int potionCount = hero.getPotion();
        if (potionCount > 0) {
            // Heal the hero
            hero.setHealth(hero.getHealth() + 40);

            // Reduce the number of healing potions by 1
            hero.setPotion(potionCount - 1);

            // Update the hero's stats in the database
            heroService.saveHero(hero);

            model.addAttribute("healMessage", "Healing potion gives you 40 health.");
        } else {
            model.addAttribute("healMessage", "Hero does not have any healing potion.");
        }

        // Load the enemy from the service and add it to the model
        Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
        if (currentEnemyId != null) {
            Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
            model.addAttribute("enemy", enemy);
        }

        model.addAttribute("hero", hero);
        return "fight";
    }

    @PostMapping("/weakness")
    public String castWeakness(@RequestParam("heroId") Long heroId, Model model, HttpSession session) {
        // Load the hero from the database using the heroId received from the form
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        if (hero == null) {
            model.addAttribute("errorMessage", "Hero not found");
            return "error-page";
        }

        // Check if the hero has equipped the "Weakness" spell
        boolean hasWeaknessSpell = hero.getEquippedItems().stream()
                .anyMatch(item -> item.getName().equals("Weakness") && item.getType() == ItemType.SPELL);

        // If the hero has the "Weakness" spell equipped, perform the spell casting logic
        if (hasWeaknessSpell) {
            int weaknessManaCost = 5; // Define the mana cost for Weakness spell
            if (hero.getMana() >= weaknessManaCost) {
                // Deduct the mana cost from the hero's total mana points
                hero.setMana(hero.getMana() - weaknessManaCost);
                heroService.saveHero(hero);

                Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
                if (currentEnemyId != null) {
                    Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
                    if (enemy != null) {
                        int spellDamage = 3;
                        int newEnemyAttack = enemy.getAttack() - spellDamage;

                        // Ensure the attack doesn't go below zero
                        if (newEnemyAttack >= 0) {
                            enemy.setAttack(newEnemyAttack);
                            enemiesService.saveEnemy(enemy);

                            // Set the spell casting result message in the model to display it in the fight page
                            model.addAttribute("spellCastingResult", "You cast Weakness. Enemy's attack decreased by " + spellDamage + " points!");
                        } else {
                            model.addAttribute("spellCastingResult", "No enemy to cast the spell on.");
                        }
                        // Set the enemy attribute in the session to be used in the fight.html template
                        session.setAttribute("currentEnemy", enemy);
                    } else {
                        model.addAttribute("spellCastingResult", "No enemy to cast the spell on.");
                    }
                } else {
                    model.addAttribute("spellCastingResult", "No enemy to cast the spell on.");
                }
            } else {
                model.addAttribute("spellCastingResult", "Not enough mana to cast Weakness.");
            }
        } else {
            model.addAttribute("spellCastingResult", "You don't have the Weakness spell equipped.");
        }

        // Load the enemy from the service and add it to the model
        Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
        if (currentEnemyId != null) {
            Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
            model.addAttribute("enemy", enemy);
        }

        model.addAttribute("hero", hero);
        return "fight";
    }


    @PostMapping("/firebolt")
    public String castFirebolt(@RequestParam("heroId") Long heroId,
                            Model model,
                            HttpSession session) {
        // Load the hero from the database using the heroId received from the form
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        if (hero == null) {
            model.addAttribute("errorMessage", "Hero not found");
            return "error-page";
        }

        // Check if the hero has equipped the "Fire bolt" spell
        boolean hasFireBoltSpell = hero.getEquippedItems().stream()
                .anyMatch(item -> item.getName().equals("Fire bolt") && item.getType() == ItemType.SPELL);

        // If the hero has the "Fire bolt" spell equipped, perform the spell casting logic
        if (hasFireBoltSpell) {
            int fireBoltManaCost = 5; // Define the mana cost for Fire bolt spell
            if (hero.getMana() >= fireBoltManaCost) {
                // Deduct the mana cost from the hero's total mana points
                hero.setMana(hero.getMana() - fireBoltManaCost);
                heroService.saveHero(hero);

                Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
                if (currentEnemyId != null) {
                    Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
                    if (enemy != null) {
                        int spellDamage = 20;
                        int newEnemyHealth = enemy.getHealth() - spellDamage;

                        // Ensure the health doesn't go below zero
                        if (newEnemyHealth <= 0) {
                            hero.setGold(hero.getGold() + 150);
                            hero.setHealth(hero.getHealth() + 100);
                            hero.setMana(hero.getMana() + 5);
                            hero.setSkillPoints(hero.getSkillPoints() + 2);
                            heroService.saveHero(hero);
                            // Remove the defeated enemy from the database
                            enemiesService.deleteEnemy(enemy);

                            // Get the next enemy for the next fight
                            Enemies nextEnemy = enemiesService.getNextEnemy();
                            if (nextEnemy != null) {
                                session.setAttribute("currentEnemyId", nextEnemy.getId());
                                model.addAttribute("enemy", nextEnemy);
                            } else {
                                // Handle the case where there are no more enemies in the database
                                model.addAttribute("noMoreEnemies", true); // Add a flag to indicate there are no more enemies
                            }
                            return "redirect:/hero"; // Redirect to the hero page if the enemy is defeated
                        }

                        enemy.setHealth(newEnemyHealth);
                        enemiesService.saveEnemy(enemy);

                        // Set the spell casting result message in the model to display it in the fight page
                        model.addAttribute("spellCastingResult", "You cast Fire bolt for " + spellDamage + " damage!");
                    } else {
                        model.addAttribute("spellCastingResult", "No enemy to cast the spell on.");
                    }
                    // Set the enemy attribute in the session to be used in the fight.html template
                    session.setAttribute("currentEnemy", enemy);
                } else {
                    model.addAttribute("spellCastingResult", "No enemy to cast the spell on.");
                }
            } else {
                model.addAttribute("spellCastingResult", "Not enough mana to cast Fire bolt.");
            }
        } else {
            model.addAttribute("spellCastingResult", "You don't have the Fire bolt spell equipped.");
        }

        // Load the enemy from the service and add it to the model
        Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
        if (currentEnemyId != null) {
            Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
            model.addAttribute("enemy", enemy);
        }

        model.addAttribute("hero", hero);
        return "fight";
    }

    @PostMapping("/upgradeHealth")
    public String upgradeHealth(Model model) {
        Hero hero = heroService.getYourHero();
        if (hero != null && hero.getSkillPoints() > 0) {
            hero.upgradeHealth();
            heroService.saveHero(hero);
            model.addAttribute("upgradeMessage", "Health upgraded by 20 points.");
        } else {
            model.addAttribute("upgradeMessage", "Not enough skill points to upgrade health.");
        }
        model.addAttribute("hero", hero);
        return "hero";
    }
    @PostMapping("/upgradeMana")
    public String upgradeMana(Model model) {
        Hero hero = heroService.getYourHero();
        if (hero != null && hero.getSkillPoints() > 0) {
            hero.upgradeMana();
            heroService.saveHero(hero);
            model.addAttribute("upgradeMessage", "Mana upgraded by 5 points.");
        } else {
            model.addAttribute("upgradeMessage", "Not enough skill points to upgrade mana.");
        }
        model.addAttribute("hero", hero);
        return "hero";
    }

    @PostMapping("/upgradeAttack")
    public String upgradeAttack(Model model) {
        Hero hero = heroService.getYourHero();
        if (hero != null && hero.getSkillPoints() > 0) {
            hero.upgradeAttack();
            heroService.saveHero(hero);
            model.addAttribute("upgradeMessage", "Attack upgraded by 1 point.");
        } else {
            model.addAttribute("upgradeMessage", "Not enough skill points to upgrade attack.");
        }
        model.addAttribute("hero", hero);
        return "hero";
    }

    @PostMapping("/upgradeDefense")
    public String upgradeDefense(Model model) {
        Hero hero = heroService.getYourHero();
        if (hero != null && hero.getSkillPoints() > 0) {
            hero.upgradeDefense();
            heroService.saveHero(hero);
            model.addAttribute("upgradeMessage", "Defense upgraded by 1 point.");
        } else {
            model.addAttribute("upgradeMessage", "Not enough skill points to upgrade defense.");
        }
        model.addAttribute("hero", hero);
        return "hero";
    }


}
