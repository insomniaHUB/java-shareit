package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestParam String text,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemByText(text, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemCreateDto item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(@PathVariable Long itemId,
                              @RequestBody @Valid ItemChangeDto item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.changeItem(itemId, item, userId);
    }
}
