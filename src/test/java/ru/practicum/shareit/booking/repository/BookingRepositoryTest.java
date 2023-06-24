package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    User booker;
    User owner;
    Item item;
    Item otherItem;
    Booking booking;
    Booking otherBooking;
    Booking thirdBooking;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    private void addData() {
        booker = userRepository.save(User.builder()
                .name("name")
                .email("email@mail.ru")
                .build());
        owner = userRepository.save(User.builder()
                .name("otherName")
                .email("otherEmail@mail.ru")
                .build());
        item = itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build());
        otherItem = itemRepository.save(Item.builder()
                .name("otherName")
                .description("otherDescription")
                .available(true)
                .owner(booker)
                .build());
        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build());
        otherBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
        thirdBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(otherItem)
                .booker(owner)
                .status(Status.APPROVED)
                .build());
    }

    @Test
    void findAllByBookerOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerOrderByStartDesc(booker, pageable);

        assertEquals(2, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(
                booker, LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findAllByBookerAndStatusAndEndBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusAndEndBeforeOrderByStartDesc(
                booker, Status.APPROVED, LocalDateTime.now(), pageable);

        assertEquals(0, bookings.size());
    }

    @Test
    void findAllByBookerAndStatusInAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusInAndStartAfterOrderByStartDesc(
                booker, Set.of(Status.APPROVED, Status.WAITING), LocalDateTime.now(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findAllByBookerAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(
                booker, Status.WAITING, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findAllByItemInOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemInOrderByStartDesc(Set.of(item), pageable);

        assertEquals(2, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
        assertEquals(booker, bookings.get(1).getBooker());
    }

    @Test
    void findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                Set.of(item), LocalDateTime.now(), LocalDateTime.now(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findAllByItemInAndStatusAndEndBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
                Set.of(otherItem), Status.APPROVED, LocalDateTime.now(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(owner, bookings.get(0).getBooker());
    }

    @Test
    void findAllByItemInAndStatusInAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemInAndStatusInAndStartAfterOrderByStartDesc(
                Set.of(item), new HashSet<>(Set.of(Status.APPROVED, Status.WAITING)), LocalDateTime.now(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findAllByItemInAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(
                Set.of(item), Status.WAITING, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booker, bookings.get(0).getBooker());
    }

    @Test
    void findFirstByItemAndStatusAndStartBeforeOrderByStartDesc() {
        Booking actualBooking = bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(
                item, Status.APPROVED, LocalDateTime.now());

        assertEquals(booking, actualBooking);
    }

    @Test
    void findFirstByItemAndStatusAndStartAfterOrderByStart() {
        Booking actualBooking = bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(
                otherItem, Status.APPROVED, LocalDateTime.now());

        assertNull(actualBooking);
    }

    @Test
    void findFirstByItemAndBookerAndStatusAndEndBefore() {
        Booking actualBooking = bookingRepository.findFirstByItemAndBookerAndStatusAndEndBefore(
                otherItem, owner, Status.APPROVED, LocalDateTime.now());

        assertEquals(thirdBooking, actualBooking);
    }

    @AfterEach
    private void deleteData() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}