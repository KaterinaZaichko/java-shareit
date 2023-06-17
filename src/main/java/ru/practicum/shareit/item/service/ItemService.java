package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItemsByOwner(Long userId);

    Item getItemById(Long itemId);

    Item saveItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    List<Item> search(String text);

    Booking getLastBookingByItem(Item item);

    Booking getNextBookingByItem(Item item);

    Comment saveComment(Long userId, Long itemId, Comment comment);

    List<Comment> findCommentsByItem(Item item);
}
