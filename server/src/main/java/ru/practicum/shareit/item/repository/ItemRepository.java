package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderById(User owner);

    List<Item> findAllByOwnerOrderById(User owner, Pageable pageable);

    @Query("select it " +
            "from Item as it " +
            "where (upper(it.name) like upper(concat('%',?1, '%'))" +
            " or upper(it.description) like upper(concat('%',?1, '%'))) and" +
            " it.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequest(ItemRequest itemRequestId);
}
