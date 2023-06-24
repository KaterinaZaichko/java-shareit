package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto saveRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        log.info("ItemRequest is being created: {}", itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.save(userId, itemRequest));
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemRequestDto> itemRequestsByRequestor = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestService.getAllByRequestor(userId)) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<ItemDto> itemsByRequest = new ArrayList<>();
            if (itemService.findItemsByRequest(itemRequest) != null) {
                for (Item item : itemService.findItemsByRequest(itemRequest)) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemsByRequest.add(itemDto);
                }
                itemRequestDto.setItems(itemsByRequest);
            }
            itemRequestsByRequestor.add(itemRequestDto);
        }
        return itemRequestsByRequestor;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        List<ItemRequestDto> itemRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestService.getAllByRequestor(userId, from, size)) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<ItemDto> itemsByRequest = new ArrayList<>();
            if (itemService.findItemsByRequest(itemRequest) != null) {
                for (Item item : itemService.findItemsByRequest(itemRequest)) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemsByRequest.add(itemDto);
                }
                itemRequestDto.setItems(itemsByRequest);
            }
            itemRequests.add(itemRequestDto);
        }
        return itemRequests;
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        ItemRequest itemRequest = itemRequestService.getItemRequestById(userId, requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> itemsByRequest = new ArrayList<>();
        if (itemService.findItemsByRequest(itemRequest) != null) {
            for (Item item : itemService.findItemsByRequest(itemRequest)) {
                ItemDto itemDto = ItemMapper.toItemDto(item);
                itemsByRequest.add(itemDto);
            }
            itemRequestDto.setItems(itemsByRequest);
        }
        return itemRequestDto;
    }
}
