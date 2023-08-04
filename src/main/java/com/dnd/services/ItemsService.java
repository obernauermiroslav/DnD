package com.dnd.services;

import com.dnd.models.Items;

import java.util.List;

public interface ItemsService {
    Items getItemById(Long id);
    Iterable<Items> getAllItems();
    Items saveItem(Items item);
    void deleteItem(Long id);
    List<Items> getEquippedItems(Long heroId);
}