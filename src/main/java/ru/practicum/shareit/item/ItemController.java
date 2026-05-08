package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@PathVariable Long itemId,
                           @RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.changeItem(itemId, item, userId);
    }
}
