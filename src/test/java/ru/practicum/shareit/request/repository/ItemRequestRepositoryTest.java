package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User requestor;
    User otherRequestor;
    ItemRequest request;
    ItemRequest otherRequest;

    @BeforeEach
    private void addData() {
        requestor = userRepository.save(User.builder()
                .name("name")
                .email("email@mail.ru")
                .build());
        otherRequestor = userRepository.save(User.builder()
                .name("otherName")
                .email("otherEmail@mail.ru")
                .build());
        request = itemRequestRepository.save(ItemRequest.builder()
                .description("description")
                .requestor(otherRequestor)
                .created(LocalDateTime.now())
                .build());
        otherRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("otherDescription")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByRequestorOrderByCreated() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorOrderByCreated(requestor);

        assertEquals(1, itemRequests.size());
        assertEquals(requestor, itemRequests.get(0).getRequestor());
    }

    @Test
    void findAllByRequestorNot() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorNot(requestor, pageable);

        assertEquals(1, itemRequests.size());
        assertEquals(otherRequestor, itemRequests.get(0).getRequestor());
    }

    @AfterEach
    private void deleteData() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}