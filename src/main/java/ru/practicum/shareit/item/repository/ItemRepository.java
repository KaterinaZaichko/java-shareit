package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    List<Item> findItemsByUserId(Long userId);

    Item findItemById(Long itemId);

    Item createItem(User user, ItemDto itemDto);

    Item update(Item item, ItemDto itemDto);

    List<Item> findItemsByRequest(String text);
}
