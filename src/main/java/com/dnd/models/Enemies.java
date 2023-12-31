package com.dnd.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name = "enemies")
public class Enemies implements Serializable {
    private String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int defence;

    private String info;
    @Id
    private Long id;

    public Enemies() {
    }

    public Enemies(Long id, String name, int health,int maxHealth, int attack, int defence, String info ) {
        this.id = id;
        this.name = name;
        this.health = health;
        this.maxHealth = maxHealth;
        this.attack = attack;
        this.defence = defence;
        this.info = info;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String saveEnemyData() {
        return String.format("%d,%d,%d", health, attack, defence);
    }

    public void loadEnemyData(String data) {
        String[] values = data.split(",");
        if (values.length == 3) {
            health = Integer.parseInt(values[0]);
            attack = Integer.parseInt(values[1]);
            defence = Integer.parseInt(values[2]);
        } else {
            // Handle incorrect data format
        }
    }
}