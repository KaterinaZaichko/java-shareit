package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequest save(long userId, ItemRequest itemRequest) {
        User requestor = checkExistenceOfUser(userId);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllByRequestor(long userId) {
        User requestor = checkExistenceOfUser(userId);
        return itemRequestRepository.findAllByRequestorOrderByCreated(requestor);
    }

    @Override
    public List<ItemRequest> getAllByRequestor(long userId, int from, int size) {
        User requestor = checkExistenceOfUser(userId);
        Pageable pageWithSomeElements = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by("created").descending());
        return itemRequestRepository.findAllByRequestorNot(requestor, pageWithSomeElements);
    }

    @Override
    public ItemRequest getItemRequestById(long userId, long requestId) {
        checkExistenceOfUser(userId);
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(
                String.format("ItemRequest with id %d not found", requestId))
        );

    }

    private User checkExistenceOfUser(long userId) {
        return userService.getUserById(userId);
    }
}
