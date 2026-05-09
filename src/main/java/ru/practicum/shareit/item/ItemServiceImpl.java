package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final UserService userService;
    private final ItemStorage itemStorage;

    public ItemDto getItemById(Long id) {
        Item item = itemStorage.getItemById(id);
        if (item == null) {
            throw new NotFoundException("Предмет не был найден");
        }

        return mapper.toItemDto(item);
    }

    public List<ItemDto> getItems(Long userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : itemStorage.getItems()) {
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

        return itemStorage.getItems().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getIsAvailable().equals(true))
                .map(mapper::toItemDto)
                .toList();
    }

    public ItemDto createItem(Item item, Long userId) {
        userService.checkUserExisting(userId);

        return mapper.toItemDto(itemStorage.createItem(item, userId));
    }

    public ItemDto changeItem(Long itemId, ItemDto itemDto, Long userId) {
        userService.checkUserExisting(userId);
        Item item = itemStorage.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет не был найден");
        }

        if (!item.getOwnerId().equals(userId)) {
            throw new AccessForbiddenException("Доступ запрещен");
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        }

        return mapper.toItemDto(item);
    }
}
