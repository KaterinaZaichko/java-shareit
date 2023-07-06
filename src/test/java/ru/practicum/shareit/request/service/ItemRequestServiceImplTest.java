package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void save_whenUserFound_thenReturnSavedItemRequest() {
        long userId = 0L;
        User requester = new User();
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, null, null, null);
        ItemRequest itemRequest = new ItemRequest(null, null, null, null);
        ItemRequestDto expectedItemRequestDto = new ItemRequestDto(null, null, null, null);
        when(userService.getUserById(userId)).thenReturn(requester);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualItemRequestDto = itemRequestService.save(userId, itemRequestDto);

        assertEquals(expectedItemRequestDto, actualItemRequestDto);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getAllByRequestorWithoutPagination_whenUserFound_thenReturnListOfItemRequests() {
        long userId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequestDto> expectedItemRequests = List.of(new ItemRequestDto(null, null, null,
                List.of(new ItemDto(null, null, null, null, null,
                        null, null, null))));
        when(userService.getUserById(userId)).thenReturn(new User());
        when(itemRequestRepository.findAllByRequestorOrderByCreated(requestor)).thenReturn(List.of(new ItemRequest()));
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(List.of(new Item()));

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllByRequestor(userId);

        assertEquals(expectedItemRequests, actualItemRequests);
    }

    @Test
    void testGetAllByRequestorWithPagination_whenUserFound_thenReturnListOfItemRequests() {
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        long userId = 0L;
        User requestor = new User();
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequestDto> expectedItemRequests = List.of(new ItemRequestDto(null, null, null,
                List.of(new ItemDto(null, null, null, null, null,
                        null, null, null))));
        when(userService.getUserById(userId)).thenReturn(new User());
        when(itemRequestRepository.findAllByRequestorNot(requestor, pageable)).thenReturn(List.of(new ItemRequest()));
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(List.of(new Item()));

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllByRequestor(userId, from, size);

        assertEquals(expectedItemRequests, actualItemRequests);
    }

    @Test
    void getItemRequestById_whenUserAndItemRequestFound_thenReturnItemRequest() {
        long userId = 0L;
        long requestId = 0L;
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto expectedItemRequestDto = new ItemRequestDto(null, null, null,
                List.of(new ItemDto(null, null, null, null, null,
                        null, null, null)));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(List.of(new Item()));

        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(userId, requestId);

        assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    void getItemRequestById_whenUserFoundButItemRequestNotFound_thenItemNotFoundExceptionThrown() {
        long userId = 0L;
        long itemRequestId = 0L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(itemRequestId)).thenThrow(new ItemRequestNotFoundException("Ошибка"));

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(userId, itemRequestId));
    }
}