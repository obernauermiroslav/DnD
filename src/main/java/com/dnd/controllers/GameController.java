package com.dnd.controllers;

import com.dnd.models.Enemies;
import com.dnd.models.Hero;
import com.dnd.models.ItemType;
import com.dnd.models.Items;
import com.dnd.services.EnemiesService;
import com.dnd.services.HeroService;
import jakarta.servlet.http.HttpSession;
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

        // Store the max health without changing it during the fight
        int maxHealth = hero.calculateMaxHealth();
        hero.setMaxHealth(maxHealth);

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
                        Model model) {
        // Load your hero and the enemy from their respective tables using the IDs received from the form
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        Enemies enemy = enemiesService.getEnemyById(enemyId);
        if (hero == null || enemy == null) {
            model.addAttribute("errorMessage", "Hero or enemy not found");
            return "error-page";
        }

        int heroAttack;
        int enemyAttack;
        String enemyAttackMessage = "";
        if (enemy.getName().equalsIgnoreCase("Lich") || enemy.getName().equalsIgnoreCase("Drake")) {
            // Lich and Dragon ignore hero armor, so the default attack will be just the enemy's attack
            heroAttack = hero.getAttack() - enemy.getDefence();
            enemyAttack = enemy.getAttack();
            enemyAttackMessage = enemy.getName() + "'s attack bypasses your armor!";
        } else {
            // Normal attack calculation considering hero's armor
            heroAttack = hero.getAttack() - enemy.getDefence();
            enemyAttack = enemy.getAttack() - hero.getDefense();

            // Check if the attack is ineffective and set it to zero
            heroAttack = Math.max(heroAttack, 0);
            enemyAttack = Math.max(enemyAttack, 0);
        }

        // Check if the hero has the "Fire Shield" equipped and if the enemy is "Drake"
        if (enemy.getName().equalsIgnoreCase("drake") && hero.hasFireShield() && Math.random() <= 0.15) {
            enemyAttack = 0;
            enemyAttackMessage = "You blocked dragon's attack with your Fire Shield!";
        } else if (!enemy.getName().equalsIgnoreCase("drake") && !enemy.getName().equalsIgnoreCase("lich") && hero.hasShield() && Math.random() <= 0.15) {
            enemyAttack = 0;
            enemyAttackMessage = "You blocked the enemy's attack with your shield!";
        }

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
        String heroAttackMessage = "";
        String ineffectiveHeroAttackMessage = "";
        String ineffectiveEnemyAttackMessage = "";
        String bonusMessage = "";
        if (newEnemyHealth <= 0) {
            hero.setGold(hero.getGold() + 200);
            hero.setMana(hero.getMana() + 6);
            hero.setSkillPoints(hero.getSkillPoints() + 2);
            hero.setRunes((hero.getRunes() + 1));
            heroService.saveHero(hero);
            bonusMessage = "You have won the fight and received:<br>" +
                           "+200 Gold, +6 Mana, +2 Skill Points, +1 Rune";

    model.addAttribute("bonusMessage", bonusMessage);

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
            model.addAttribute("heroLost", true);
            session.setAttribute("heroLost", true);
        } else {
            fightResult = "";
            model.addAttribute("heroLost", false);
        }

        if (heroAttack > 0) {
            heroAttackMessage = "Your hero attacks for " + heroAttack + " damage!";
        }

        if (enemyAttack > 0) {
            enemyAttackMessage = "Enemy attacks for " + enemyAttack + " damage!";
        }

        if (heroAttack == 0) {
            ineffectiveHeroAttackMessage = "Your hero's attack was ineffective!";
        }

        if (enemyAttack == 0) {
            ineffectiveEnemyAttackMessage = "Enemy's attack was ineffective!";
        }

        model.addAttribute("fightResult", fightResult);
        model.addAttribute("heroAttackMessage", heroAttackMessage);
        model.addAttribute("enemyAttackMessage", enemyAttackMessage);
        model.addAttribute("ineffectiveHeroAttackMessage", ineffectiveHeroAttackMessage);
        model.addAttribute("ineffectiveEnemyAttackMessage", ineffectiveEnemyAttackMessage);

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
            return "error"; // You might want to return an error page here
        }

        // Check if the hero has at least one healing potion
        int potionCount = hero.getPotion();
        if (potionCount > 0) {
            // Calculate the amount of health that can be healed
            int maxHealingAmount = hero.getMaxHealth() - hero.getHealth();

            // Ensure the healing amount is within the range of 0 to maxHealingAmount
            int actualHealingAmount = Math.max(0, Math.min(30, maxHealingAmount));

            if (actualHealingAmount > 0) {
                // Heal the hero
                hero.setHealth(hero.getHealth() + actualHealingAmount);

                // Reduce the number of healing potions by 1
                hero.setPotion(potionCount - 1);

                // Update the hero's stats in the database
                heroService.saveHero(hero);

                model.addAttribute("healMessage", "Healing potion restores " + actualHealingAmount + " health.");
            } else {
                model.addAttribute("healMessage", "Hero's health is already full.");
            }
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
            int weaknessManaCost = 7; // Define the mana cost for Weakness spell
            if (hero.getMana() >= weaknessManaCost) {
                Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
                if (currentEnemyId != null) {
                    Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
                    if (enemy != null) {
                        int spellDamage = 3;
                        int newEnemyAttack = enemy.getAttack() - spellDamage;

                        // Ensure the attack doesn't go below zero
                        if (newEnemyAttack >= 0) {
                            hero.setMana(hero.getMana() - weaknessManaCost);
                            heroService.saveHero(hero);
                            enemy.setAttack(newEnemyAttack);
                            enemiesService.saveEnemy(enemy);

                            // Set the spell casting result message in the model to display it in the fight page
                            model.addAttribute("spellCastingResult", "You cast Weakness. Enemy's attack is decreased by " + spellDamage + " points!");
                        } else {
                            model.addAttribute("spellCastingResult", "Enemy's attack is on minimum.");
                        }
                        // Set the enemy attribute in the session to be used in the fight.html template
                        session.setAttribute("currentEnemy", enemy);
                    } else {
                        model.addAttribute("spellCastingResult", "No enemy to cast the Weakness on.");
                    }
                } else {
                    model.addAttribute("spellCastingResult", "Enemy's attack is on minimum.");
                }
            } else {
                model.addAttribute("spellCastingResult", "Not enough mana to cast Weakness.");
            }
        } else {
            model.addAttribute("spellCastingResult", "You do not know how to cast Weakness.");
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
    @PostMapping("/sunder_armor")
    public String castSunderArmor(@RequestParam("heroId") Long heroId, Model model, HttpSession session) {
        // Load the hero from the database using the heroId received from the form
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        if (hero == null) {
            model.addAttribute("errorMessage", "Hero not found");
            return "error-page";
        }

        // Check if the hero has equipped the "Weakness" spell
        boolean hasSunder_ArmorSpell = hero.getEquippedItems().stream()
                .anyMatch(item -> item.getName().equals("Sunder Armor") && item.getType() == ItemType.SPELL);

        // If the hero has the "Sunder_Armor" spell equipped, perform the spell casting logic
        if (hasSunder_ArmorSpell) {
            int sunder_armorManaCost = 7; // Define the mana cost for Sunder_Armor spell
            if (hero.getMana() >= sunder_armorManaCost) {
                Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
                if (currentEnemyId != null) {
                    Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
                    if (enemy != null) {
                        int spellDamage = 3;
                        int newEnemyDefence = enemy.getDefence() - spellDamage;

                        // Ensure the attack doesn't go below zero
                        if (newEnemyDefence >= 0) {
                            hero.setMana(hero.getMana() - sunder_armorManaCost);
                            heroService.saveHero(hero);

                            enemy.setDefence(newEnemyDefence);
                            enemiesService.saveEnemy(enemy);

                            // Set the spell casting result message in the model to display it in the fight page
                            model.addAttribute("spellCastingResult", "You cast Sunder Armor. Enemy's defence is decreased by " + spellDamage + " points!");
                        } else {
                            model.addAttribute("spellCastingResult", "Enemy's defence is on minimum.");
                        }
                        // Set the enemy attribute in the session to be used in the fight.html template
                        session.setAttribute("currentEnemy", enemy);
                    } else {
                        model.addAttribute("spellCastingResult", "No enemy to cast Sunder Armor on.");
                    }
                } else {
                    model.addAttribute("spellCastingResult", "Enemy's defence is on minimum.");
                }
            } else {
                model.addAttribute("spellCastingResult", "Not enough mana to cast Sunder Armor.");
            }
        } else {
            model.addAttribute("spellCastingResult", "You do not know how to cast Sunder Armor.");
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

    @PostMapping("/healing")
    public String castHealing(@RequestParam("heroId") Long heroId, Model model, HttpSession session) {
        // Load the hero from the database using the heroId received from the form
        Optional<Hero> heroOptional = heroService.getHeroById(heroId);
        Hero hero = heroOptional.orElse(null);

        if (hero == null) {
            model.addAttribute("errorMessage", "Hero not found");
            return "error-page";
        }

        // Check if the hero has equipped the "Weakness" spell
        boolean hasHealingSpell = hero.getEquippedItems().stream()
                .anyMatch(item -> item.getName().equals("Healing") && item.getType() == ItemType.SPELL);

        // If the hero has the "Healing" spell equipped, perform the spell casting logic
        if (hasHealingSpell) {
            int healingManaCost = 6; // Define the mana cost for Healing spell
            if (hero.getMana() >= healingManaCost) {
                Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
                if (currentEnemyId != null) {
                    Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
                    if (enemy != null) {
                        int healAmount = 25;
                        int newHeroHealth = hero.getHealth() + healAmount;

                        if (hero.getHealth() == hero.getMaxHealth()) {
                            model.addAttribute("spellCastingResult", "No need to cast Healing, your health is on maximum. .");
                        } else {
                            hero.setMana(hero.getMana() - healingManaCost);
                            int actualHealAmount = Math.min(healAmount, hero.getMaxHealth() - hero.getHealth());
                            hero.setHealth(hero.getHealth() + actualHealAmount);

                            heroService.saveHero(hero);

                            // Set the spell casting result message in the model to display it in the fight page
                            model.addAttribute("spellCastingResult", "You cast Healing. You are healed by " + actualHealAmount + " points!");

                            // Set the enemy attribute in the session to be used in the fight.html template
                            session.setAttribute("currentEnemy", enemy);
                        }
                    } else {
                        model.addAttribute("spellCastingResult", "No need to cast Healing, you have won.");
                    }
                } else {
                    model.addAttribute("spellCastingResult", "Error occured.");
                }
            } else {
                model.addAttribute("spellCastingResult", "Not enough mana to cast Healing.");
            }
        } else {
            model.addAttribute("spellCastingResult", "You do not know how to cast Healing.");
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
                .anyMatch(item -> item.getName().equals("Firebolt") && item.getType() == ItemType.SPELL);

        // If the hero has the "Fire bolt" spell equipped, perform the spell casting logic
        if (hasFireBoltSpell) {
            int fireBoltManaCost = 6; // Define the mana cost for Fire bolt spell
            if (hero.getMana() >= fireBoltManaCost) {
                // Deduct the mana cost from the hero's total mana points
                hero.setMana(hero.getMana() - fireBoltManaCost);
                heroService.saveHero(hero);

                Long currentEnemyId = (Long) session.getAttribute("currentEnemyId");
                if (currentEnemyId != null) {
                    Enemies enemy = enemiesService.getEnemyById(currentEnemyId);
                    if (enemy != null) {
                        int spellDamage = 30;
                        int newEnemyHealth = enemy.getHealth() - spellDamage;
                        String bonusMessage = "";

                        // Ensure the health doesn't go below zero
                        if (newEnemyHealth <= 0) {
                            hero.setGold(hero.getGold() + 200);
                            hero.setMana(hero.getMana() + 6);
                            hero.setSkillPoints(hero.getSkillPoints() + 2);
                            hero.setRunes((hero.getRunes() + 1));
                            heroService.saveHero(hero);
                            bonusMessage = "You have won the fight and received:<br>" +
            "+200 Gold<br>" +
            "+6 Mana<br>" +
            "+2 Skill Points<br>" +
            "+1 Rune";

    model.addAttribute("bonusMessage", bonusMessage);
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
                        model.addAttribute("spellCastingResult", "You cast Firebolt for " + spellDamage + " damage!");
                    } else {
                        model.addAttribute("spellCastingResult", "No enemy to cast the Firebolt on.");
                    }
                    // Set the enemy attribute in the session to be used in the fight.html template
                    session.setAttribute("currentEnemy", enemy);
                } else {
                    model.addAttribute("spellCastingResult", "No enemy to cast the spell on.");
                }
            } else {
                model.addAttribute("spellCastingResult", "Not enough mana to cast Firebolt.");
            }
        } else {
            model.addAttribute("spellCastingResult", "You do not know how to cast Firebolt.");
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
            model.addAttribute("upgradeMessage", "Health upgraded by 30 points.");
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
