package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void createUser() throws Exception {
        UserDto request = UserDto.builder().name("Kirill").email("kirill@gmail.com").build();
        UserDto response = UserDto.builder().id(1L).name("Kirill").email("kirill@gmail.com").build();
        when(userService.createUser(any(UserDto.class))).thenReturn(response);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Kirill"))
                .andExpect(jsonPath("$.email").value("kirill@gmail.com"));
    }

    @Test
    void changeUser() throws Exception {
        UserDto request = UserDto.builder().name("Anton").email("kirill@gmail.com").build();
        UserDto response = UserDto.builder().id(1L).name("Anton").email("kirill@gmail.com").build();
        when(userService.changeUser(any(UserDto.class), eq(1L))).thenReturn(response);

        mvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Anton"));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(UserDto.builder().id(1L).name("Kirill").email("kirill@gmail.com").build());

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Kirill"))
                .andExpect(jsonPath("$.email").value("kirill@gmail.com"));
    }

    @Test
    void getUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(
                UserDto.builder().name("Kirill").email("kirill@gmail.com").build(),
                UserDto.builder().id(1L).name("Anton").email("anton@gmail.com").build()
        ));

        mvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Kirill"))
                .andExpect(jsonPath("$[1].name").value("Anton"));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}
