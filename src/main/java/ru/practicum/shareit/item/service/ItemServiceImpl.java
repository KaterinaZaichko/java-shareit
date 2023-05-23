package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.UpdateNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getItemsOfUser(Long userId) {
        return itemRepository.findItemsByUserId(userId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findItemById(itemId);
    }

    @Override
    public Item saveItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findUserById(userId);
        return itemRepository.createItem(user, itemDto);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.findUserById(userId);
        Item item = itemRepository.findItemById(itemId);
        if (item.getOwner().equals(user)) {
            return itemRepository.update(item, itemDto);
        } else {
            throw new UpdateNotAvailableException(
                    String.format("User with id %d is not owner of item with id %d", userId, itemId));
        }
    }

    @Override
    public List<Item> getItemsByRequest(String text) {
        return itemRepository.findItemsByRequest(text);
    }
}
