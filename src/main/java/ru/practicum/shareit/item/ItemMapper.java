package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .build();
    }

    public ItemGetDto toItemGetDto(Item item, List<CommentDto> comments) {
        return ItemGetDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .comments(comments)
                .build();
    }

    public ItemBookingsDto toItemBookingDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        return ItemBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public Item toItemFromDto(ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .isAvailable(dto.getIsAvailable())
                .build();
    }

    public Item toItemFromChangeDto(ItemChangeDto item, Long ownerId) {
        Item newItem = new Item();
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setIsAvailable(item.getIsAvailable());

        return newItem;
    }

    public Item toItemFromCreateDto(ItemCreateDto item, User owner) {
        Item newItem = new Item();
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setIsAvailable(item.getIsAvailable());
        newItem.setOwner(owner);

        return newItem;
    }
}
