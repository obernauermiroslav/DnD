package com.dnd.services;

import com.dnd.models.Enemies;
import com.dnd.repositories.EnemiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnemiesService {

    private final EnemiesRepository enemiesRepository;
    private List<Enemies> allEnemies;
    private int currentEnemyIndex = 0;

    @Autowired
    public EnemiesService(EnemiesRepository enemiesRepository) {
        this.enemiesRepository = enemiesRepository;
    }

    private List<Enemies> loadAllEnemies() {
        return enemiesRepository.findAll();
    }

    public Enemies getEnemyById(Long id) {
        return enemiesRepository.findById(id).orElse(null);
    }

    public Enemies getNextEnemy() {
        if (allEnemies == null) {
            // Load all enemies lazily if not already loaded
            allEnemies = loadAllEnemies();
        }

        if (allEnemies.isEmpty()) {
            // If there are no enemies, return null
            return null;
        }

        if (currentEnemyIndex >= allEnemies.size()) {
            // If all enemies have been fought, reset the index to 0
            currentEnemyIndex = 0;
        }

        Enemies nextEnemy = allEnemies.get(currentEnemyIndex);
        currentEnemyIndex++;

        return nextEnemy;
    }

    public Enemies saveEnemy(Enemies enemy) {
        return enemiesRepository.save(enemy);
    }

    public void deleteEnemy(Enemies enemy) {
        if (allEnemies == null) {
            allEnemies = loadAllEnemies();
        }

        int enemyIndex = allEnemies.indexOf(enemy);
        if (enemyIndex >= 0) {
            // If the enemy is found in the list, remove it
            allEnemies.remove(enemyIndex);

            // Update the currentEnemyIndex if needed
            if (enemyIndex < currentEnemyIndex) {
                // If the deleted enemy is before the current enemy index, adjust the index
                currentEnemyIndex--;
            } else if (currentEnemyIndex >= allEnemies.size()) {
                // If the currentEnemyIndex exceeds the number of available enemies, reset it to 0
                currentEnemyIndex = 0;
            }
        }

        // Delete the enemy from the database
        enemiesRepository.delete(enemy);
    }

    public void resetEnemyIndex() {
        currentEnemyIndex = 0;
    }

    public int getCurrentEnemyIndex() {
        return currentEnemyIndex;
    }

    public Enemies getFirstEnemy() {
        // Get all enemies sorted by ID in ascending order
        List<Enemies> enemiesList = enemiesRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        // Return the first enemy from the list
        return enemiesList.isEmpty() ? null : enemiesList.get(0);
    }
}