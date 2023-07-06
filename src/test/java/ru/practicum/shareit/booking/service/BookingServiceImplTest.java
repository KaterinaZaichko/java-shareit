package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.UpdateNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void save_whenBookingIsValid_thenReturnBooking() {
        User booker = new User(0L, "name", "email@mail.ru");
        User owner = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        BookingDto bookingDto = new BookingDto(0L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item.getId(), owner.getId(), null);
        Booking bookingToSave = new Booking(0L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, owner, Status.APPROVED);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(itemService.getItemById(bookingDto.getItemId())).thenReturn(item);
        when(bookingRepository.save(bookingToSave)).thenReturn(bookingToSave);

        Booking actualBooking = bookingService.save(booker.getId(), bookingDto, bookingToSave);

        assertEquals(bookingToSave, actualBooking);
        verify(bookingRepository).save(bookingToSave);
    }

    @Test
    void save_whenOwnerAndBookerAreEquals_thenBookingByOwnerNotAvailableExceptionThrown() {
        User booker = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, booker, null);
        BookingDto bookingDto = new BookingDto(0L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item.getId(), booker.getId(), null);
        Booking bookingToSave = new Booking(0L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, Status.APPROVED);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(itemService.getItemById(bookingDto.getItemId())).thenReturn(item);

        assertThrows(BookingByOwnerNotAvailableException.class,
                () -> bookingService.save(booker.getId(), bookingDto, bookingToSave));
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void save_whenAvailableOfBookingIsNotTrue_thenBookingNotAvailableExceptionThrown() {
        User booker = new User(0L, "name", "email@mail.ru");
        User owner = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", false, owner, null);
        BookingDto bookingDto = new BookingDto(0L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item.getId(), owner.getId(), null);
        Booking bookingToSave = new Booking(0L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, owner, Status.APPROVED);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(itemService.getItemById(bookingDto.getItemId())).thenReturn(item);

        assertThrows(BookingNotAvailableException.class,
                () -> bookingService.save(booker.getId(), bookingDto, bookingToSave));
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void save_whenStartIsAfterEnd_thenDateSequenceExceptionThrown() {
        User booker = new User(0L, "name", "email@mail.ru");
        User owner = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        BookingDto bookingDto = new BookingDto(0L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(2), item.getId(), owner.getId(), null);
        Booking bookingToSave = new Booking(0L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(2), item, owner, Status.APPROVED);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(itemService.getItemById(bookingDto.getItemId())).thenReturn(item);

        assertThrows(DateSequenceException.class,
                () -> bookingService.save(booker.getId(), bookingDto, bookingToSave));
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void save_whenStartAndEndAreEquals_thenDateSequenceExceptionThrown() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User(0L, "name", "email@mail.ru");
        User owner = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        BookingDto bookingDto = new BookingDto(0L, now, now, item.getId(), owner.getId(), null);
        Booking bookingToSave = new Booking(0L, now, now, item, owner, Status.APPROVED);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(itemService.getItemById(bookingDto.getItemId())).thenReturn(item);

        assertThrows(DateSequenceException.class,
                () -> bookingService.save(booker.getId(), bookingDto, bookingToSave));
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void update_whenBookingFoundAndAllConditionsIsValid_thenReturnApprovedBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking bookingToUpdate = new Booking(0L, start, end, item, owner, Status.WAITING);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(bookingRepository.findById(bookingToUpdate.getId())).thenReturn(Optional.of(bookingToUpdate));
        when(bookingRepository.save(bookingToUpdate)).thenReturn(bookingToUpdate);

        bookingService.update(owner.getId(), bookingToUpdate.getId(), "true");

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }

    @Test
    void update_whenBookingFoundAndAllConditionsIsValid_thenReturnRejectedBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking bookingToUpdate = new Booking(0L, start, end, item, owner, Status.WAITING);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(bookingRepository.findById(bookingToUpdate.getId())).thenReturn(Optional.of(bookingToUpdate));
        when(bookingRepository.save(bookingToUpdate)).thenReturn(bookingToUpdate);

        bookingService.update(owner.getId(), bookingToUpdate.getId(), "false");

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(Status.REJECTED, savedBooking.getStatus());
    }

    @Test
    void update_whenStatusIsApproved_thenStatusChangingNotAvailableExceptionThrown() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking bookingToUpdate = new Booking(0L, start, end, item, owner, Status.APPROVED);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(bookingRepository.findById(bookingToUpdate.getId())).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(StatusChangingNotAvailableException.class,
                () -> bookingService.update(owner.getId(), bookingToUpdate.getId(), "true"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void update_whenUserIsNotOwner_thenUpdateNotAvailableExceptionThrown() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking bookingToUpdate = new Booking(0L, start, end, item, booker, Status.WAITING);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findById(bookingToUpdate.getId())).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(UpdateNotAvailableException.class,
                () -> bookingService.update(booker.getId(), bookingToUpdate.getId(), "true"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void update_ApprovedIsNotValid_thenUpdateNotAvailableExceptionThrown() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking bookingToUpdate = new Booking(0L, start, end, item, owner, Status.WAITING);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(bookingRepository.findById(bookingToUpdate.getId())).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(UpdateNotAvailableException.class,
                () -> bookingService.update(owner.getId(), bookingToUpdate.getId(), ""));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_whenUserIsOwnerOrBookerOfItem_thenReturnBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, start, end, item, owner, Status.WAITING);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.getBookingById(owner.getId(), booking.getId());

        assertEquals(booking, actualBooking);
    }

    @Test
    void getBookingById_whenBookingNotFound_thenBookingNotFoundExceptionThrown() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "bookerName", "bookerEmail@mail.ru");
        User otherUser = new User(2L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, start, end, item, booker, Status.WAITING);
        when(userService.getUserById(otherUser.getId())).thenReturn(otherUser);
        when(bookingRepository.findById(booking.getId())).thenThrow(new BookingNotFoundException(
                String.format("Booking with id %d not found", booking.getId())));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(otherUser.getId(), booking.getId()));
    }

    @Test
    void getBookingById_whenUserIsNotOwnerOrBookerOfItem_thenGettingNotAvailableExceptionThrown() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "bookerName", "bookerEmail@mail.ru");
        User otherUser = new User(2L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, start, end, item, booker, Status.WAITING);
        when(userService.getUserById(otherUser.getId())).thenReturn(otherUser);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(GettingNotAvailableException.class,
                () -> bookingService.getBookingById(otherUser.getId(), booking.getId()));
    }

    @Test
    void getBookingsByBooker_whenAllStates_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "ALL";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker, pageable)).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByBooker(booker.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByBooker_whenStateIsCurrent_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "current";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(
                eq(booker), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByBooker(booker.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByBooker_whenStateIsPast_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "past";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findAllByBookerAndStatusAndEndBeforeOrderByStartDesc(
                eq(booker), eq(Status.APPROVED), any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByBooker(booker.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByBooker_whenStateIsFuture_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "future";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findAllByBookerAndStatusInAndStartAfterOrderByStartDesc(
                eq(booker), eq(new HashSet<>(Arrays.asList(Status.APPROVED, Status.WAITING))),
                any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByBooker(booker.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByBooker_whenStateIsWaiting_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "waiting";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(
                eq(booker), eq(Status.WAITING), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByBooker(booker.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByBooker_whenStateIsRejected_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "rejected";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(booker.getId())).thenReturn(booker);
        when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(
                eq(booker), eq(Status.REJECTED), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByBooker(booker.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByBooker_whenStateIsUnknown_thenUnsupportedStatusExceptionThrown() {
        User booker = new User(0L, "name", "email@mail.ru");
        String state = "unknown";
        when(userService.getUserById(booker.getId())).thenReturn(booker);

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getBookingsByBooker(booker.getId(), state, 0, 10));
    }

    @Test
    void getBookingsByOwner_whenAllStates_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "ALL";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemInOrderByStartDesc(new HashSet<>(items), pageable)).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByOwner(owner.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByOwner_whenStateIsCurrent_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "current";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(eq(new HashSet<>(items)),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByOwner(owner.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByOwner_whenStateIsPast_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "past";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(eq(new HashSet<>(items)),
                eq(Status.APPROVED), any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByOwner(owner.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByOwner_whenStateIsFuture_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "future";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusInAndStartAfterOrderByStartDesc(
                eq(new HashSet<>(items)), eq(new HashSet<>(Arrays.asList(Status.APPROVED, Status.WAITING))),
                any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByOwner(owner.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByOwner_whenStateIsWaiting_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "waiting";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusOrderByStartDesc(eq(new HashSet<>(items)),
                eq(Status.WAITING), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByOwner(owner.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByOwner_whenStateIsRejected_thenReturnListOfBookings() {
        List<Booking> bookings = List.of(new Booking());
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "rejected";
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusOrderByStartDesc(eq(new HashSet<>(items)),
                eq(Status.REJECTED), eq(pageable))).thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getBookingsByOwner(owner.getId(), state, 0, 10);

        assertEquals(bookings, actualBookings);
    }

    @Test
    void getBookingsByOwner_whenStateIsUnknown_thenUnsupportedStatusExceptionThrown() {
        User owner = new User(0L, "name", "email@mail.ru");
        List<Item> items = List.of(new Item());
        String state = "unknown";
        when(userService.getUserById(owner.getId())).thenReturn(owner);
        when(itemService.getItemsByOwner(owner.getId())).thenReturn(items);

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getBookingsByOwner(owner.getId(), state, 0, 10));

    }
}