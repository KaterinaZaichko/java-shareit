package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest save(long userId, ItemRequest itemRequest);

    List<ItemRequest> getAllByRequestor(long userId);

    List<ItemRequest> getAllByRequestor(long userId, int from, int size);

    ItemRequest getItemRequestById(long userId, long requestId);
}
