package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class RequestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestService requestService;

    @Test
    void createItemRequest() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder().userId(1L).description("I wanna that chainsaw").build();
        ItemRequestDto response = ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build();
        when(requestService.createItemRequest(any(ItemRequestDto.class), eq(1L))).thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("I wanna that chainsaw"));
    }

    @Test
    void getRequest() throws Exception {
        ItemRequestDto response = ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build();
        when(requestService.getRequest(1L, 1L)).thenReturn(response);

        mvc.perform(get("/requests/1")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUsersItemRequests() throws Exception {
        when(requestService.getUsersItemRequests(1L)).thenReturn(List.of(
                ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build(),
                ItemRequestDto.builder().id(2L).userId(2L).description("Meow").build()
        ));

        mvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getRequests() throws Exception {
        when(requestService.getRequests(1L)).thenReturn(List.of(
                ItemRequestDto.builder().id(1L).userId(1L).description("I wanna that chainsaw").build(),
                ItemRequestDto.builder().id(2L).userId(2L).description("Meow").build()
        ));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
