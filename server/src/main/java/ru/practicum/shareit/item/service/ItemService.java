package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemService {
    List<Item> getItemsByOwner(Long userId);

    List<Item> getItemsByOwner(Long userId, int from, int size);

    Item getItemById(Long itemId);

    Item saveItem(Long userId, ItemDto itemDto, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    List<Item> search(String text, int from, int size);

    Booking getLastBookingByItem(Item item);

    Booking getNextBookingByItem(Item item);

    Comment saveComment(Long userId, Long itemId, Comment comment);

    List<Comment> findCommentsByItem(Item item);

    List<Item> findItemsByRequest(ItemRequest itemRequestId);
}
