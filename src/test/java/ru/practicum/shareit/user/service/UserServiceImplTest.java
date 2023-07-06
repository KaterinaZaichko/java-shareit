package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getUsers_forever_thenReturnListOfUsers() {
        List<User> users = List.of(new User(0L, "name", "email@mail.ru"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> actualUsers = userService.getUsers();

        assertEquals(users, actualUsers);
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        long userId = 0L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException(
                String.format("User with id %d not found", userId)));

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void saveUser_whenUserValid_thenSavedUser() {
        User userToSave = new User();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.saveUser(userToSave);

        assertEquals(userToSave, actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void updateUser_whenUserFound_thenUpdatedUser() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("email@mail.ru");

        User newUser = new User();
        newUser.setName("updateName");
//        newUser.setEmail("updateEmail@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.updateUser(userId, newUser);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("updateName", savedUser.getName());
        assertEquals("email@mail.ru", savedUser.getEmail());
    }

    @Test
    void deleteUser() {
        long userId = 0L;

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }
}