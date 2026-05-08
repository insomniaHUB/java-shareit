package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UsedEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Map<Long, User> userStorage = new HashMap<>();
    private final UserMapper mapper;
    private Long id = 0L;

    public UserDto getUserById(Long id) {
        checkUserExisting(id);
        User user = userStorage.get(id);
        return mapper.toUserDto(user);
    }

    public List<UserDto> getUsers() {
        return userStorage.values().stream()
                .map(mapper::toUserDto)
                .toList();
    }

    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        isEmailUsed(userDto);
        User user = mapper.toUser(userDto);
        id += 1L;
        user.setId(id);
        userStorage.put(id, user);

        return mapper.toUserDto(user);
    }

    public UserDto changeUser(UserDto newUser, Long id) {
        checkUserExisting(id);
        User user = userStorage.get(id);

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            isEmailUsed(newUser);
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }

        return mapper.toUserDto(user);
    }

    public void deleteUser(Long id) {
        userStorage.remove(id);
    }

    public void checkUserExisting(Long userId) {
        User user = userStorage.get(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не был найден");
        }
    }

    private void validateUser(UserDto user) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()
                || user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Ошибка валидации пользователя");
        }
    }

    private void isEmailUsed(UserDto user) {
        List<String> emails = userStorage.values().stream()
                .map(User::getEmail)
                .toList();
        if (emails.contains(user.getEmail())) {
            throw new UsedEmailException("Пользователь с такой почтой уже существует");
        }
    }
}
