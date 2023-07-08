package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(Long id);

    User saveUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long id);
}
