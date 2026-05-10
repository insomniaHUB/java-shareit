package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.util.List;

public interface ItemService {
    ItemChangeDto getItemById(Long id);

    List<ItemChangeDto> getItems(Long userId);

    List<ItemChangeDto> getItemByText(String text);

    ItemCreateDto createItem(ItemCreateDto item, Long userId);

    ItemChangeDto changeItem(Long itemId, ItemChangeDto itemChangeDto, Long userId);
}
