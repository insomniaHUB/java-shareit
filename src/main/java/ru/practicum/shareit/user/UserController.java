package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserChangeDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserChangeDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserChangeDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserCreateDto createUser(@Valid @RequestBody UserCreateDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserChangeDto changeUser(@Valid @RequestBody UserChangeDto user,
                                    @PathVariable Long userId) {
        return userService.changeUser(user, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
