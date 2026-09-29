package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto requestDto, User user) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto toRequestDto(ItemRequest request, List<ItemInRequestDto> items) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .description(request.getDescription())
                .userId(request.getRequestor().getId())
                .items(items)
                .build();
    }
}
