package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemStorage {
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private Long id = 0L;

    public Item getItemById(Long id) {
        return itemStorage.get(id);
    }

    public Collection<Item> getItems() {
        return itemStorage.values();
    }

    public Item createItem(Item item, Long userId) {
        id += 1L;
        item.setId(id);
        item.setOwnerId(userId);
        itemStorage.put(id, item);

        return item;
    }
}
