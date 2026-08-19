package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemServiceImpl itemService;

    @Test
    void createItem() throws Exception {
        ItemDto request = ItemDto.builder().name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        ItemDto response = ItemDto.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        when(itemService.createItem(any(ItemDto.class), eq(1L))).thenReturn(response);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chainsaw"))
                .andExpect(jsonPath("$.description").value("Very cool chainsaw"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createComment() throws Exception {
        CommentDto request = CommentDto.builder().text("Awesome chainsaw").authorName("Kirill").build();
        CommentDto response = CommentDto.builder().id(1L).text("Awesome chainsaw").authorName("Kirill").build();
        when(itemService.createComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(response);

        mvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Awesome chainsaw"))
                .andExpect(jsonPath("$.authorName").value("Kirill"));
    }

    @Test
    void changeItem() throws Exception {
        ItemDto request = ItemDto.builder().name("Pickaxe").description("Very cool pickaxe").isAvailable(true).build();
        ItemDto response = ItemDto.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        when(itemService.changeItem(eq(1L), any(ItemDto.class), eq(1L))).thenReturn(response);

        mvc.perform(patch("/items/1")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chainsaw"))
                .andExpect(jsonPath("$.description").value("Very cool chainsaw"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemById() throws Exception {
        ItemGetDto response = ItemGetDto.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build();
        when(itemService.getItemById(1L, 1L)).thenReturn(response);

        mvc.perform(get("/items/1")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chainsaw"))
                .andExpect(jsonPath("$.description").value("Very cool chainsaw"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(1L)).thenReturn(List.of(
                ItemBookingsDto.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build(),
                ItemBookingsDto.builder().id(2L).name("Pickaxe").description("Very cool pickaxe").isAvailable(true).build()
        ));

        mvc.perform(get("/items")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getItemByText() throws Exception {
        when(itemService.getItemByText("Chainsaw")).thenReturn(List.of(
                ItemDto.builder().id(1L).name("Chainsaw").description("Very cool chainsaw").isAvailable(true).build()
        ));

        mvc.perform(get("/items/search")
                .param("text", "Chainsaw")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Chainsaw"));
    }
}
