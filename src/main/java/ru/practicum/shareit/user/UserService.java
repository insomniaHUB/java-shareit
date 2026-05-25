package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserChangeDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserStorage userStorage;

    public UserChangeDto getUserById(Long id) {
        return mapper.toUserChangeDto(userStorage.getUserById(id));
    }

    public List<UserChangeDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(mapper::toUserChangeDto)
                .toList();
    }

    public UserCreateDto createUser(UserCreateDto userDto) {
        User user = mapper.toUserFromCreate(userDto);

        return mapper.toUserCreateDto(userStorage.createUser(user));
    }

    public UserChangeDto changeUser(UserChangeDto newUser, Long id) {
        return mapper.toUserChangeDto(userStorage.changeUser(newUser, id));
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
}
