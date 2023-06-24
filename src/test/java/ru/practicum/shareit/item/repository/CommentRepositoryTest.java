package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    Comment comment;
    User author;
    User owner;
    Item item;

    @BeforeEach
    private void addData() {
        owner = userRepository.save(User.builder()
                .name("otherName")
                .email("otherEmail@mail.ru")
                .build());
        item = itemRepository.save(Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build());
        author = userRepository.save(User.builder()
                .name("name")
                .email("email@mail.ru")
                .build());
        comment = commentRepository.save(Comment.builder()
                .text("text")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByItem() {
        List<Comment> comments = commentRepository.findAllByItem(item);

        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0));
    }

    @AfterEach
    private void deleteData() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }
}