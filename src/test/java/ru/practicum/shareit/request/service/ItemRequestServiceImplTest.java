package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void save_whenUserFound_thenReturnSavedItemRequest() {
        long userId = 0L;
        ItemRequest itemRequestToSave = new ItemRequest();
        when(itemRequestRepository.save(itemRequestToSave)).thenReturn(itemRequestToSave);

        ItemRequest savedItemRequest = itemRequestService.save(userId, itemRequestToSave);

        assertEquals(itemRequestToSave, savedItemRequest);
        verify(itemRequestRepository).save(itemRequestToSave);
    }

    @Test
    void getAllByRequestorWithoutPagination_whenUserFound_thenReturnListOfItemRequests() {
        User requestor = new User(0L, "name", "email@mail.ru");
        List<ItemRequest> itemRequests = List.of(
                new ItemRequest(0L, "description", requestor, LocalDateTime.now()));
        when(userService.getUserById(requestor.getId())).thenReturn(requestor);
        when(itemRequestRepository.findAllByRequestorOrderByCreated(requestor)).thenReturn(itemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService.getAllByRequestor(requestor.getId());

        assertEquals(itemRequests, actualItemRequests);
    }

    @Test
    void testGetAllByRequestorWithPagination_whenUserFound_thenReturnListOfItemRequests() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        User requestor = new User(0L, "name", "email@mail.ru");
        User otherUser = new User(1L, "otherName", "otherEmail@mail.ru");
        List<ItemRequest> itemRequests = List.of(
                new ItemRequest(0L, "description", otherUser, LocalDateTime.now()));
        when(userService.getUserById(requestor.getId())).thenReturn(requestor);
        when(itemRequestRepository.findAllByRequestorNot(requestor, pageable)).thenReturn(itemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService.getAllByRequestor(requestor.getId(), 0, 10);

        assertEquals(itemRequests, actualItemRequests);
    }

    @Test
    void getItemRequestById_whenUserAndItemRequestFound_thenReturnItemRequest() {
        long userId = 0L;
        long itemRequestId = 0L;
        ItemRequest expectedItemRequest = new ItemRequest();
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(expectedItemRequest));

        ItemRequest actualItemRequest = itemRequestService.getItemRequestById(userId, itemRequestId);

        assertEquals(expectedItemRequest, actualItemRequest);
    }

    @Test
    void getItemRequestById_whenUserFoundButItemRequestNotFound_thenItemNotFoundExceptionThrown() {
        long userId = 0L;
        long itemRequestId = 0L;
        when(itemRequestRepository.findById(itemRequestId)).thenThrow(new ItemRequestNotFoundException("Ошибка"));

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(userId, itemRequestId));
    }
}