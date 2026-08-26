package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createItemRequest() throws Exception {
        ItemRequestDto validDto = ItemRequestDto.builder().description("Need a drill").build();

        when(itemRequestClient.createItemRequest(any(ItemRequestDto.class), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createItemRequest_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto invalidDto = ItemRequestDto.builder().description("").build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersItemRequests() throws Exception {
        when(itemRequestClient.getUsersItemRequests(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequests() throws Exception {
        when(itemRequestClient.getRequests(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestClient.getRequest(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}