package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @GetMapping("/{itemId}")
    public ItemChangeDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemChangeDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemChangeDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @PostMapping
    public ItemCreateDto createItem(@RequestBody @Valid ItemCreateDto item,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemChangeDto changeItem(@PathVariable Long itemId,
                                    @RequestBody @Valid ItemChangeDto item,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.changeItem(itemId, item, userId);
    }
}
