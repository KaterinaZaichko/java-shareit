package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void getUsers() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getUsers();
    }

    @SneakyThrows
    @Test
    void getUserById() {
        Long userId = 0L;
        User userToGetting = new User(0L, "name", "email@mail.ru");
        when(userService.getUserById(userId)).thenReturn(userToGetting);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void saveUser() {
        UserDto userDtoIn = new UserDto(null, "name", "email@mail.ru");
        UserDto userDtoOut = new UserDto(0L, "name", "email@mail.ru");
        User userToCreate = new User(0L, "name", "email@mail.ru");
        when(userService.saveUser(UserMapper.toUser(userDtoIn))).thenReturn(userToCreate);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoOut), result);
    }

    @SneakyThrows
    @Test
    void saveUser_whenUserIsNotValid_thenReturnBadRequest() {
        UserDto userDto = new UserDto(null, "name", "email");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(User.class));
    }

    @SneakyThrows
    @Test
    void updateUser() {
        Long userId = 0L;
        UserDto userDto = new UserDto(0L, "updateName", null);
        User userToUpdate = new User(0L, "name", "email@mail.ru");
        userToUpdate.setName(userDto.getName());
        when(userService.updateUser(userId, UserMapper.toUser(userDto))).thenReturn(userToUpdate);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(UserMapper.toUserDto(userToUpdate)), result);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 0L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}

