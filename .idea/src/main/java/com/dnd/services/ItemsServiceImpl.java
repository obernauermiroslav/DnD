package com.dnd.services;

import com.dnd.models.Items;
import com.dnd.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}