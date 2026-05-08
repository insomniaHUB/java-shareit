package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> getItemByText(String text);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto changeItem(Long itemId, ItemDto itemDto, Long userId);
}
