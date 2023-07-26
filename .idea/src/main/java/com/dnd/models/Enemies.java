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
    private int attack;
    private int defence;
    @Id
    private Long id;

    public Enemies() {
    }

    public Enemies(Long id, String name, int health, int attack, int defence) {
        this.id = id;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defence = defence;
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