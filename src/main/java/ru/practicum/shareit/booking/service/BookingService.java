package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking saveBooking(long userId, BookingDtoIn bookingDtoIn, Booking booking);

    Booking updateBooking(long userId, long bookingId, String approved);

    Booking getBookingById(long userId, long bookingId);

    List<Booking> getBookingsByBooker(long userId, String state);

    List<Booking> getBookingsByOwner(long userId, String state);
}
