package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("Kirill")
                .email("owner@mail.com")
                .build();
        entityManager.persistAndFlush(owner);
    }

    @Test
    void findByText() {
        Item item1 = Item.builder()
                .name("Chainsaw")
                .description("Big")
                .isAvailable(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .name("Tool")
                .description("Excellent chainsaw")
                .isAvailable(true)
                .owner(owner)
                .build();

        Item item3 = Item.builder()
                .name("Hammer")
                .description("Heavy")
                .isAvailable(false)
                .owner(owner)
                .build();

        Item item4 = Item.builder()
                .name("Axe")
                .description("Dangerous")
                .isAvailable(true)
                .owner(owner)
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.persist(item4);
        entityManager.flush();

        List<Item> result = itemRepository.findByText("ChaInsAW");

        assertThat(result)
                .hasSize(2)
                .extracting(Item::getName)
                .containsExactlyInAnyOrder("Chainsaw", "Tool");
        assertThat(result).noneMatch(item -> item.getName().equals("Hammer") || item.getName().equals("Axe"));
    }

    @Test
    void findByText_ShouldReturnEmptyList() {
        Item item = Item.builder()
                .name("Hammer")
                .description("Heavy")
                .isAvailable(true)
                .owner(owner)
                .build();

        entityManager.persistAndFlush(item);

        List<Item> result = itemRepository.findByText("Chainsaw");

        assertThat(result).isEmpty();
    }
}
