package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemChangeDto toItemChangeDto(Item item) {
        return new ItemChangeDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable());
    }

    public ItemCreateDto toItemCreateDto(Item item) {
        return new ItemCreateDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable());
    }

    public Item toItemFromChangeDto(ItemChangeDto item, Long ownerId) {
        Item newItem = new Item();
        newItem.setOwnerId(ownerId);
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setIsAvailable(item.getIsAvailable());

        return newItem;
    }

    public Item toItemFromCreateDto(ItemCreateDto item, Long ownerId) {
        Item newItem = new Item();
        newItem.setOwnerId(ownerId);
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setIsAvailable(item.getIsAvailable());

        return newItem;
    }
}
