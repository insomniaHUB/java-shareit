package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserChangeDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserClient userClient;

    @Test
    void getUserById() throws Exception {
        when(userClient.getUserById(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsers() throws Exception {
        when(userClient.getUsers(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsers_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser() throws Exception {
        UserCreateDto validDto = UserCreateDto.builder().name("Anton").email("anton@gmail.com").build();

        when(userClient.createUser(any(UserCreateDto.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_ShouldReturnBadRequest() throws Exception {
        UserCreateDto invalidDto = UserCreateDto.builder().name("Anton").email("aaaaaaaa").build();

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeUser() throws Exception {
        UserChangeDto validDto = UserChangeDto.builder().name("Anton").email("anton@gmail.com").build();

        when(userClient.changeUser(any(UserChangeDto.class), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void changeUser_ShouldReturnBadRequest() throws Exception {
        UserChangeDto invalidDto = UserChangeDto.builder().name("Anton").email("aaaaaaaaa").build();

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser() throws Exception {
        when(userClient.deleteUser(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}