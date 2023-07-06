package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
        log.info("ItemRequest is being created");
        return itemRequestService.save(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestService.getAllByRequestor(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
