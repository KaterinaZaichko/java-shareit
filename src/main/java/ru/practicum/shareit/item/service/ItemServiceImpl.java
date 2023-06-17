package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.CommentNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UpdateNotAvailableException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Item> getItemsByOwner(Long userId) {
        User owner = userService.getUserById(userId);
        return itemRepository.findAllByOwnerOrderById(owner);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Item with id %d not found", itemId))
        );
    }

    @Override
    public Item saveItem(Long userId, Item item) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        User owner = userService.getUserById(userId);
        Item updatedItem = getItemById(itemId);
        if (updatedItem.getOwner().getId().equals(userId)) {
            item.setId(itemId);
            if (item.getName() == null) {
                item.setName(updatedItem.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(updatedItem.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(updatedItem.getAvailable());
            }
            item.setOwner(owner);
            return itemRepository.save(item);
        } else {
            throw new UpdateNotAvailableException(
                    String.format("User with id %d is not owner of item with id %d", userId, itemId));
        }
    }

    @Override
    public List<Item> search(String text) {
        if (!text.isEmpty()) {
            return itemRepository.search(text);
        }
        return new ArrayList<>();
    }

    @Override
    public Booking getLastBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(
                item, Status.APPROVED, LocalDateTime.now());
    }

    @Override
    public Booking getNextBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(
                item, Status.APPROVED, LocalDateTime.now());
    }

    @Override
    public Comment saveComment(Long userId, Long itemId, Comment comment) {
        User author = userService.getUserById(userId);
        Item item = getItemById(itemId);
        Booking booking = bookingRepository.findFirstByItemAndBookerAndStatusAndEndBefore(
                item, author, Status.APPROVED, LocalDateTime.now());
        if (booking != null) {
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return commentRepository.save(comment);
        }
        throw new CommentNotAvailableException(String.format(
                "User with id %d is not booker of item with id %d or booking is not over yet", userId, itemId));
    }

    @Override
    public List<Comment> findCommentsByItem(Item item) {
        return commentRepository.findAllByItem(item);
    }
}
