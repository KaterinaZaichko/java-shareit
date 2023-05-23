package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items;
    private int itemsCount = 0;

    public long generateId() {
        return ++itemsCount;
    }

    @Override
    public List<Item> findItemsByUserId(Long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public Item findItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new ItemNotFoundException(String.format("Item with id %d not found", itemId));
        }
    }

    @Override
    public Item createItem(User user, ItemDto itemDto) {
        Item item = new Item();
        item.setId(generateId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    @Override
    public List<Item> findItemsByRequest(String text) {
        List<Item> itemsByRequest = new ArrayList<>();
        if (!text.isEmpty()) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true)) {
                    itemsByRequest.add(item);
                }
            }
        }
        return itemsByRequest;
    }
}
