package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemCreateDtoJsonTest {
    private final JacksonTester<ItemCreateDto> json;

    @Test
    void testItemCreateDto() throws Exception {
        ItemCreateDto itemDto = ItemCreateDto.builder()
                .name("Chainsaw")
                .description("Very cool chainsaw")
                .isAvailable(true)
                .requestId(1L)
                .build();

        JsonContent<ItemCreateDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Chainsaw");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Very cool chainsaw");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}