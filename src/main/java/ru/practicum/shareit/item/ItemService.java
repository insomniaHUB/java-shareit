package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemGetDto getItemById(Long id, Long userId);

    List<ItemBookingsDto> getItems(Long userId);

    List<ItemDto> getItemByText(String text);

    ItemDto createItem(ItemCreateDto item, Long userId);

    ItemDto changeItem(Long itemId, ItemChangeDto itemChangeDto, Long userId);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
