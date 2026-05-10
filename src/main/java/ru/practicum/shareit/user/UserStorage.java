package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UsedEmailException;
import ru.practicum.shareit.user.dto.UserChangeDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();
    private Long id = 0L;

    public User getUserById(Long id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }

        return user;
    }

    public Collection<User> getUsers() {
        return userStorage.values();
    }

    public User createUser(User user) {
        isEmailUsed(user.getEmail());

        id += 1L;
        user.setId(id);
        userStorage.put(id, user);

        return user;
    }

    public User changeUser(UserChangeDto newUser, Long id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            isEmailUsed(newUser.getEmail());
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }

        return user;
    }

    public void deleteUser(Long id) {
        userStorage.remove(id);
    }

    private void isEmailUsed(String email) {
        userStorage.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findAny()
                .ifPresent(u -> {
                    throw new UsedEmailException("Пользователь с такой почтой уже существует");
                });
    }
}
