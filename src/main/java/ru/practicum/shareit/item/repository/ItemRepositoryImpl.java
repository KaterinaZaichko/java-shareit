package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
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
    public Item createItem(User user, Item item) {
        Item newItem = new Item();
        newItem.setId(generateId());
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        newItem.setOwner(user);
        if (item.getRequest() != null) {
            newItem.setRequest(item.getRequest());
        }
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item update(Item itemForUpdate, Item item) {
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        return itemForUpdate;
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
