package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_WhenUserExists() {
        User user = User.builder()
                .name("Кирилл")
                .email("kirill@gmail.com")
                .build();

        User savedUser = entityManager.persistAndFlush(user);

        User foundUser = userRepository.findByEmail("kirill@gmail.com");

        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getName()).isEqualTo("Кирилл");
        assertThat(foundUser.getEmail()).isEqualTo("kirill@gmail.com");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist() {
        User foundUser = userRepository.findByEmail("notfound@gmail.com");
        assertThat(foundUser).isNull();
    }
}
