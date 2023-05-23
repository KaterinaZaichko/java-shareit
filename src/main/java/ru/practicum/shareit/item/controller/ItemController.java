package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    public Item saveItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Item had been created");
        return itemService.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId,
                           @RequestBody ItemDto itemDto) {
        log.info("User had been updated");
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<Item> getItemsByRequest(@RequestParam String text) {
        return itemService.getItemsByRequest(text);
    }
}
