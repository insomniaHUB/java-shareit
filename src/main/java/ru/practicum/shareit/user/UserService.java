package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UsedEmailException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserStorage userStorage;


    public UserDto getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }

        return mapper.toUserDto(user);
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(mapper::toUserDto)
                .toList();
    }

    public UserDto createUser(User user) {
        isEmailUsed(user);

        return mapper.toUserDto(userStorage.createUser(user));
    }

    public UserDto changeUser(UserDto newUser, Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            isEmailUsed(mapper.toUser(newUser));
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }

        return mapper.toUserDto(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void checkUserExisting(Long userId) {
        User user = userStorage.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }
    }

    private void isEmailUsed(User user) {
        userStorage.getUsers().stream()
                .filter(u -> user.getEmail().equals(u.getEmail()))
                .findAny()
                .ifPresent(u -> { throw new UsedEmailException("Пользователь с такой почтой уже существует"); });
    }
}
