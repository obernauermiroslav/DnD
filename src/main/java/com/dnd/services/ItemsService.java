package com.dnd.services;

import com.dnd.models.Items;

public interface ItemsService {
    Items getItemById(Long id);
    Iterable<Items> getAllItems();
    Items saveItem(Items item);
    void deleteItem(Long id);
}