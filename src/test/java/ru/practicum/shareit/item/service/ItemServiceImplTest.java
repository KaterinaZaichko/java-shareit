package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UpdateNotAvailableException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void getItemsByOwnerWithoutPagination_whenOwnerFound_thenReturnListOfItems() {
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(
                new Item(0L, "name", "description", true, owner, null));
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.findAllByOwnerOrderById(owner)).thenReturn(items);

        List<Item> actualItems = itemService.getItemsByOwner(owner.getId());

        assertEquals(items, actualItems);
    }

    @Test
    void testGetItemsByOwnerWithPagination_whenOwnerFound_thenReturnListOfItems() {
        Pageable pageable = PageRequest.of(0, 10);
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(
                new Item(0L, "name", "description", true, owner, null));
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.findAllByOwnerOrderById(owner, pageable)).thenReturn(items);

        List<Item> actualItems = itemService.getItemsByOwner(owner.getId(), 0, 10);

        assertEquals(items, actualItems);
    }

    @Test
    void getItemById_whenItemFound_thenReturnItem() {
        long itemId = 0L;
        Item expectedItem = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        Item actualItem = itemService.getItemById(itemId);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    void getItemById_whenItemNotFound_thenItemNotFoundExceptionThrown() {
        long itemId = 0L;
        when(itemRepository.findById(itemId)).thenThrow(new ItemNotFoundException("Ошибка"));

        assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(itemId));
    }

    @Test
    void saveItem_whenItemValid_thenSavedItem() {
        User owner = new User(0L, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(0L, "description", new User(), LocalDateTime.now());
        ItemDto itemDto = new ItemDto(null, "name", "description",
                true, itemRequest.getId(), null, null, null);
        Item itemToSave = new Item(0L, "name", "description", true, null, itemRequest);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.save(itemToSave)).thenReturn(itemToSave);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));

        Item actualItem = itemService.saveItem(owner.getId(), itemDto, itemToSave);

        assertEquals(itemToSave, actualItem);
        verify(itemRepository).save(itemToSave);
    }

    @Test
    void updateItem_whenItemFoundAndUserIsOwner_thenReturnUpdatedItem() {
        User owner = new User(0L, "name", "email@mail.ru");
        long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(owner);

        Item newItem = new Item();
        newItem.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        itemService.updateItem(owner.getId(), itemId, newItem);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals("name", savedItem.getName());
        assertEquals("description", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateItem_whenItemFoundAndUserIsNotOwner_thenUpdateNotAvailableExceptionThrown() {
        long userId = 0L;
        User user = new User();
        long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setOwner(new User(1L, "name", "email@mail.ru"));

        Item newItem = new Item();
        newItem.setName("updateName");
        newItem.setDescription("updateDescription");
        newItem.setAvailable(false);
        when(userService.getUserById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        assertThrows(UpdateNotAvailableException.class,
                () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void search_whenTextIsNotEmpty_thenReturnListOfItems() {
        String text = "text";
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = List.of(new Item());
        when(itemRepository.search(text, pageable)).thenReturn(items);

        List<Item> actualItems = itemService.search(text, 0, 10);

        assertEquals(items, actualItems);
    }

    @Test
    void search_whenTextIsEmpty_thenReturnEmptyListOfItems() {
        String text = "";

        List<Item> actualItems = itemService.search(text, 0, 10);

        assertEquals(new ArrayList<>(), actualItems);
    }

    @Test
    void getLastBookingByItem() {
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, booker, Status.APPROVED);
        when(bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(
                eq(item), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(booking);

        Booking actualBooking = itemService.getLastBookingByItem(item);

        assertEquals(booking, actualBooking);
    }

    @Test
    void getNextBookingByItem() {
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.APPROVED);
        when(bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(
                eq(item), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(booking);

        Booking actualBooking = itemService.getNextBookingByItem(item);

        assertEquals(booking, actualBooking);
    }

    @Test
    void saveComment_whenUserAndItemAndBookingFound_thenReturnComment() {
        User author = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, new User(), null);
        Comment commentToSave = new Comment(0L, "text", null, null, null);
        Booking booking = new Booking(0L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, author, Status.APPROVED);
        when(userService.getUserById(author.getId())).thenReturn(author);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemAndBookerAndStatusAndEndBefore(
                eq(item), eq(author), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(booking);
        when(commentRepository.save(commentToSave)).thenReturn(commentToSave);

        Comment actualComment = itemService.saveComment(author.getId(), item.getId(), commentToSave);

        assertEquals(commentToSave, actualComment);
        verify(commentRepository).save(commentToSave);
    }

    @Test
    void saveComment_whenUserAndItemFoundButBookingNotFound_thenCommentNotAvailableExceptionThrown() {
        User author = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, new User(), null);
        Comment commentToSave = new Comment(0L, "text", null, null, null);
        when(userService.getUserById(author.getId())).thenReturn(author);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemAndBookerAndStatusAndEndBefore(
                eq(item), eq(author), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(null);

        assertThrows(CommentNotAvailableException.class,
                () -> itemService.saveComment(author.getId(), item.getId(), commentToSave));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void findCommentsByItem_forever_thenReturnListOfComment() {
        Item item = new Item();
        List<Comment> comments = List.of(new Comment(0L, "text", item, new User(), LocalDateTime.now()));
        when(commentRepository.findAllByItem(item)).thenReturn(comments);

        List<Comment> actualComments = itemService.findCommentsByItem(item);

        assertEquals(comments, actualComments);
    }

    @Test
    void findItemsByRequest_forever_thenReturnListOfItems() {
        ItemRequest itemRequest = new ItemRequest();
        List<Item> items = List.of(new Item());
        when(itemRepository.findAllByRequest(itemRequest)).thenReturn(items);

        List<Item> actualItems = itemService.findItemsByRequest(itemRequest);

        assertEquals(items, actualItems);
    }
}