package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto save(long userId, ItemRequestDto itemRequestDto) {
        User requestor = userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByRequestor(long userId) {
        User requestor = userService.getUserById(userId);
        List<ItemRequestDto> itemRequestsByRequestor = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.findAllByRequestorOrderByCreated(requestor)) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<ItemDto> itemsByRequest = new ArrayList<>();
            if (itemRepository.findAllByRequest(itemRequest) != null) {
                for (Item item : itemRepository.findAllByRequest(itemRequest)) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemsByRequest.add(itemDto);
                }
                itemRequestDto.setItems(itemsByRequest);
            }
            itemRequestsByRequestor.add(itemRequestDto);
        }
        return itemRequestsByRequestor;
    }

    @Override
    public List<ItemRequestDto> getAllByRequestor(long userId, int from, int size) {
        User requestor = userService.getUserById(userId);
        Pageable pageWithSomeElements = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<ItemRequestDto> itemRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.findAllByRequestorNot(requestor, pageWithSomeElements)) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<ItemDto> itemsByRequest = new ArrayList<>();
            if (itemRepository.findAllByRequest(itemRequest) != null) {
                for (Item item : itemRepository.findAllByRequest(itemRequest)) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemsByRequest.add(itemDto);
                }
                itemRequestDto.setItems(itemsByRequest);
            }
            itemRequests.add(itemRequestDto);
        }
        return itemRequests;
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        if (userRepository.existsById(userId)) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(
                    String.format("ItemRequest with id %d not found", requestId)));
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<ItemDto> itemsByRequest = new ArrayList<>();
            if (itemRepository.findAllByRequest(itemRequest) != null) {
                for (Item item : itemRepository.findAllByRequest(itemRequest)) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    itemsByRequest.add(itemDto);
                }
                itemRequestDto.setItems(itemsByRequest);
            }
            return itemRequestDto;
        }
        throw new UserNotFoundException(String.format("User with id %d not found", userId));
    }
}
