package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_success() {
        UserDto userDto = UserDto.builder().name("Anton").email("antonio@gmail.com").build();
        when(userRepository.findByEmail("antonio@gmail.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(1L).name("Anton").email("antonio@gmail.com").build());

        UserDto result = userService.createUser(userDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Anton");
        assertThat(result.getEmail()).isEqualTo("antonio@gmail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changeUser_success() {
        User user = User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        UserDto userDto = UserDto.builder().name("Anton").email("antonio@gmail.com").build();
        when(userRepository.findByEmail("antonio@gmail.com")).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.changeUser(userDto, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Anton");
        assertThat(result.getEmail()).isEqualTo("antonio@gmail.com");
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build()));

        UserDto result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Kirill");
        assertThat(result.getEmail()).isEqualTo("kirill@gmail.com");
    }

    @Test
    void getUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(User.builder().id(1L).name("Kirill").email("kirill@gmail.com").build(),
                User.builder().name("Anton").email("antonio@gmail.com").build()));

        List<UserDto> result = userService.getUsers();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getName()).isEqualTo("Kirill");
    }
}
