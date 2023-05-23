package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findUsers();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        return userRepository.update(id, user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.remove(id);
    }
}
