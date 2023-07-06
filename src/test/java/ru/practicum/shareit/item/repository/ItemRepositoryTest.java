package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User owner;
    User requester;
    ItemRequest request;
    ItemRequest otherRequest;

    @BeforeEach
    private void addData() {
        owner = userRepository.save(User.builder()
                .name("name")
                .email("email@mail.ru")
                .build());
        requester = userRepository.save(User.builder()
                .name("otherName")
                .email("otherEmail@mail.ru")
                .build());
        request = itemRequestRepository.save(ItemRequest.builder()
                .description("description")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build());
        otherRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("otherDescription")
                .requestor(owner)
                .created(LocalDateTime.now())
                .build());
        itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
        itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(requester)
                .request(otherRequest)
                .build());
    }

    @Test
    void findAllByOwnerOrderById() {
        List<Item> items = itemRepository.findAllByOwnerOrderById(owner);

        assertEquals(1, items.size());
        assertEquals(owner, items.get(0).getOwner());
    }

    @Test
    void testFindAllByOwnerOrderById() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findAllByOwnerOrderById(requester, pageable);

        assertEquals(1, items.size());
        assertEquals(requester, items.get(0).getOwner());
    }

    @Test
    void search() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.search("name", pageable);

        assertEquals(2, items.size());
        assertEquals("name", items.get(0).getName());
    }

    @Test
    void findAllByRequest() {
        List<Item> items = itemRepository.findAllByRequest(request);

        assertEquals(1, items.size());
        assertEquals(request, items.get(0).getRequest());
    }

    @AfterEach
    private void deleteData() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}