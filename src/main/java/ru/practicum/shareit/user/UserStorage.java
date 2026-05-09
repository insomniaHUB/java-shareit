package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();
    private Long id = 0L;

    public User getUserById(Long id) {
        return userStorage.get(id);
    }

    public Collection<User> getUsers() {
        return userStorage.values();
    }

    public User createUser(User user) {
        id += 1L;
        user.setId(id);
        userStorage.put(id, user);

        return user;
    }

    public void deleteUser(Long id) {
        userStorage.remove(id);
    }
}
