package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void saveRequest() {
        long userId = 0L;
        LocalDateTime now = LocalDateTime.now();
        User user = new User(0L, "name", "email@mail.ru");
        ItemRequestDto itemRequestDtoIn = new ItemRequestDto(null, "description", null, null);
        ItemRequestDto itemRequestDtoOut = new ItemRequestDto(0L, "description", now, null);
        ItemRequest itemRequestToCreate = new ItemRequest(0L, "description", user, now);
        when(itemRequestService.save(userId, itemRequestDtoIn))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequestToCreate));

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoOut), result);
    }

    @SneakyThrows
    @Test
    void saveRequest_whenItemRequestIsNotValid_thenReturnBadRequest() {
        long userId = 0L;
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, null, null, null);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).save(anyLong(), any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void getItemRequestsByRequestor() {
        long requestorId = 1L;
        User owner = new User(0L, "name", "email@mail.ru");
        User requester = new User(1L, "otherName", "otherEmail@mail.ru");
        ItemRequest request = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        when(itemRequestService.getAllByRequestor(requestorId))
                .thenReturn(List.of(ItemRequestMapper.toItemRequestDto(request)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requestorId))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllByRequestor(requestorId);
    }

    @SneakyThrows
    @Test
    void getItemRequests() {
        long requestorId = 1L;
        int from = 0;
        int size = 10;
        User owner = new User(0L, "name", "email@mail.ru");
        User requester = new User(1L, "otherName", "otherEmail@mail.ru");
        ItemRequest request = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        when(itemRequestService.getAllByRequestor(requestorId, from, size))
                .thenReturn(List.of(ItemRequestMapper.toItemRequestDto(request)));

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", requestorId))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllByRequestor(requestorId, from, size);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        long requestId = 0L;
        long userId = 0L;
        ItemRequest itemRequestToGetting = new ItemRequest(
                0L, "description", new User(), LocalDateTime.now());
        when(itemRequestService.getItemRequestById(userId, requestId))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequestToGetting));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestService).getItemRequestById(userId, requestId);
    }
}