package com.dnd.services;

import com.dnd.models.Items;
import com.dnd.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsServiceImpl implements ItemsService {

    private final ItemsRepository itemsRepository;

    @Autowired
    public ItemsServiceImpl(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public Items getItemById(Long id) {
        return itemsRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Items> getAllItems() {
        return itemsRepository.findAll();
    }

    @Override
    public Items saveItem(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        itemsRepository.deleteById(id);
    }
    @Override
    public List<Items> getEquippedItems(Long heroId) {
        // You need to implement the logic to fetch equipped items here
        // For example, you can use the HeroRepository to get the hero by ID and return its equippedItems
        // Make sure to adapt the logic based on how your data model is structured
        // For demonstration purposes, let's assume that we are returning an empty list for now
        return new ArrayList<>();
    }
}