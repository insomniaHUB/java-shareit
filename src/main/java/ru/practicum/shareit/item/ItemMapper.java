package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable());
    }

    public Item toItem(ItemDto item, Long itemId, Long ownerId) {
        return new Item(itemId,
                ownerId,
                item.getName(),
                item.getDescription(),
                item.getIsAvailable());
    }
}
