package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserChangeDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

@Component
public class UserMapper {
    public UserChangeDto toUserChangeDto(User user) {
        return new UserChangeDto(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public UserCreateDto toUserCreateDto(User user) {
        return new UserCreateDto(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public User toUserFromCreate(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    public User toUserFromChange(UserChangeDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }
}
