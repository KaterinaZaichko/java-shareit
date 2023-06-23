package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking save(long userId, BookingDto bookingDto, Booking booking);

    Booking update(long userId, long bookingId, String approved);

    Booking getBookingById(long userId, long bookingId);

    List<Booking> getBookingsByBooker(long userId, String state);

    List<Booking> getBookingsByOwner(long userId, String state);
}
