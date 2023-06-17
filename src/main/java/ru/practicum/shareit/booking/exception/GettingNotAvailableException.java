package ru.practicum.shareit.booking.exception;

public class GettingNotAvailableException extends RuntimeException{
    public GettingNotAvailableException(String message) {
        super(message);
    }
}
