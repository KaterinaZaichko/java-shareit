package ru.practicum.shareit.booking.exception;

public class StatusChangingNotAvailableException extends RuntimeException {
    public StatusChangingNotAvailableException(String message) {
        super(message);
    }
}
