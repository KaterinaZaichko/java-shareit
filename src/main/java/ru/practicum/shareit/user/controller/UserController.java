package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userService.getUsers()) {
            users.add(UserMapper.toUserDto(user));
        }
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @PostMapping
    public UserDto saveUser(@RequestBody @Valid UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("User is being created: {}", user);
        return UserMapper.toUserDto(userService.saveUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("User is being updated: {}", userId);
        return UserMapper.toUserDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("User had been deleted: {}", userId);
    }
}
