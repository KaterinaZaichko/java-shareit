package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto saveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        log.info("Booking is being created: {}", booking);
        return BookingMapper.toBookingFullDto(bookingService.save(userId, bookingDto, booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable int bookingId,
                                        @RequestParam String approved) {
        log.info("Booking is being updated");
        return BookingMapper.toBookingFullDto(bookingService.update(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) {
        return BookingMapper.toBookingFullDto(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public List<BookingFullDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        List<BookingFullDto> bookingsByBooker = new ArrayList<>();
        for (Booking booking : bookingService.getBookingsByBooker(userId, state)) {
            bookingsByBooker.add(BookingMapper.toBookingFullDto(booking));
        }
        return bookingsByBooker;
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        List<BookingFullDto> bookingsByOwner = new ArrayList<>();
        for (Booking booking : bookingService.getBookingsByOwner(userId, state)) {
            bookingsByOwner.add(BookingMapper.toBookingFullDto(booking));
        }
        return bookingsByOwner;
    }
}
