package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailNotUniqueException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users;
    private int usersCount = 0;

    public long generateId() {
        return ++usersCount;
    }

    @Override
    public List<User> findUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException(String.format("User with id %d not found", id));
        }
    }

    @Override
    public User createUser(User user) {
        for (User member : users.values()) {
            if (member.getEmail().equals(user.getEmail())) {
                throw new EmailNotUniqueException(
                        String.format("The user with email %s already exists", user.getEmail()));
            }
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        if (users.containsKey(id)) {
            User updateUser = users.get(id);
            if (user.getName() != null) {
                updateUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                for (User member : users.values()) {
                    if (member.equals(updateUser)) {
                        continue;
                    }
                    if (member.getEmail().equals(user.getEmail())) {
                        throw new EmailNotUniqueException(
                                String.format("The user with email %s already exists", user.getEmail()));
                    }
                }
                updateUser.setEmail(user.getEmail());
            }
            return updateUser;
        } else {
            throw new UserNotFoundException(String.format("User with id %d not found", user.getId()));
        }
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
    }
}
