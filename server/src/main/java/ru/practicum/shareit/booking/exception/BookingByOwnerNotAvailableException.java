package ru.practicum.shareit.booking.exception;

public class BookingByOwnerNotAvailableException extends RuntimeException {
    public BookingByOwnerNotAvailableException(String message) {
        super(message);
    }
}
