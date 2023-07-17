package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating item request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.saveRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
