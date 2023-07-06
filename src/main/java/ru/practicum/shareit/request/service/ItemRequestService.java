package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByRequestor(long userId);

    List<ItemRequestDto> getAllByRequestor(long userId, int from, int size);

    ItemRequestDto getItemRequestById(long userId, long requestId);
}
