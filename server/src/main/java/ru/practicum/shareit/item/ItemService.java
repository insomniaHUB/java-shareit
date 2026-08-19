package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemGetDto getItemById(Long id, Long userId);

    List<ItemBookingsDto> getItems(Long userId);

    List<ItemDto> getItemByText(String text);

    ItemDto createItem(ItemDto item, Long userId);

    ItemDto changeItem(Long itemId, ItemDto itemChangeDto, Long userId);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
