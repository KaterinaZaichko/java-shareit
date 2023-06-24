package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void saveBooking() {
        User booker = new User(0L, "name", "email@mail.ru");
        User owner = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        long bookerId = 0L;
        long itemId = 0L;
        BookingDto bookingDtoIn = new BookingDto(null, start, end, itemId, null, null);
        BookingFullDto bookingDtoOut = new BookingFullDto(0L, start, end, item, booker, Status.WAITING);
        Booking bookingToCreate = new Booking(0L, start, end, item, booker, Status.WAITING);
        when(bookingService.save(bookerId, bookingDtoIn,
                BookingMapper.toBooking(bookingDtoIn))).thenReturn(bookingToCreate);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoOut), result);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        long bookingId = 0L;
        long itemId = 0L;
        long ownerId = 0L;
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(null, start, end, itemId, null, null);
        Booking bookingToUpdate = new Booking(0L, start, end, item, booker, Status.WAITING);
        bookingToUpdate.setStatus(Status.APPROVED);
        when(bookingService.update(ownerId, bookingId, "true")).thenReturn(bookingToUpdate);

        String result = mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(BookingMapper.toBookingFullDto(bookingToUpdate)), result);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        long bookingId = 0L;
        long itemId = 0L;
        long ownerId = 0L;
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(0L, "name", "description", true, owner, null);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking bookingToGetting = new Booking(0L, start, end, item, booker, Status.WAITING);

        when(bookingService.getBookingById(ownerId, bookingId)).thenReturn(bookingToGetting);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(bookingService).getBookingById(ownerId, bookingId);
    }

    @SneakyThrows
    @Test
    void getBookingsByBooker() {
        long bookerId = 0L;
        String state = "current";
        int from = 0;
        int size = 10;
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now(), item, booker, Status.APPROVED);
        when(bookingService.getBookingsByBooker(bookerId, state, from, size)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings?state=current&from=0&size=10")
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk());

        verify(bookingService).getBookingsByBooker(bookerId, state, from, size);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwner() {
        long ownerId = 0L;
        String state = "current";
        int from = 0;
        int size = 10;
        User owner = new User(0L, "name", "email@mail.ru");
        User booker = new User(1L, "otherName", "otherEmail@mail.ru");
        Item item = new Item(1L, "name", "description", true, owner, null);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now(), item, booker, Status.APPROVED);
        when(bookingService.getBookingsByOwner(ownerId, state, from, size)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner?state=current&from=0&size=10")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(bookingService).getBookingsByOwner(ownerId, state, from, size);
    }
}