package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDate startRent;
    private LocalDate endRent;
    private Long item;
    private Status status;
}
