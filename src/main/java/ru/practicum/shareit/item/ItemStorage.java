package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemStorage {
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private Long id = 0L;

    public Item getItemById(Long id) {
        Item item = itemStorage.get(id);
        if (item == null) {
            throw new NotFoundException("Предмет не был найден");
        }

        return item;
    }

    public Collection<Item> getItems(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    public List<Item> getItemByText(String text) {
        return itemStorage.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getIsAvailable().equals(true))
                .toList();
    }

    public Item createItem(Item item, Long userId) {
        id += 1L;
        item.setId(id);
        item.setOwnerId(userId);
        itemStorage.put(id, item);

        return item;
    }

    public Item changeItem(Long itemId, ItemChangeDto itemChangeDto, Long userId) {
        Item item = itemStorage.get(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет не был найден");
        }

        if (!item.getOwnerId().equals(userId)) {
            throw new AccessForbiddenException("Доступ запрещен");
        }
        if (itemChangeDto.getDescription() != null && !itemChangeDto.getDescription().isBlank()) {
            item.setDescription(itemChangeDto.getDescription());
        }
        if (itemChangeDto.getName() != null && !itemChangeDto.getName().isBlank()) {
            item.setName(itemChangeDto.getName());
        }
        if (itemChangeDto.getIsAvailable() != null) {
            item.setIsAvailable(itemChangeDto.getIsAvailable());
        }

        return item;
    }
}
