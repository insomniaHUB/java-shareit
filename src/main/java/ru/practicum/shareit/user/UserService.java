package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UsedEmailException;
import ru.practicum.shareit.user.dto.UserChangeDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;

    public UserDto getUserById(Long id) {
        return mapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден")));
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .toList();
    }

    public UserDto createUser(UserCreateDto userDto) {
        isEmailUsed(userDto.getEmail());
        User user = mapper.toUserFromCreate(userDto);

        return mapper.toUserDto(userRepository.save(user));
    }

    public UserDto changeUser(UserChangeDto newUser, Long id) {
        isEmailUsed(newUser.getEmail());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }

        return mapper.toUserDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void isEmailUsed(String email) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            throw new UsedEmailException("Пользователь с такой почтой уже существует");
        }
    }
}
