package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final UserService userService;
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private Long id = 0L;

    public ItemDto getItemById(Long id) {
        Item item = itemStorage.get(id);
        return mapper.toItemDto(item);
    }

    public List<ItemDto> getItems(Long userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : itemStorage.values()) {
            if (item.getOwnerId().equals(userId)) {
                items.add(item);
            }
        }

        return items.stream().map(mapper::toItemDto).toList();
    }

    public List<ItemDto> getItemByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemStorage.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getIsAvailable().equals(true))
                .map(mapper::toItemDto)
                .toList();
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        validateItem(itemDto);
        userService.checkUserExisting(userId);
        id += 1L;
        Item item = mapper.toItem(itemDto, id, userId);
        itemStorage.put(id, item);

        return mapper.toItemDto(item);
    }

    public ItemDto changeItem(Long itemId, ItemDto itemDto, Long userId) {
        checkExisting(itemId);
        userService.checkUserExisting(userId);
        Item item = itemStorage.get(itemId);

        if (item.getOwnerId().equals(userId)) {
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getIsAvailable() != null) {
                item.setIsAvailable(itemDto.getIsAvailable());
            }
        } else {
            throw new AccessForbiddenException("Доступ запрещен");
        }

        return mapper.toItemDto(item);
    }

    private void validateItem(ItemDto item) {
        if (item.getDescription() == null || item.getDescription().isBlank()
                || item.getName() == null || item.getName().isBlank()
                || item.getIsAvailable() == null) {
            throw new ValidationException("Ошибка валидации предмета");
        }
    }

    private void checkExisting(Long id) {
        Item item = itemStorage.get(id);

        if (item == null) {
            throw new NotFoundException("Предмет не был найден");
        }
    }
}
