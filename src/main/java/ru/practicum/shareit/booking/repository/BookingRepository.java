package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(
            User booker, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBookerAndStatusAndEndBeforeOrderByStartDesc(
            User booker, Status approved, LocalDateTime now);

    List<Booking> findAllByBookerAndStatusInAndStartAfterOrderByStartDesc(
            User booker, Set<Status> future, LocalDateTime now);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, Status status);

    List<Booking> findAllByItemInOrderByStartDesc(Set<Item> items);

    List<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
            Set<Item> items, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
            Set<Item> items, Status approved, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusInAndStartAfterOrderByStartDesc(
            Set<Item> items, HashSet<Status> statuses, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(Set<Item> items, Status waiting);

    Booking findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(
            Item item, Status approved, LocalDateTime now);

    Booking findFirstByItemAndStatusAndStartAfterOrderByStart(
            Item item, Status approved, LocalDateTime now);

    Booking findFirstByItemAndBookerAndStatusAndEndBefore(
            Item item, User booker, Status approved, LocalDateTime now);
}
