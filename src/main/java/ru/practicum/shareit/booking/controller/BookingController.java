package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutForBookingController;
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
    public BookingDtoOutForBookingController saveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        Booking booking = BookingMapper.toBooking(bookingDtoIn);
        log.info("Booking is being created: {}", booking);
        return BookingMapper.toBookingDtoOutForBookingController(bookingService.saveBooking(userId, bookingDtoIn, booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutForBookingController updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable int bookingId,
                                                           @RequestParam String approved) {
        log.info("Booking is being updated");
        return BookingMapper.toBookingDtoOutForBookingController(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutForBookingController getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @PathVariable long bookingId) {
        return BookingMapper.toBookingDtoOutForBookingController(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public List<BookingDtoOutForBookingController> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                       @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDtoOutForBookingController> bookingsByBooker = new ArrayList<>();
        for (Booking booking : bookingService.getBookingsByBooker(userId, state)) {
            bookingsByBooker.add(BookingMapper.toBookingDtoOutForBookingController(booking));
        }
        return bookingsByBooker;
    }

    @GetMapping("/owner")
    public List<BookingDtoOutForBookingController> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                      @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDtoOutForBookingController> bookingsByOwner = new ArrayList<>();
        for (Booking booking : bookingService.getBookingsByOwner(userId, state)) {
            bookingsByOwner.add(BookingMapper.toBookingDtoOutForBookingController(booking));
        }
        return bookingsByOwner;
    }
}
