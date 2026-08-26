package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findByRequestorIdOrderByCreatedDesc() {
        User requestor = User.builder().name("Kirill").email("kirill@gmail.com").build();
        User user = User.builder().name("Ivan").email("ivan@gmail.com").build();

        entityManager.persist(requestor);
        entityManager.persist(user);

        LocalDateTime now = LocalDateTime.now();

        ItemRequest oldestRequest = ItemRequest.builder()
                .description("Need an older chainsaw")
                .requestor(requestor)
                .created(now.minusDays(2))
                .build();

        ItemRequest newestRequest = ItemRequest.builder()
                .description("Need a super new laptop")
                .requestor(requestor)
                .created(now)
                .build();

        ItemRequest middleRequest = ItemRequest.builder()
                .description("Need a medium drill")
                .requestor(requestor)
                .created(now.minusDays(1))
                .build();

        ItemRequest otherRequest = ItemRequest.builder()
                .description("Someone else request")
                .requestor(user)
                .created(now)
                .build();

        entityManager.persist(oldestRequest);
        entityManager.persist(newestRequest);
        entityManager.persist(middleRequest);
        entityManager.persist(otherRequest);
        entityManager.flush();

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDescription()).isEqualTo("Need a super new laptop");
        assertThat(result.get(1).getDescription()).isEqualTo("Need a medium drill");
        assertThat(result.get(2).getDescription()).isEqualTo("Need an older chainsaw");
        assertThat(result).noneMatch(req -> req.getDescription().equals("Someone else request"));
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc_ShouldReturnEmptyList() {
        User user = User.builder().name("Anton").email("anton@gmail.com").build();
        entityManager.persistAndFlush(user);

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());

        assertThat(result).isEmpty();
    }
}
