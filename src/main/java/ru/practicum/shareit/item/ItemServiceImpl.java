package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final UserService userService;
    private final ItemStorage itemStorage;

    public ItemChangeDto getItemById(Long id) {
        return mapper.toItemChangeDto(itemStorage.getItemById(id));
    }

    public List<ItemChangeDto> getItems(Long userId) {
        return itemStorage.getItems(userId).stream().map(mapper::toItemChangeDto).toList();
    }

    public List<ItemChangeDto> getItemByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemStorage.getItemByText(text).stream()
                .map(mapper::toItemChangeDto)
                .toList();
    }

    public ItemCreateDto createItem(ItemCreateDto itemDto, Long userId) {
        userService.checkUserExisting(userId);
        Item item = mapper.toItemFromCreateDto(itemDto, userId);

        return mapper.toItemCreateDto(itemStorage.createItem(item, userId));
    }

    public ItemChangeDto changeItem(Long itemId, ItemChangeDto itemChangeDto, Long userId) {
        userService.checkUserExisting(userId);

        return mapper.toItemChangeDto(itemStorage.changeItem(itemId, itemChangeDto, userId));
    }
}
