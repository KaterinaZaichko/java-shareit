package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItemsOfUser(Long userId);

    Item getItemById(Long itemId);

    Item saveItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    List<Item> getItemsByRequest(String text);
}
