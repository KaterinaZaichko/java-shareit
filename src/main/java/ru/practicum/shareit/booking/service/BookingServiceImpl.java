package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking saveBooking(long userId, BookingDtoIn bookingDtoIn, Booking booking) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingDtoIn.getItemId());
        if (item.getOwner().equals(booker)) {
            throw new BookingByOwnerNotAvailableException(
                    String.format("User with id %d is owner of item with id %d", userId, item.getId()));

        }
        if (item.getAvailable().equals(false)) {
            throw new BookingNotAvailableException(
                    String.format("Item with id %d is not available for booking", item.getId()));
        }
        if (booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().equals(booking.getEnd())) {
            throw new DateSequenceException("End date must not be earlier than start date, dates must not be null");
        }
        booking.setItem(item);
        booking.setBooker(booker);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(long userId, long bookingId, String approved) {
        userService.getUserById(userId);
        Booking updatedBooking = getBookingById(userId, bookingId);
        Item item = updatedBooking.getItem();
        if (updatedBooking.getStatus().equals(Status.APPROVED)) {
            throw new StatusChangingNotAvailableException("Booking is confirmed, status cannot be changed");
        }
        if (item.getOwner().getId().equals(userId)) {
            switch (approved.toLowerCase()) {
                case ("true"):
                    updatedBooking.setStatus(Status.APPROVED);
                    break;
                case ("false"):
                    updatedBooking.setStatus(Status.REJECTED);
                    break;
                default:
                    throw new UpdateNotAvailableException("Booking confirmation status must be TRUE or FALSE");
            }
            return bookingRepository.save(updatedBooking);
        } else {
            throw new UpdateNotAvailableException(
                    String.format("User with id %d is not owner of item with id %d", userId, item.getId()));
        }
    }

    @Override
    public Booking getBookingById(long userId, long bookingId) {
        userService.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Booking with id %d not found", bookingId))
        );
        Item item = booking.getItem();
        if (item.getOwner().getId().equals(userId)
                || booking.getBooker().getId().equals(userId)) {
            return booking;
        } else {
            throw new GettingNotAvailableException(
                    String.format("User with id %d is not owner or booker of item with id %d", userId, item.getId()));
        }
    }

    @Override
    public List<Booking> getBookingsByBooker(long userId, String state) {
        User booker = userService.getUserById(userId);
        if (state.equals("ALL")) {
            return bookingRepository.findAllByBookerOrderByStartDesc(booker);
        } else {
            State valueState;
            try {
                valueState = State.valueOf(state);
                switch (valueState) {
                    case CURRENT:
                        return bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(
                                booker, LocalDateTime.now(), LocalDateTime.now());
                    case PAST:
                        return bookingRepository.findAllByBookerAndStatusAndEndBeforeOrderByStartDesc(
                                booker, Status.APPROVED, LocalDateTime.now());
                    case FUTURE:
                        return bookingRepository.findAllByBookerAndStatusInAndStartAfterOrderByStartDesc(
                                booker, new HashSet<>(Arrays.asList(Status.APPROVED, Status.WAITING)), LocalDateTime.now());
                    case WAITING:
                        return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, Status.WAITING);
                    case REJECTED:
                        return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, Status.REJECTED);
                    default:
                        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
                }
            } catch (IllegalArgumentException e) {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
    }

    @Override
    public List<Booking> getBookingsByOwner(long userId, String state) {
        userService.getUserById(userId);
        Set<Item> items = new HashSet<>(itemService.getItemsByOwner(userId));
        if (state.equals("ALL")) {
            return bookingRepository.findAllByItemInOrderByStartDesc(items);
        } else {
            State valueState;
            try {
                valueState = State.valueOf(state);
                switch (valueState) {
                    case CURRENT:
                        return bookingRepository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                                items, LocalDateTime.now(), LocalDateTime.now());
                    case PAST:
                        return bookingRepository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
                                items, Status.APPROVED, LocalDateTime.now());
                    case FUTURE:
                        return bookingRepository.findAllByItemInAndStatusInAndStartAfterOrderByStartDesc(items,
                                new HashSet<>(Arrays.asList(Status.APPROVED, Status.WAITING)), LocalDateTime.now());
                    case WAITING:
                        return bookingRepository.findAllByItemInAndStatusOrderByStartDesc(items, Status.WAITING);
                    case REJECTED:
                        return bookingRepository.findAllByItemInAndStatusOrderByStartDesc(items, Status.REJECTED);
                    default:
                        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
                }
            } catch (IllegalArgumentException e) {
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
    }
}
