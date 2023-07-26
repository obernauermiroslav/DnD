package com.dnd.models;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private List<Items> availableItems;

    public Shop() {
        availableItems = new ArrayList<>();
    }

    public void addItem(Items item) {
        availableItems.add(item);
    }

    public void displayItems() {
        System.out.println("Available Items in Shop:");
        for (int i = 0; i < availableItems.size(); i++) {
            System.out.println((i + 1) + ". " + availableItems.get(i).getName());
        }
    }

    public boolean buyItem(Items item, Hero hero) {
        return true;
    }

    public List<Items> getAvailableItems() {
        return availableItems;
    }
}

