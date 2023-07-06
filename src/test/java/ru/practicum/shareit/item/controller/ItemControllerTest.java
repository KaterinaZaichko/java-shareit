package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;
    @MockBean
    private BookingRepository bookingRepository;

    @SneakyThrows
    @Test
    void getItemsByOwner() {
        Long ownerId = 0L;
        int from = 0;
        int size = 10;
        User owner = new User(1L, "name", "email@mail.ru");
        User requester = new User(2L, "otherName", "otherEmail@mail.ru");
        ItemRequest request = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, request);
        when(itemService.getItemsByOwner(ownerId, from, size)).thenReturn(List.of(item));
        when(itemService.getLastBookingByItem(item)).thenReturn(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, requester, Status.APPROVED));
        when(itemService.getNextBookingByItem(item)).thenReturn(new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, requester, Status.APPROVED));
        when(itemService.findCommentsByItem(item)).thenReturn(List.of(
                new Comment(1L, "text", item, requester, LocalDateTime.now())));

        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(itemService).getItemsByOwner(ownerId, from, size);
        verify(itemService, times(2)).getLastBookingByItem(item);
        verify(itemService, times(2)).getNextBookingByItem(item);
        verify(itemService).findCommentsByItem(item);
    }

    @SneakyThrows
    @Test
    void getItemsByOwner_whenRequestParamIsNotValid_thenReturnBadRequest() {
        Long ownerId = 0L;
        int from = -1;
        int size = 10;
        mockMvc.perform(get("/items?from=-1&size=10")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemsByOwner(ownerId, from, size);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        Long itemId = 0L;
        Long userId = 0L;
        User owner = new User(0L, "name", "email@mail.ru");
        User requester = new User(1L, "otherName", "otherEmail@mail.ru");
        ItemRequest request = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item itemToGetting = new Item(1L, "name", "description", true, owner, request);

        when(itemService.getItemById(itemId)).thenReturn(itemToGetting);
        when(itemService.getLastBookingByItem(itemToGetting))
                .thenReturn(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                        itemToGetting, requester, Status.APPROVED));
        when(itemService.getNextBookingByItem(itemToGetting))
                .thenReturn(new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                        itemToGetting, requester, Status.APPROVED));
        when(itemService.findCommentsByItem(itemToGetting)).thenReturn(List.of(
                new Comment(1L, "text", itemToGetting, requester, LocalDateTime.now())));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).getItemById(itemId);
    }

    @SneakyThrows
    @Test
    void saveItem() {
        User owner = new User(0L, "name", "email@mail.ru");
        ItemDto itemDtoIn = new ItemDto(null, "name", "description",
                true, null, null, null, null);
        ItemDto itemDtoOut = new ItemDto(0L, "name", "description",
                true, null, null, null, null);
        Item itemToCreate = new Item(0L, "name", "description",
                true, owner, null);
        when(itemService.saveItem(owner.getId(), itemDtoIn, ItemMapper.toItem(itemDtoIn))).thenReturn(itemToCreate);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(objectMapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoOut), result);
    }

    @SneakyThrows
    @Test
    void saveItem_whenItemIsNotValid_thenReturnBadRequest() {
        Long userId = 0L;
        ItemDto itemDto = new ItemDto(null, null, "description",
                true, null, null, null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).saveItem(anyLong(), eq(itemDto), eq(ItemMapper.toItem(itemDto)));
    }

    @SneakyThrows
    @Test
    void updateItem() {
        User owner = new User(0L, "name", "email@mail.ru");
        ItemDto itemDto = new ItemDto(0L, "updateName", "description",
                true, null, null, null, null);
        Item itemToUpdate = new Item(0L, "name", "description",
                true, owner, null);
        itemToUpdate.setName(itemDto.getName());
        when(itemService.updateItem(owner.getId(), itemDto.getId(), ItemMapper.toItem(itemDto))).thenReturn(itemToUpdate);

        String result = mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(ItemMapper.toItemDto(itemToUpdate)), result);
    }

    @SneakyThrows
    @Test
    void search() {
        String text = "text";
        int from = 0;
        int size = 10;
        User owner = new User(0L, "name", "email@mail.ru");
        User requester = new User(1L, "otherName", "otherEmail@mail.ru");
        ItemRequest request = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item itemToGetting = new Item(1L, "name", "description", true, owner, request);

        when(itemService.search(text, from, size)).thenReturn(List.of(itemToGetting));

        mockMvc.perform(get("/items/search?text=text&from=0&size=10"))
                .andExpect(status().isOk());

        verify(itemService).search(text, from, size);
    }

    @SneakyThrows
    @Test
    void saveComment() {
        long userId = 0L;
        long itemId = 0L;
        LocalDateTime now = LocalDateTime.now();
        User author = new User(0L, "name", "email@mail.ru");
        CommentDto commentDtoIn = new CommentDto(null, "text", null, null);
        CommentDto commentDtoOut = new CommentDto(0L, "text", "name", now);
        Comment commentToCreate = new Comment(0L, "text", new Item(), author, now);
        when(itemService.saveComment(userId, itemId, CommentMapper.toComment(commentDtoIn))).thenReturn(commentToCreate);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoOut), result);
    }

    @SneakyThrows
    @Test
    void saveComment_whenCommentIsNotValid_thenReturnBadRequest() {
        long itemId = 0L;
        CommentDto commentDto = new CommentDto(null, null, null, null);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).saveComment(anyLong(), anyLong(), any(Comment.class));
    }
}