package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutForBookingController;
import ru.practicum.shareit.booking.dto.BookingDtoOutForItemController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public class BookingMapper {
    public static BookingDtoOutForBookingController toBookingDtoOutForBookingController(Booking booking) {
        return new BookingDtoOutForBookingController(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingDtoOutForItemController toBookingDtoOutForItemController(Booking booking) {
        return new BookingDtoOutForItemController(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn) {
        return new Booking(
                null,
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd(),
                null,
                null,
                Status.WAITING
        );
    }
}

