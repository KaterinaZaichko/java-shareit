package ru.practicum.shareit.booking.exception;

public class BookingNotAvailableException extends RuntimeException{
    public BookingNotAvailableException(String message) {
        super(message);
    }
}

