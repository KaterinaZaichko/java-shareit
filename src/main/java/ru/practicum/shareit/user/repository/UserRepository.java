package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findUsers();

    User findUserById(Long id);

    User createUser(User user);

    User update(Long id, User user);

    void remove(Long id);
}
