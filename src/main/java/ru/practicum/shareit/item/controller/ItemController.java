package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        List<ItemDto> itemsByOwner = new ArrayList<>();
        List<CommentDto> commentsByItem = new ArrayList<>();
        for (Item item : itemService.getItemsByOwner(userId, from, size)) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (itemService.getLastBookingByItem(item) != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(
                        itemService.getLastBookingByItem(item)));
            }
            if (itemService.getNextBookingByItem(item) != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(
                        itemService.getNextBookingByItem(item)));
            }
            for (Comment comment : itemService.findCommentsByItem(item)) {
                CommentDto commentDto = CommentMapper.toCommentDto(comment);
                commentsByItem.add(commentDto);
            }
            itemDto.setComments(commentsByItem);
            itemsByOwner.add(itemDto);
        }
        return itemsByOwner;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long itemId) {
        Item item = itemService.getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (itemService.getLastBookingByItem(item) != null && item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(BookingMapper.toBookingDto(
                    itemService.getLastBookingByItem(item)));
        }
        if (itemService.getNextBookingByItem(item) != null && item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(
                    itemService.getNextBookingByItem(item)));
        }
        List<CommentDto> commentsByItem = new ArrayList<>();
        if (itemService.findCommentsByItem(item) != null) {
            for (Comment comment : itemService.findCommentsByItem(item)) {
                CommentDto commentDto = CommentMapper.toCommentDto(comment);
                commentsByItem.add(commentDto);
            }
            itemDto.setComments(commentsByItem);
        }
        return itemDto;
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @RequestBody @Valid ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        log.info("Item is being created");
        return ItemMapper.toItemDto(itemService.saveItem(userId, itemDto, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        log.info("Item is being updated");
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, item));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(defaultValue = "10") @Positive int size) {
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemService.search(text, from, size)) {
            items.add(ItemMapper.toItemDto(item));
        }
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long itemId,
                                  @RequestBody @Valid CommentDto commentDto) {
        Comment comment = CommentMapper.toComment(commentDto);
        log.info("Comment is being created");
        return CommentMapper.toCommentDto(itemService.saveComment(userId, itemId, comment));
    }
}
