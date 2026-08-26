package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemChangeDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient itemClient;

    @Test
    void getItemById() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItems() throws Exception {
        when(itemClient.getItems(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void changeItem() throws Exception {
        ItemChangeDto validDto = ItemChangeDto.builder().name("New Name").description("New Description").isAvailable(true).build();

        when(itemClient.changeItem(anyLong(), any(ItemChangeDto.class), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createItem() throws Exception {
        ItemCreateDto validDto = ItemCreateDto.builder().name("Chainsaw").description("Very cool").isAvailable(true).build();

        when(itemClient.createItem(any(ItemCreateDto.class), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_ShouldReturnBadRequest() throws Exception {
        ItemCreateDto invalidDto = ItemCreateDto.builder().name("").description("Very cool").isAvailable(true).build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_ShouldReturnBadRequest() throws Exception {
        CommentDto invalidComment = new CommentDto("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemByText() throws Exception {
        when(itemClient.getItemByText("text", 1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "text"))
                .andExpect(status().isOk());
    }
}