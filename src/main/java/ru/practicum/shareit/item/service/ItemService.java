package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItemsOfUser(Long userId);

    Item getItemById(Long itemId);

    Item saveItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<Item> getItemsByRequest(String text);
}
